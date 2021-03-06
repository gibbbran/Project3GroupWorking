package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import academic.AcademicCollection;
import academic.AcademicRecord;
import employment.Employer;
import employment.EmployerCollection;
import student.Student;

/**
 * This class contains methods to access the Student tables data.
 * 
 * @author mabraham
 * @author Brian Lloyd
 *
 */

public class StudentDB {

	private Connection mConnection;
	
	/**
	 * Modifies the data on a student
	 * 
	 * @param student
	 * @param columnName
	 * @param data
	 * @return Returns a message with success or failure.
	 */
	public String updateStudent(Student student, String columnName, String data) {

		if (mConnection == null) {
			try {
				mConnection = DataConnection.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String sql = "UPDATE Student SET `" + columnName + "` = ?  WHERE studentID = ?";
		// For debugging - System.out.println(sql);
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = mConnection.prepareStatement(sql);
			preparedStatement.setString(1, data);
			preparedStatement.setInt(2, Integer.parseInt(student.getID()));
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			return "Error updating student: " + e.getMessage();
		}
		return "Updated Student Successfully";
	}
	
	/**
	 * Adds a new student to the Student table.
	 * 
	 * @param student
	 * @return Returns "Added Student Successfully" or "Error adding student: " with
	 *         the sql exception.
	 */
	public String addStudent(Student student) {
		String sql = "INSERT INTO Student(firstName, lastName) values "
				+ "(?, ?); ";

		if (mConnection == null) {
			try {
				mConnection = DataConnection.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = mConnection.prepareStatement(sql);
			preparedStatement.setString(1, student.getFirstName());
			preparedStatement.setString(2, student.getLastName());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error adding student: " + e.getMessage();
		}
		return "Added Student Successfully";
	}

	/**
	 * Gets  all Students from the student tables
	 * 
	 * @return Returns list of all students
	 * @throws SQLException
	 */
	public ArrayList<Student> getStudents() throws SQLException {
		if (mConnection == null) {
			try {
				mConnection = DataConnection.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Statement stmt = null;
		String query = "SELECT * " + "FROM Student";

		ArrayList<Student> students = new ArrayList<Student>();
		try {
			stmt = mConnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			students = buildStudent(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return students;
	}
	
	private ArrayList<Student> buildStudent(ResultSet rs) throws SQLException{
		ArrayList<Student> students = new ArrayList<Student>();
		while (rs.next()) {
			int id = rs.getInt("studentID");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			
			//Build object
			String stringID = Integer.toString(id);
			AcademicRecord record = AcademicCollection.getAcademicRecord(stringID);
			ArrayList<Employer> employers = EmployerCollection.getEmployers(stringID);
			
			Student student = new Student(stringID, firstName, lastName, record, employers);		
			students.add(student);
		}
		return students;
	}
	
	/**
	 * Gets  all Students from the student tables matching the first name and last name
	 * 
	 * @param firstName first name of student
	 * @param lastName last name of student
	 * 
	 * @return Returns list of all students
	 * @throws SQLException
	 */
	public ArrayList<Student> getStudents(String firstName, String lastName) throws SQLException {
		if (mConnection == null) {
			try {
				mConnection = DataConnection.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		PreparedStatement preparedStmt = null;
		String query = "SELECT * " + "FROM Student WHERE firstName = ? AND lastName = ?";

		ArrayList<Student> students = new ArrayList<Student>();
		try {
			preparedStmt = mConnection.prepareStatement(query);
			preparedStmt.setString(1, firstName);
			preparedStmt.setString(2, lastName);
			ResultSet rs = preparedStmt.executeQuery();
			
			students = buildStudent(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			if (preparedStmt != null) {
				preparedStmt.close();
			}
		}
		return students;
	}
	
	/**
	 * Gets a student from the DB using the unique UW email
	 * 
	 * @param uwEmail unique University of Washington email address
	 * 
	 * @return Returns a student with the email address
	 * @throws SQLException
	 */
	public Student getStudent(String uwEmail) throws SQLException {
		if (mConnection == null) {
			try {
				mConnection = DataConnection.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		PreparedStatement preparedStmt = null;
		String query = "SELECT Student.*, uwEmail FROM Student JOIN AcademicRecord ON Student.studentID = AcademicRecord.studentID WHERE uwEmail = ? ";

		Student student = null;
		try {
			preparedStmt = mConnection.prepareStatement(query);
			preparedStmt.setString(1, uwEmail);
			ResultSet rs = preparedStmt.executeQuery();
			
			ArrayList<Student> students = buildStudent(rs);
			if (students.size() > 0){
				student = students.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			if (preparedStmt != null) {
				preparedStmt.close();
			}
		}
		return student;
	}
}
