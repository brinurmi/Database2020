package Database_TermProject;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - The password for our Default Root user was changed to differentiate between:                  //
//              [Root: InitializeDB.jsp] AND [Root: Local MySQL Instance Admin, Workbench]              //
//      - The method closeAndDisconnectAll() is implemented to replace the methods                      //
//              resultSet.close() statement.close(), preparedStatement.close(), connect.disconnect().   //
//              with intent to eliminate guess work as to which SQL items need to be closed and when.   //
//                                                                                                      //
// IntelliJ Warning Suppression:                                                                     //
         @SuppressWarnings("SqlNoDataSourceInspection")                                                 //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/UserDAO")
public class UserDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	public UserDAO() {}

    /**
     * @see HttpServlet#HttpServlet()
     * User. and Pass. updated to match local server instance (MySQL Workbench)
     * Password changed to differentiate between:
     * Root: InitializeDB.jsp [AND] Root: Local MySQL Instance Admin
     */
    protected void connect_func() throws SQLException {
        if (connect == null || connect.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }

            connect = (Connection) DriverManager
                    .getConnection("jdbc:mysql://127.0.0.1:3306/TermProject?"
                            + "user=root&password=admin");
        }
    }


    private void closeAndDisconnectAll() throws SQLException {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connect != null) connect.close();
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
	

	public void initializeTable() throws SQLException {

        // !! !! CRITICAL: Search users by username or ID?? (or both?)
    	String SQL_clearExistingUsersTable = "DROP TABLE IF EXISTS users";

    	String SQL_tableUsers = "CREATE TABLE IF NOT EXISTS users" +
								   "(id INTEGER NOT NULL AUTO_INCREMENT, " +
								   "username varchar(30)," +
								   "password varchar(24)," +
								   "firstName varchar(50)," +
								   "lastName varchar(50)," +
								   "email varchar(50)," +
								   "PRIMARY KEY (id, username) );";

        String SQL_populateUserTable = "INSERT INTO users (username, password, firstName, lastName, email) values " +
                                                     "('user_0', 'pass_0', 'FName0', 'LName0', '0@email.com'), " +
                                                     "('user_1', 'pass_1', 'FName1', 'LName1', '1@email.com'), " +
                                                     "('user_2', 'pass_2', 'FName2', 'LName2', '2@email.com'), " +
                                                     "('user_3', 'pass_3', 'FName3', 'LName3', '3@email.com'), " +
                                                     "('user_4', 'pass_4', 'FName4', 'LName4', '4@email.com'), " +
                                                     "('user_5', 'pass_5', 'FName5', 'LName5', '5@email.com'), " +
                                                     "('user_6', 'pass_6', 'FName6', 'LName6', '6@email.com'), " +
                                                     "('user_7', 'pass_7', 'FName7', 'LName7', '7@email.com'), " +
                                                     "('user_8', 'pass_8', 'FName8', 'LName8', '8@email.com'), " +
                                                     "('user_9', 'pass_9', 'FName9', 'LName9', '9@email.com') ;" ;
		
    	connect_func();											            // Ensure active connection

		statement = connect.createStatement();

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");              // Disable foreign key constraints (req'd to drop tables w/ references)
		
		statement.executeUpdate(SQL_clearExistingUsersTable);               // Drop any preexisting Users table
		statement.executeUpdate(SQL_tableUsers);					    	// Establish new Table
		statement.executeUpdate(SQL_populateUserTable);			    	    // Populate Table w/ Predefined initial values

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");              // Re-enable foreign key constraints

        closeAndDisconnectAll();
	}
	

	public boolean validateLoginAttempt(String username, String password) throws SQLException{
		
		String SQL_findUserMatch = "SELECT * FROM users WHERE username = ? AND password = ?";
		
		connect_func();

		preparedStatement = connect.prepareStatement(SQL_findUserMatch);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, password);
		
		resultSet = preparedStatement.executeQuery();

        closeAndDisconnectAll();

		return resultSet.next();
	}


    public List<User> listAllUsers() throws SQLException {

        List<User> listUsers = new ArrayList<>();
        String sql = "SELECT * FROM users";      

        connect_func();

        statement =  (Statement) connect.createStatement();
        resultSet = statement.executeQuery(sql);
         
        while (resultSet.next()) {
            int id = resultSet.getInt("id");                                                // Extract data from each table row (i.e. Each user)
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			String firstName = resultSet.getString("firstName");
			String lastName = resultSet.getString("lastName");
			String email = resultSet.getString("email");
	
			User user = new User(id, username, password, firstName, lastName, email);       // Intermediate temp User obj
            listUsers.add(user);                                                            // Add the new temp User to the list
        }

        closeAndDisconnectAll();
        
        return listUsers;                                                                   // Return the list of Users
    }


    public boolean insert(User user) throws SQLException {
		
    	String SQL_insertUser = "INSERT INTO users(username, password, firstName, lastName, email) VALUES (?, ?, ?, ?, ?)";
  
		connect_func();                                                                     // Ensure active connection
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertUser);
		preparedStatement.setString(1, user.username);                                      // Set values of the User to be inserted
		preparedStatement.setString(2, user.password);
		preparedStatement.setString(3, user.firstName);
		preparedStatement.setString(4, user.lastName);
		preparedStatement.setString(5, user.email);
		
        boolean rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

        return rowInserted;                                                                 // Confirm INSERT of new User into table Users
    }


    public boolean delete(int usersid) throws SQLException {

        String SQL_deleteUser = "DELETE FROM users WHERE id = ?";
        
        connect_func();                                                                     // Ensure active connection
         
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteUser);   // Prepare the query with parameters
        preparedStatement.setInt(1, usersid);
         
        boolean rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        
        return rowDeleted;                                                                   // Confirm DELETE of User from table Users
    }


    public boolean update(User user) throws SQLException {

        String SQL_updateUser = "UPDATE users SET Name=?, Address =?,Status = ? where id = ?";
        
        connect_func();                                                                     // Ensure active connection
	
		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_updateUser);   // Prepare the query with parameters
		preparedStatement.setString(1, user.username);
		preparedStatement.setString(2, user.password);
		preparedStatement.setString(3, user.firstName);
		preparedStatement.setString(4, user.lastName);
		preparedStatement.setString(5, user.email);
         
        boolean rowUpdated = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        
        return rowUpdated;
    }


	public int retrieveUserID(String username, String password) throws SQLException {

	    String SQL_FindUserID = "SELECT * FROM users WHERE username = ? AND password = ?";

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_FindUserID);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        resultSet = preparedStatement.executeQuery();

        // CHECK: Is there a possibility of no ID found?
        // if (!resultSet.next()) { return -1 }

        closeAndDisconnectAll();
        return resultSet.getInt("id");
    }

    // !! !! CRITICAL: Search users by username or ID?? (or both?)
    public User getUser(int userID) throws SQLException {

        String SQL_findUserByID = "SELECT * FROM users WHERE id = ?";

        User user = null;

        connect_func();
         
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_findUserByID);
        preparedStatement.setInt(1, userID);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String email = resultSet.getString("email");
	
			user = new User(username, password, firstName, lastName, email);
        }

        closeAndDisconnectAll();
         
        return user;
    }


}// end [ CLASS: UserDAO ]
