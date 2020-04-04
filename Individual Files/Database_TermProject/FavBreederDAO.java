package Database_TermProject;

import java.io.IOException;

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
//      - Breeders are just the users who had posted an animal, thus return types are List<User>        //
//      - The size of the table columns that reference usernames (e.g. favBreedersUsername) must        //
//              match the size of corresponding column in the reference table.                          //
//              [EXAMPLE]: If in table Users username is set to "varchar(30), then in                   //
//                         table favBreeders favBreedersUsername must also be "varchar(30)"             //
//      - Deleting a tuple from the FavBreeders table effectively "deletes" the breeder                 //
//          from the user's fav                                                                         //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//

@WebServlet("/FavBreederDAO")
public class FavBreederDAO extends HttpServlet {
//	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;


	public FavBreederDAO() {}

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


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


    private void closeAndDisconnectAll() throws SQLException {
        if (resultSet != null)          resultSet.close();
        if (statement != null)          statement.close();
        if (preparedStatement != null)  preparedStatement.close();
        if (connect != null)            connect.close();
    }


	public void initializeTable() throws SQLException {

		String SQL_dropTable;
        String SQL_tableFavBreeders;

        SQL_dropTable = "DROP TABLE IF EXISTS favBreeders";

        SQL_tableFavBreeders = "CREATE TABLE IF NOT EXISTS favBreeders (" +
                                        "breederUsername varchar(30) NOT NULL," +
                                        "whoFavdBreeder varchar(30) NOT NULL," +
                                        "PRIMARY KEY (breederUsername, whoFavdBreeder)," +
                                        "FOREIGN KEY (breederUsername) REFERENCES Users(username) ON DELETE CASCADE," + // Breeder
                                        "FOREIGN KEY (whoFavdBreeder) REFERENCES Users(username) ON DELETE CASCADE); "; // Who fav'd the breeder

		connect_func();											            // Ensure active connection
		statement =  (Statement) connect.createStatement();
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");              // Disable foreign key constraints (req'd to drop tables w/ references)

        statement = connect.createStatement();                              // Create the statement
        statement.executeUpdate(SQL_dropTable);         // Drop any preexisting FavBreeders table
		statement.executeUpdate(SQL_tableFavBreeders);                      // Establish new FavBreeders table

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");              // Re-enable foreign key constraints

        closeAndDisconnectAll();
        System.out.println("FavBreeders Table: Initialized");
	}


	public List<User> listAllFavBreeders(String usernameOfWhoFavdBreeder) throws SQLException {

	    List<User> listFavBreeders;
		String SQL_getFavBreeders;

        listFavBreeders = new ArrayList<>();
        SQL_getFavBreeders = "SELECT * FROM favBreeders WHERE whoFavdBreeder = ?";

		connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_getFavBreeders);
        preparedStatement.setString(1, usernameOfWhoFavdBreeder);

		while (resultSet.next())
		{
            String username = resultSet.getString("username");
            String password = "";
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String email = resultSet.getString("email");

            User breeder = new User(username, password, firstName, lastName, email);        // Intermediate temp User obj
            listFavBreeders.add(breeder);
		}

        closeAndDisconnectAll();
		return listFavBreeders;
	}


    //public User(String username, String password, String firstName, String lastName, String email) {
	public boolean insert(String breederUsername, String username) throws SQLException {

	    String SQL_insertFavBreeders;
        boolean rowInserted;

	    connect_func();

        SQL_insertFavBreeders = "INSERT INTO favBreeders(breederUsername, username) VALUES (?, ?)";
	    preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertFavBreeders);
        preparedStatement.setString(1, breederUsername);
        preparedStatement.setString(2, username);                                   // "username" is the foreign key referencing the user who fav'd the breeder
		rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
		return rowInserted;
	}


	public boolean delete(String breederUsername) throws SQLException {

		String SQL_deleteFavBreeder;
        boolean rowDeleted;

		connect_func();
        SQL_deleteFavBreeder = "DELETE FROM favBreeders WHERE username = ?";    // (About deleting) See: Notes

		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteFavBreeder);
		preparedStatement.setString(1, breederUsername);
		rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
		return rowDeleted;
	}

}// END CLASS [ FavBreederDAO ]
