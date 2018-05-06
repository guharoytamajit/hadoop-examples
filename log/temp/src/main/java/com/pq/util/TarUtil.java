package com.pq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class TarUtil {

	private static boolean removeJunkPath = false;
	private static Configuration conf = new Configuration();
	private static FileSystem fileSystem;

	public TarUtil() throws IOException {
		// fileSystem = FileSystem.newInstance(conf);
		// conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		fileSystem = FileSystem.get(conf);
	}

	/**
	 * Create a new Tar from a root directory
	 * 
	 * @param directory
	 *            the base directory
	 * @param filename
	 *            the output filename
	 * @param absolute
	 *            store absolute filepath (from directory) or only filename
	 * @return True if OK
	 */
	public static boolean createTarFromDirectory(String directory, String filename, boolean absolute)
			throws IOException {

		// recursive call
		TarArchiveOutputStream taos;
		try {
			FSDataOutputStream out = fileSystem.create(new Path(filename));
			taos = new TarArchiveOutputStream(out);

		} catch (FileNotFoundException e) {
			return false;
		}
		taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		try {
			recurseFiles(new Path(directory), new Path(directory), taos, absolute);
		} catch (IOException e2) {
			try {
				taos.close();
			} catch (IOException e) {
				// ignore
			}
			return false;
		}
		try {
			taos.finish();
		} catch (IOException e1) {
			// ignore
		}
		try {
			taos.flush();
		} catch (IOException e) {
			// ignore
		}
		try {
			taos.close();
		} catch (IOException e) {
			// ignore
		}
		return true;
	}

	/**
	 * Recursive traversal to add files
	 * 
	 * @param root
	 * @param file
	 * @param taos
	 * @param absolute
	 * @throws IOException
	 */
	private static void recurseFiles(Path root, Path file, TarArchiveOutputStream taos, boolean absolute)
			throws IOException {

		if (fileSystem.isDirectory(file)) {
			// recursive call
			FileStatus[] files = fileSystem.listStatus(file);

			for (FileStatus file2 : files) {
				Path filePath_obj = new Path(file2.getPath().toString());
				recurseFiles(root, filePath_obj, taos, absolute);
			}
		} else if ((!file.getName().endsWith(".tar")) && (!file.getName().endsWith(".TAR"))) {
			String filename = null;
			if (absolute) {
				String root_uri = root.makeQualified(fileSystem).toString() + "/";
				filename = file.toString().substring(root_uri.length());
			} else {
				filename = file.getName();
			}
			TarArchiveEntry tae = new TarArchiveEntry(filename);
			tae.setSize(fileSystem.getLength(file));
			taos.putArchiveEntry(tae);
			FSDataInputStream fis = fileSystem.open(file);
			IOUtils.copy(fis, taos);
			taos.closeArchiveEntry();
		}
	}

	/**
	 * Create a new Tar from a list of Files (only name of files will be used)
	 * 
	 * @param files
	 *            list of files to add
	 * @param filename
	 *            the output filename
	 * @return True if OK
	 */
	public static boolean createTarFromFiles(List<FileStatus> files, String filename) throws IOException {
		return createTarFromFiles(files.toArray(new FileStatus[] {}), filename);
	}

	/**
	 * Create a new Tar from an array of Files (only name of files will be used)
	 * 
	 * @param files
	 *            array of files to add
	 * @param filename
	 *            the output filename
	 * @return True if OK
	 */
	public static boolean createTarFromFiles(FileStatus[] files, String filename) throws IOException {
		// recursive call
		TarArchiveOutputStream taos;
		try {
			FSDataOutputStream out = fileSystem.create(new Path(filename));
			taos = new TarArchiveOutputStream(out);
		} catch (FileNotFoundException e) {
			return false;
		}
		taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		for (FileStatus file : files) {
			try {
				addFile(file, taos);
			} catch (IOException e) {
				try {
					taos.close();
				} catch (IOException e1) {
					// ignore
				}
				return false;
			}
		}
		try {
			taos.finish();
		} catch (IOException e1) {
			// ignore
		}
		try {
			taos.flush();
		} catch (IOException e) {
			// ignore
		}
		try {
			taos.close();
		} catch (IOException e) {
			// ignore
		}
		return true;
	}

	/**
	 * Recursive traversal to add files
	 * 
	 * @param file
	 * @param taos
	 * @throws IOException
	 */
	private static void addFile(FileStatus file, TarArchiveOutputStream taos) throws IOException {
		String filename = null;
		filename = file.getPath().getName();
		TarArchiveEntry tae = new TarArchiveEntry(filename);
		tae.setSize(fileSystem.getLength(file.getPath()));
		taos.putArchiveEntry(tae);
		FSDataInputStream fis = fileSystem.open(file.getPath());
		IOUtils.copy(fis, taos);
		taos.closeArchiveEntry();
	}

	/**
	 * Extract all files from Tar into the specified directory
	 * 
	 * @param tarFile
	 * @param directory
	 * @return the list of extracted filenames
	 * @throws IOException
	 */
	public static List<String> unTar(String tarFile, String untared_directory) throws IOException {
		List<String> result = new ArrayList<String>();
		File directory = new File(untared_directory);

		Path new_unTared_FilePath;

		FSDataInputStream inputStream = fileSystem.open(new Path(tarFile));
		TarArchiveInputStream in = new TarArchiveInputStream(inputStream);
		TarArchiveEntry entry = in.getNextTarEntry();
		while (entry != null) {
			if (entry.isDirectory()) {
				entry = in.getNextTarEntry();
				continue;
			}
			File curfile = new File(directory, entry.getName());
			File parent = curfile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}

			String new_file_name = curfile.toString();

			// remove directory structure.
			if (removeJunkPath) {
				new_file_name = curfile.toString().replace(untared_directory, "").replaceAll("[^A-Za-z0-9.]", "_")
						.replaceAll("^_+", "");
			}

			FSDataOutputStream out = fileSystem.create(new Path(untared_directory, new_file_name));

			try {
				IOUtils.copy(in, out);
				out.close();

			} catch (IOException e) {
				System.out.println(
						"Failed to untar the file ~[" + tarFile + "] due to the exception =>[" + e.getMessage() + "]");

			}

			new_unTared_FilePath = new Path(untared_directory, new_file_name);

			result.add((fileSystem.getFileStatus(new_unTared_FilePath).getPath()).toString());
			entry = in.getNextTarEntry();
		}
		in.close();
		return result;
	}

	public static List<String> unTar(String tarFile, String untared_directory, boolean removeDirStructure)
			throws IOException {
		if (removeDirStructure) {
			removeJunkPath = removeDirStructure;
		}
		return unTar(tarFile, untared_directory);
	}

	public static List<String> uncompressTarGZ(String tarFile, String untared_directory) throws IOException {
		List<String> result = new ArrayList<String>();
		List<String> b_result = new ArrayList<String>();
		File directory = new File(untared_directory);

		Path new_unTared_FilePath;

		FSDataInputStream inputStream = fileSystem.open(new Path(tarFile));
		TarArchiveInputStream in = null;
		in = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(inputStream)));
		TarArchiveEntry entry = in.getNextTarEntry();
		while (entry != null) {
			try {
				if (entry.isDirectory()) {
					entry = in.getNextTarEntry();
					continue;
				}
				File curfile = new File(directory, entry.getName());

				File parent = curfile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}

				String new_file_name = curfile.toString();
				// remove directory structure.
				if (removeJunkPath) {
					new_file_name = curfile.toString().replace(untared_directory, "").replaceAll("[^A-Za-z0-9.]", "_")
							.replaceAll("^_+", "");
				}

				FSDataOutputStream out = fileSystem.create(new Path(untared_directory, new_file_name));
				IOUtils.copy(in, out);
				out.close();

				new_unTared_FilePath = new Path(untared_directory, new_file_name);
				result.add((fileSystem.getFileStatus(new_unTared_FilePath).getPath()).toString());

				entry = in.getNextTarEntry();

			} catch (IOException e) {
				System.out.println(
						"Failed to untar the file ~[" + tarFile + "] due to the exception =>[" + e.getMessage() + "]");
				return b_result;
			}

		}
		in.close();
		return result;
	}

	public static List<String> uncompressTarGZ(String tarFile, String untared_directory, boolean removeDirStructure)
			throws IOException {
		if (removeDirStructure) {
			removeJunkPath = removeDirStructure;
		}
		return uncompressTarGZ(tarFile, untared_directory);
	}

}
