import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * Servlet implementation class Connect
 */
@SuppressWarnings("SqlNoDataSourceInspection")
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
	
	protected void disconnect() throws SQLException {
		if (connect != null && !connect.isClosed())
			connect.close();
	}
	
	// CHECK: Discuss naming of String SQL_* variables
	// TODO: Confirm Primary Key
	public void initializeTable() throws SQLException {
		// [ REFERENCE: Exercise 3 ]
		
    	String SQL_clearExistingTable = "DROP TABLE IF EXISTS users";
    	
    	String SQL_table = "CREATE TABLE IF NOT EXISTS users" +
								   "(id INTEGER NOT NULL AUTO_INCREMENT, " +
								   "username varchar(50)," +
								   "password varchar(24)," +
								   "firstName varchar(50)," +
								   "lastName varchar(50)," +
								   "email varchar(50)," +
								   "PRIMARY KEY ( id, username ));";
		
    	String SQL_populateTable = "INSERT INTO users (username, password, firstname, lastname, email) values " +
										   "('basicfornow', 'password', 'john', 'smith', 'basicfornow@default.com');" ;
		
    	connect_func();											// Ensure active connection
		statement = connect.createStatement();
		
		statement.executeUpdate(SQL_clearExistingTable);		// Drop existing Users table (if present)
		statement.executeUpdate(SQL_table);						// Establish new Table
		statement.executeUpdate(SQL_populateTable);				// Populate Table w/ Predefined initial values
		System.out.println("INITIALIZE CHECK");
	}
	

	public boolean validateLoginAttempt(String username, String password) throws SQLException{
		// [ REFERENCE: Exercise 4 and Ch.2 ]
		
		String SQL_findUserMatch = "SELECT * FROM users WHERE username = ? AND password = ?";		// Intermediate var for ease of reading
		
		connect_func();
		
		preparedStatement = connect.prepareStatement(SQL_findUserMatch);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, password);
		
		resultSet = preparedStatement.executeQuery();
		
		return resultSet.next();
	}


    public List<User> listAllUsers() throws SQLException {

        // !! METHOD listAllUsers() CURRENTLY NOT USED

        List<User> listUsers = new ArrayList<User>();
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
        
        resultSet.close();
        statement.close();         
        disconnect();
        
        return listUsers;                                                                   // Return the list of Users
    }
    
    
    public boolean insert(User user) throws SQLException {
		
    	String SQL_insertUser = "INSERT INTO users(username, password, firstName, lastName, email) values (?, ?, ?, ?, ?)";
  
		connect_func();                                                                     // Ensure active connection
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertUser);
		preparedStatement.setString(1, user.username);                                      // Set the variable values of the User to be inserted
		preparedStatement.setString(2, user.password);
		preparedStatement.setString(3, user.firstName);
		preparedStatement.setString(4, user.lastName);
		preparedStatement.setString(5, user.email);
		
        boolean rowInserted = preparedStatement.executeUpdate() > 0;
        preparedStatement.close();

        return rowInserted;                                                                 // Confirm INSERT of new User into table Users
    }     
     
    public boolean delete(int usersid) throws SQLException {

        String SQL_deleteUser = "DELETE FROM users WHERE id = ?";
        
        connect_func();                                                                     // Ensure active connection
         
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteUser);   // Prepare the query with parameters
        preparedStatement.setInt(1, usersid);
         
        boolean rowDeleted = preparedStatement.executeUpdate() > 0;
        preparedStatement.close();
        
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
        preparedStatement.close();
        
        return rowUpdated;                                                                  // Confirm UPDATE of User
    }


    // TODO
    public void addPostToUserList(Animal animal){
        //currentUser.addNewAnimalToMyPostedAnimals(animal)
        return;
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

    // TODO: Confirm return value for failed resultSet
	public int retrieveUserID(String username, String password) throws SQLException {

	    String SQL_FindUserID = "SELECT * FROM users WHERE username = ? AND password = ?";
	    int foundID = -1;

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_FindUserID);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) { foundID = resultSet.getInt("id"); }

        resultSet.close();
        statement.close();

        return foundID;
    }
	
    public User getUser(int id) throws SQLException {

        String SQL_findUserByID = "SELECT * FROM users WHERE id = ?";

        User user = null;

        connect_func();
         
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_findUserByID);
        preparedStatement.setInt(1, id);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String username = resultSet.getString("name");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String email = resultSet.getString("email");
	
			user = new User(id, username, password, firstName, lastName, email);
        }
         
        resultSet.close();
        statement.close();
         
        return user;
    }

}// end [ CLASS: UserDAO ]
