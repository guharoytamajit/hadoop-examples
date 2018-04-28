package custominputformat.titanic;

import java.io.DataInput;

import java.io.DataOutput;

import java.io.IOException;

import org.apache.hadoop.io.*;

import com.google.common.collect.ComparisonChain;

public class AliveGenderTuple implements WritableComparable<AliveGenderTuple> {
	private String alive;

	private String gender;

	public String getAlive() {
		return alive;
	}

	public void setAlive(String alive) {
		this.alive = alive;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void readFields(DataInput dataInput) throws IOException {
		alive = dataInput.readUTF();
		gender=dataInput.readUTF();
	}

	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(alive);
		dataOutput.writeUTF(gender);
	}

	public int compareTo(AliveGenderTuple obj) {
		return ComparisonChain.start().compare(this.alive,obj.alive).compare(this.gender,obj.gender).result();
	}

	public AliveGenderTuple(String alive, String gender) {
		super();
		this.alive = alive;
		this.gender = gender;
	}

	public AliveGenderTuple() {
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AliveGenderTuple)) {
			return false;
		}
		AliveGenderTuple other = (AliveGenderTuple) obj;
		return this.alive.equals(other.alive) && this.gender.equals(other.gender);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.alive + "|" + this.gender;
	}

}
