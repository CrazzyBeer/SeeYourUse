package com.SeeYourUse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

public class SQLiteDB {
	public Connection c = null;
	public Statement stmt = null;
	public PreparedStatement pstmt = null;
	public ResultSet rs = null;

	public SQLiteDB() {

	}

	private void connect() {

		try {

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/DB/SeeYourUse.db");
			

		} catch (Exception e) {
			//System.err.println("Couldn't connect to the DB");
			e.printStackTrace();
		} finally {
			Logger log = new Logger();
			if (c==null)
				try {
					log.log("null\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else {
				try {
					log.log("OK\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// System.out.println("DB Opened successfully");
	}

	/**
	 * Closes the Connection, Statement, PreparedStatement and ResultSet
	 * Exceptions are ignored
	 */
	private void close() {

		try {

			if (c != null)
				c.close();
			if (stmt != null)
				stmt.close();
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();

		} catch (Exception e) {
			/* Ignored */
		}

	}

	
	/**
	 * Checks whether the table MESSAGES already exists IF not, creates it
	 */
	public void init() {
		
		connect();

		createMessages();

		createPagination();

		createSearchOption();

		close();

	}

	/**
	 * Creates the table messages or looks if it is created
	 */
	public void createMessages() {

		connect();

		try {

			// Statement
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS MESSAGES " + "(ID INTEGER PRIMARY KEY   AUTOINCREMENT,"
					+ " CONTENT        TEXT    NOT NULL, " + " DATE      TEXT  NOT NULL, "
					+ " POINTS INTEGER NOT NULL)";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			close();

		}

	}

	/**
	 * Creates the table pagination or looks if it is created Adds default
	 * pagination if the table was just created
	 */
	public void createPagination() {

		connect();

		try {

			// Statement
			stmt = c.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS PAGINATION " + "(ID INTEGER PRIMARY KEY, "
					+ " PER_PAGE INTEGER NOT NULL DEFAULT 50)";

			stmt.executeUpdate(sql);

			/*
			 * CHECKS IF TABLE PAGINATION HAS ANY ROWS IF NOT, ADDS ONE ROW
			 * CONTAINING THE DEFAULT NUMBER OF MESSAGES PER PAGE
			 * 
			 * Selects all the rows, returns the number representing the number
			 * of rows
			 */
			sql = "SELECT COUNT(*) AS rowcount FROM PAGINATION;";

			// ResultSet
			rs = stmt.executeQuery(sql);
			int count = 0;

			rs.next();

			count = rs.getInt("rowcount");

			// Checks the number of rows, if it is 0, inserts the default row
			if (count == 0) {

				sql = "INSERT INTO PAGINATION (ID, PER_PAGE) " + "VALUES (1, 50);";

				stmt.executeUpdate(sql);

			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			close();

		}

	}

	/**
	 * Creates the table search_option or looks if it is created Adds default
	 * search option if the table was just created Integer value "OPTION" gets
	 * values 0 - search only in message content 1 - search in message content,
	 * message date and message points
	 */
	public void createSearchOption() {

		connect();

		try {

			// Statement
			stmt = c.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS SEARCH_OPTION " + "(ID INTEGER PRIMARY KEY, "
					+ " OPTION INTEGER NOT NULL DEFAULT 0)";

			stmt.executeUpdate(sql);

			/*
			 * CHECKS IF TABLE SEARCH_OPTION HAS ANY ROWS IF NOT, ADDS ONE ROW
			 * CONTAINING THE DEFAULT SEARCH OPTION
			 * 
			 * Selects all the rows, returns the number representing the number
			 * of rows
			 */
			sql = "SELECT COUNT(*) AS rowcount FROM SEARCH_OPTION;";

			// ResultSet
			rs = stmt.executeQuery(sql);
			int count = 0;

			rs.next();

			count = rs.getInt("rowcount");

			// Checks the number of rows, if it is 0, inserts the default row
			if (count == 0) {

				sql = "INSERT INTO SEARCH_OPTION (ID, OPTION) " + "VALUES (1, 0);";

				stmt.executeUpdate(sql);

			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {

			close();

		}
	}

	/**
	 * Drops the given table
	 * 
	 * @param table
	 */
	public void drop(String table) {

		connect();

		try {

			String sql = "DROP TABLE " + table + ";";

			// PreparedStatement
			pstmt = c.prepareStatement(sql);

			pstmt.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			close();

		}

	}

	/**
	 * Instead of truncating, this function drops the db And creates another
	 * This is for reseting of auto-increment variables
	 */
	public void truncate(String table) {

		drop(table);
		init();

	}

	/**
	 * Inserts the given record to the database
	 * 
	 * @param rec
	 */
	public void insert(Record rec) {

		connect();

		try {

			c.setAutoCommit(false);

			String sql = "INSERT INTO MESSAGES (ID, CONTENT, DATE, POINTS)" + "VALUES (NULL, ?, ?, ?);";

			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, rec.getContent());
			pstmt.setString(2, rec.getDate());
			pstmt.setInt(3, rec.getPoints());

			pstmt.executeUpdate();
			c.commit();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}
	}

	/**
	 * Delete the given rec from the database by it's ID
	 * 
	 * @param rec
	 */
	public void delete(Record rec) {

		connect();

		try {

			c.setAutoCommit(false);

			String sql = "DELETE FROM MESSAGES WHERE ID = ?";

			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			pstmt.setInt(1, rec.getId());
			pstmt.executeUpdate();
			pstmt.close();
			c.commit();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

	}

	/**
	 * Gets the amount of rows in MESSAGES Table
	 * 
	 * @return size
	 */
	public int size() {
		int size = 0;

		connect();

		try {

			// Statement
			stmt = c.createStatement();
			// ResultSet
			rs = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM MESSAGES;");

			// Getting the rowcount from the resultSET
			rs.next();
			size = rs.getInt("rowcount");

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		return size;
	}

	/**
	 * Selects from the database all the records
	 * 
	 * @return messages
	 */
	public Vector<Record> selectMessages() {
		Vector<Record> messages = new Vector<Record>();

		connect();

		try {

			// Statement
			stmt = c.createStatement();

			// ResultSet
			rs = stmt.executeQuery("SELECT * FROM MESSAGES ORDER BY DATE;");

			while (rs.next()) {
				Record rec = new Record(rs.getString("content"), rs.getString("date"), rs.getInt("points"),
						rs.getInt("ID"));
				messages.addElement(rec);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		return messages;
	}

	/**
	 * Selects from the database records which are between date1 and date2
	 * 
	 * @param date1
	 * @param date2
	 * @return messages
	 */
	public Vector<Record> selectBetweenDates(String date1, String date2) {
		Vector<Record> messages = new Vector<Record>();

		connect();

		try {

			String sql = "SELECT * FROM MESSAGES WHERE DATE BETWEEN ? AND ? ORDER BY DATE;";
			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, date1);
			pstmt.setString(2, date2);

			// ResultSet
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Record rec = new Record(rs.getString("content"), rs.getString("date"), rs.getInt("points"),
						rs.getInt("ID"));
				messages.addElement(rec);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		return messages;
	}

	/**
	 * Builds the search query according to the option
	 * 
	 * @param option
	 *            - integer value from 0 to 1 0 - search only in CONTENT 1 -
	 *            search in all the fields of the message
	 * @return searchQuery - the resulting string
	 */
	public String getSearchQuery(int option) {

		String searchQuery = null;

		if (option == 0) {

			searchQuery = "SELECT * FROM MESSAGES WHERE CONTENT LIKE ? ORDER BY DATE";

		} else if (option == 1) {

			searchQuery = "SELECT * FROM MESSAGES WHERE (CONTENT LIKE ?) OR (DATE LIKE ?) OR (POINTS LIKE ?) ORDER BY DATE;";

		}

		return searchQuery;
	}

	/**
	 * Builds the search statement based on the search option given by the user
	 * If the OPTION is 0 - gives a query to search only in content Else,
	 * searches is content, date and points
	 */
	public void makeSearchStatement(int option, String query) {

		// gets the searchQuery
		String searchQuery = getSearchQuery(option);

		try {
			// PreparedStatement
			pstmt = c.prepareStatement(searchQuery);

			/*
			 * Fill the statement in dependence of the option IF it is 0 - there
			 * is only on string to fill IF it is 1 - there are 3 strings to
			 * fill
			 */
			if (option == 0) {

				pstmt.setString(1, "%" + query + "%");

			} else if (option == 1) {

				pstmt.setString(1, "%" + query + "%");
				pstmt.setString(2, "%" + query + "%");
				pstmt.setString(3, "%" + query + "%");

			}

		} catch (SQLException e) {

			e.printStackTrace();

		}

	}

	/**
	 * Searches in the database for records which content has occurrences of
	 * "query"
	 * 
	 * @param query
	 * @return messages
	 */
	public Vector<Record> find(String query) {
		Vector<Record> messages = new Vector<Record>();

		int searchOption = getSearchOption();
		connect();

		try {

			makeSearchStatement(searchOption, query);

			// ResultSet
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Record rec = new Record(rs.getString("content"), rs.getString("date"), rs.getInt("points"),
						rs.getInt("ID"));
				messages.addElement(rec);
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		// System.out.println("Search done");
		return messages;
	}

	/**
	 * Selects the number of messages per page from the db
	 * 
	 * @return
	 */
	public int getPerPage() {
		int pages = 50;

		connect();

		try {

			String sql = "SELECT * FROM PAGINATION;";

			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			// ResultSet

			rs = pstmt.executeQuery();

			// The ResultSet contains only one row so we can simply select it
			// without any loops
			rs.next();
			pages = rs.getInt("PER_PAGE");

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		return pages;
	}

	/**
	 * Sets the new number of messages per page
	 * 
	 * @param perPage
	 *            - the new perPage parameter that will be saved to the database
	 */
	public void setPerPage(int perPage) {

		connect();

		try {

			String sql = "UPDATE PAGINATION SET PER_PAGE = ? WHERE ID = 1;";
			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			pstmt.setInt(1, perPage);

			pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}
	}

	/**
	 * Gets from the database the search option
	 * 
	 * @return searchOption
	 */
	public int getSearchOption() {
		int searchOption = 0;

		connect();

		try {

			String sql = "SELECT * FROM SEARCH_OPTION;";
			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			// ResultSet
			rs = pstmt.executeQuery();

			// The ResultSet contains only one row so we can simply select it
			// without any loops
			rs.next();
			searchOption = rs.getInt("OPTION");

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}

		return searchOption;
	}

	/**
	 * Sets the preferred search option and saves it to the database
	 * 
	 * @param option
	 */
	public void setSearchOption(int option) {

		connect();

		try {

			String sql = "UPDATE SEARCH_OPTION SET OPTION = ? WHERE ID = 1;";

			// PreparedStatement
			pstmt = c.prepareStatement(sql);
			pstmt.setInt(1, option);

			pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			close();

		}
	}

	/**
	 * Populates the database with records with random dates
	 * 
	 * @param count
	 */
	public void populate(int count) {
		Random rand = new Random();

		for (int i = 0; i < count; i++) {
			int randDay = rand.nextInt(30) + 1;
			int randPoints = rand.nextInt(100);

			Calendar randDate = Calendar.getInstance();
			randDate.set(Calendar.DAY_OF_MONTH, randDay);

			String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(randDate.getTime());

			Record rec = new Record("Sample Content", dateNow, randPoints);

			insert(rec);
		}

	}
}
