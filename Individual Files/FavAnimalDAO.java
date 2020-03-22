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
//      - !! The size of the table columns that reference usernames (e.g. favBreedersUsername) must     //
//              match the size of corresponding column in the reference table.                          //
//              [EXAMPLE]: If in table Users username is set to "varchar(30), then in                   //
//                         table favBreeders favBreedersUsername must also be "varchar(30)"             //
//                                                                                                      //
// IntelliJ Warning Suppression:                                                                     //
/*       */ @SuppressWarnings("SqlNoDataSourceInspection")                                              //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/FavAnimalDAO")
public class FavAnimalDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	

	public FavAnimalDAO() {}

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
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connect != null) connect.close();
    }


	public void initializeTable() throws SQLException {

		String SQL_clearExistingFavAnimalsTable = "DROP TABLE IF EXISTS favAnimals";

		// CHECK: Change name of "username" to "favoriteBy" or similar
		String SQL_tableFavAnimals = "CREATE TABLE IF NOT EXISTS favAnimals" +
                                        "(animalID INTEGER NOT NULL, " +
                                        "username varchar(30), " +
                                        "PRIMARY KEY (animalID, username)," +
                                        "FOREIGN KEY (animalID) REFERENCES Animals(animalID)," +
                                        "FOREIGN KEY (username) REFERENCES Users(username)); ";

		connect_func();											            // Ensure active connection
		statement = connect.createStatement();                              // Create the statement

		statement.executeUpdate(SQL_clearExistingFavAnimalsTable);          // Drop any preexisting FavAnimals table
		statement.executeUpdate(SQL_tableFavAnimals);                       // Establish new FavAnimals table

        closeAndDisconnectAll();
	}

	
	public List<Animal> listAllFavoriteAnimals(String username) throws SQLException {

		connect_func();
		
	    List<Animal> listAnimals = new ArrayList<>();
        String sql = "SELECT * FROM favAnimals WHERE username = ?";

		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, username);
		resultSet = statement.executeQuery(sql);

		while (resultSet.next())
		{
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
			String ownerUsername = resultSet.getString("ownerID");

			// !! TODO: Add a method to list traits in the form (like a text box or links?)

			Animal animal = new Animal(id, name, species, birthDate, adoptionPrice, ownerUsername);
			listAnimals.add(animal);
		}

        closeAndDisconnectAll();

		return listAnimals;
	}

	
	public boolean insert(int animalID, String ownerUsername) throws SQLException {

	    connect_func();

	    String sql = "INSERT INTO favAnimals(animalID, username) VALUES (?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, animalID);
		preparedStatement.setString(2, ownerUsername);

		boolean rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowInserted;
	}


	public boolean delete(int animalID) throws SQLException {

		String sql = "DELETE FROM favAnimals WHERE animalID = ?";

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, animalID);
		
		boolean rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowDeleted;
	}


	public Animal getFavAnimal(String username) throws SQLException {

		String sql = "SELECT * FROM favAnimals WHERE username = ?";

		Animal animal = null;

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, username);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownerID = resultSet.getString("ownerID");

			animal = new Animal(name, species, birthDate, adoptionPrice, ownerID);
		}

        closeAndDisconnectAll();
		
		return animal;
	}

}// END [ CLASS: FavoritesDAO ]
