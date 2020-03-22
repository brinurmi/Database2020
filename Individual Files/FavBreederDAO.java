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
//      - Breeders are just the users who had posted an animal, thus return types are List<User>        //
//      - !! The size of the table columns that reference usernames (e.g. favBreedersUsername) must     //
//              match the size of corresponding column in the reference table.                          //
//              [EXAMPLE]: If in table Users username is set to "varchar(30), then in                   //
//                         table favBreeders favBreedersUsername must also be "varchar(30)"             //
//                                                                                                      //
// IntelliJ Warning Suppression:                                                                     //
/*       */ @SuppressWarnings("SqlNoDataSourceInspection")                                              //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/FavBreederDAO")
public class FavBreederDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
        // TODO Auto-generated method stub
        doGet(request, response);
    }


    private void closeAndDisconnectAll() throws SQLException {
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connect != null) connect.close();
    }


	public void initializeTable() throws SQLException {

		String SQL_clearExistingFavBreedersTable = "DROP TABLE IF EXISTS favBreeders";

        String SQL_tableFavBreeders = "CREATE TABLE IF NOT EXISTS favBreeders" +
                                        "(breederID INTEGER NOT NULL, " +
                                        "username varchar(30)," +
                                        "PRIMARY KEY (breederID, username)," +
                                        "FOREIGN KEY (breederID) REFERENCES Users(id)," +
                                        "FOREIGN KEY (username) REFERENCES Users(username)); ";

		connect_func();											            // Ensure active connection
		statement = connect.createStatement();                              // Create the statement

        statement.executeUpdate(SQL_clearExistingFavBreedersTable);         // Drop any preexisting FavBreeders table
		statement.executeUpdate(SQL_tableFavBreeders);                      // Establish new FavBreeders table

        closeAndDisconnectAll();
	}

	
	public List<User> listAllFavBreeders() throws SQLException {

		connect_func();
		
	    List<Animal> listFavBreeders = new ArrayList<>();
		String sql = "SELECT * FROM animals";

		statement = (Statement) connect.createStatement();
		resultSet = statement.executeQuery(sql);

		while (resultSet.next())
		{
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			String adoptionPrice = resultSet.getString("adoptionPrice");
			int ownerID = resultSet.getInt("ownerID");

			// !! TODO: Add method to list traits

			User breeder = new Animal(id, name, species, birthDate, adoptionPrice, ownerID);
            listFavBreeders.add(animal);
		}

        closeAndDisconnectAll();

		return listFavBreeders;
	}

	
	public boolean insert(Animal animal) throws SQLException {

	    connect_func();

	    String sql = "INSERT INTO animals(name, species, birthDate, adoptionPrice) VALUES (?, ?, ?, ?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, animal.name);
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setString(4, animal.adoptionPrice);

		boolean rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowInserted;
	}


	public boolean delete(int AnimalID) throws SQLException {

		String sql = "DELETE FROM animals WHERE id = ?";

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, AnimalID);
		
		boolean rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowDeleted;
	}

}// END [ CLASS: FavoritesDAO ]
