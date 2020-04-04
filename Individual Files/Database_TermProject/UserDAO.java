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
//      - Required User info will be handled by the JSP form, but are marked                            //
//          in the table declaration as "NOT NULL" for demo purposes                                    //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/UserDAO")
public class UserDAO extends HttpServlet {
    //	private static final long serialVersionUID = 1L;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public UserDAO() {}

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

        String SQL_dropTable;
        String SQL_createTable;
        String SQL_populateTable;

        SQL_dropTable = "DROP TABLE IF EXISTS users";

        SQL_createTable = "CREATE TABLE IF NOT EXISTS users(" +
                "username varchar(30) NOT NULL," +
                "password varchar(24) NOT NULL," +
                "firstName varchar(50) DEFAULT 'Anonymous'," +
                "lastName varchar(50)," +
                "email varchar(60) NOT NULL," +
                "PRIMARY KEY (username)," +
                "UNIQUE KEY (username) );";

        SQL_populateTable = "INSERT INTO users (username, password, firstName, lastName, email) values " +
                "('user_0', 'pass_0', 'FName0', 'LName0', '0@email.com'), " +
                "('user_1', 'pass_1', 'FName1', 'LName1', '1@email.com'), " +
                "('user_2', 'pass_2', 'FName2', 'LName2', '2@email.com'), " +
                "('user_3', 'pass_3', 'FName3', 'LName3', '3@email.com'), " +
                "('user_4', 'pass_4', 'FName4', 'LName4', '4@email.com'), " +
                "('user_5', 'pass_5', 'FName5', 'LName5', '5@email.com'), " +
                "('user_6', 'pass_6', 'FName6', 'LName6', '6@email.com'), " +
                "('user_7', 'pass_7', 'FName7', 'LName7', '7@email.com'), " +
                "('user_8', 'pass_8', 'FName8', 'LName8', '8@email.com'), " +
                "('user_9', 'pass_9', 'FName9', 'LName9', '9@email.com');";


        connect_func();                                                         // Ensure active connection
        statement = connect.createStatement();
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");                  // Disable foreign key constraints (req'd to drop tables w/ references)

        statement.executeUpdate(SQL_dropTable);                                // Drop any preexisting Users table
        statement.executeUpdate(SQL_createTable);                               // Establish new Table
        statement.executeUpdate(SQL_populateTable);                             // Populate Table w/ Predefined initial values

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");                  // Re-enable foreign key constraints

        closeAndDisconnectAll();                                                // Terminate any open connections
        System.out.println("Users Table: Initialized");
    }


    public boolean validateLoginAttempt(String username, String password) throws SQLException {

        String SQL_findUserMatch;
        boolean userLocated;

        SQL_findUserMatch = "SELECT * FROM users WHERE username = ? AND password = ?";

        connect_func();

        preparedStatement = connect.prepareStatement(SQL_findUserMatch);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        resultSet = preparedStatement.executeQuery();
        userLocated = resultSet.next(); // CHECK: This method proper?

        if (userLocated) {
            // !! CRITICAL: remove after testing
            System.out.println("USERNAME OF FOUND: " + resultSet.getString("username"));
            System.out.println("PASSWORD OF FOUND: " + resultSet.getString("password"));
        }

        closeAndDisconnectAll();
        return userLocated;
    }

    // [Return True]:  MAX REACHED
    // [Return False]: GOOD TO POST AN ANIMAL
    public boolean maxAnimalsReached(String username) throws SQLException {

        String SQL_findUserMatch;
        int numberOfAnimalsPosted = 0;

        SQL_findUserMatch = "SELECT * FROM animals WHERE ownerUsername = ?";

        connect_func();

        preparedStatement = connect.prepareStatement(SQL_findUserMatch);
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            numberOfAnimalsPosted++;
        }

        return numberOfAnimalsPosted >= 5;                                      // ">" Handle unforeseen scenario of >5 posts
/*

        int number;
        String SQL_test = "SELECT COUNT(*) FROM animals WHERE ownerUsername = ?";

        preparedStatement = connect.prepareStatement(SQL_test);
        preparedStatement.setString(1, "user_1");
        resultSet = preparedStatement.executeQuery();

        number = resultSet.getInt("COUNT(*)");
        System.out.println("Number using COUNT: " + number);

        //closeAndDisconnectAll();

        return number >= 5;
*/
    }


    public List<User> listAllUsers() throws SQLException {

        List<User> listUsers;
        String SQL_getUsers;
        User tempUser;

        listUsers = new ArrayList<>();
        SQL_getUsers = "SELECT * FROM users";

        connect_func();

        statement = (Statement) connect.createStatement();
        resultSet = statement.executeQuery(SQL_getUsers);

        while (resultSet.next()) {
            String username = resultSet.getString("username");                  // Extract data from each table row (i.e. Each user)
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String email = resultSet.getString("email");

            tempUser = new User(username, password, firstName, lastName, email);    // Intermediate temp User obj

            listUsers.add(tempUser);                                            // Add the just-made temp User to the list
        }

        closeAndDisconnectAll();
        return listUsers;                                                       // Return the list of Users (can be empty)
    }


    public boolean insert(User user) throws SQLException {

        String SQL_insertUser;
        boolean rowInserted;

        connect_func();

        SQL_insertUser = "INSERT INTO users (username, password, firstName, lastName, email) VALUES (?, ?, ?, ?, ?)";

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertUser);
        preparedStatement.setString(1, user.username);
        preparedStatement.setString(2, user.password);
        preparedStatement.setString(3, user.firstName);
        preparedStatement.setString(4, user.lastName);
        preparedStatement.setString(5, user.email);

        rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        return rowInserted;
    }


    public boolean delete(String username) throws SQLException {

        String SQL_deleteUser;
        boolean rowDeleted;

        connect_func();

        SQL_deleteUser = "DELETE FROM users WHERE username = ?";

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteUser);
        preparedStatement.setString(1, username);

        rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        return rowDeleted;
    }


    public boolean update(User user) throws SQLException {

        String SQL_updateUser;
        boolean rowUpdated;

        connect_func();
//"INSERT INTO users (username, password, firstName, lastName, email) VALUES (?, ?, ?, ?, ?)";
        SQL_updateUser = "UPDATE users SET username=?, password=?, firstName=?, lastName=?, email=?" +
                "WHERE animalID = ?";

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_updateUser);
        preparedStatement.setString(1, user.username);
        preparedStatement.setString(2, user.password);
        preparedStatement.setString(3, user.firstName);
        preparedStatement.setString(4, user.lastName);
        preparedStatement.setString(5, user.email);
        rowUpdated = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

        return rowUpdated;
    }


	/*public int retrieveUserID(String username, String password) throws SQLException {

	    String SQL_FindUserID = "SELECT * FROM users WHERE username = ? AND password = ?";

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_FindUserID);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        resultSet = preparedStatement.executeQuery();
        String resultSet.getString("id")
        closeAndDisconnectAll();

        return ;
    }*/


    public User getUser(String usernameToGet, String passwordToGet) throws SQLException {

        String SQL_getUser;
        User user = null;                                                       // Init. to null to handle User not existing

        SQL_getUser = "SELECT * FROM users WHERE username = ? AND password = ?";

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_getUser);
        preparedStatement.setString(1, usernameToGet);
        preparedStatement.setString(2, passwordToGet);

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

        return user;                                                            // Return user (or null if not found)
    }


}// END CLASS [ UserDAO ]
