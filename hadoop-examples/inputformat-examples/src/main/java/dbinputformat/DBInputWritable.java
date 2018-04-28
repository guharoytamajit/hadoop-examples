package dbinputformat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class DBInputWritable implements Writable, DBWritable {
	private int USER_ID;
	private String USERNAME, CREATED_BY;

	public void readFields(DataInput in) throws IOException {
	}

	public void readFields(ResultSet rs) throws SQLException
	// Resultset object represents the data returned from a SQL statement
	{
		USER_ID = rs.getInt(1);
		USERNAME = rs.getString(2);
		CREATED_BY = rs.getString(3);
	}

	public void write(DataOutput out) throws IOException {
	}

	public void write(PreparedStatement ps) throws SQLException {
		ps.setInt(1, USER_ID);
		ps.setString(2, USERNAME);
		ps.setString(3, CREATED_BY);
	}

	public int getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(int uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public String getCREATED_BY() {
		return CREATED_BY;
	}

	public void setCREATED_BY(String cREATED_BY) {
		CREATED_BY = cREATED_BY;
	}
	
	

}
