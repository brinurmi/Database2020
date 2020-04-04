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
//         - A similar method is used to add an animal's traits to the Traits table                     //
//         - TraitDAO traitAccessor allows access to the methods in TraitDAO since we cannot extend     //
//              two classes (HttpServlet AND TraitDAO)                                                  //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/AnimalDAO")
public class AnimalDAO extends HttpServlet {
//	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public AnimalDAO() {}

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


    // !! CRITICAL: Inserting to Animal Table works without: "FOREIGN KEY (ownerUsername) REFERENCES Users(username)"
	public void initializeTable() throws SQLException {

		String SQL_clearExistingTable = "DROP TABLE IF EXISTS animals";

		String SQL_tableAnimals = "CREATE TABLE IF NOT EXISTS animals (" +
                                    "animalID INTEGER NOT NULL AUTO_INCREMENT," +
                                    "name varchar(30)," +
                                    "species varchar(24) NOT NULL," +
                                    "birthDate varchar(10)," +
                                    "adoptionPrice INTEGER DEFAULT 1, " +
                                    "ownerUsername varchar(30)," +
                                    "PRIMARY KEY (animalID)," +
                                    "FOREIGN KEY (ownerUsername) REFERENCES Users (username) ON DELETE CASCADE ); ";

		// IF TIME: Fill with better example names

        // user_1 maxes out allowed number of animal posts
        // "Hippopotamus" maxes out species length
        // "Whale" maxes out the adoptionPrice
        // "Ox" (mins out?) is the shortest animal species allowed
        // "Tortoise" maxes out birthDate allowed
		String SQL_populateAnimalTable = "INSERT INTO animals (name, species, birthDate, adoptionPrice, ownerUsername) values " +
                                            "('Animal_1', 'Cat',          '2018/1/01',    10, 'user_1'  ), "+
                                            "('Animal_2', 'Dog',          '2018/2/01',    20, 'user_1'  ), "+
                                            "('Animal_3', 'Cat',          '2018/3/01',    30, 'user_1'  ), "+
                                            "('Animal_4', 'Phish',        '2018/4/01',    40, 'user_1'  ), "+
                                            "('Animal_5', 'Pangolin',     '2019/5/01',    50, 'user_1'  ), "+
                                            "('Animal_6', 'Hippopotamus', '2019/6/01', 10000, 'user_2'  ), "+
                                            "('Animal_7', 'Whale',        '2009/7/01', 99999, 'user_2'  ), "+
                                            "('Animal_8', 'Gorillaz',     '2019/8/01',    80, 'user_2'  ), "+
                                            "('Animal_9', 'Ox',           '2020/9/01',    90, 'user_9'  ), "+
                                            "('Animal_10','Tortoise',     '1980-01-01',  100, 'user_10' )  ;" ;

		connect_func();											                // Ensure active connection
		statement = connect.createStatement();                                  // Create the statement
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");                  // Disable foreign key constraints (req'd to drop tables w/ references)

        statement.executeUpdate(SQL_clearExistingTable);                        // Drop any preexisting Animals table
		statement.executeUpdate(SQL_tableAnimals);                              // Establish new Animals table
		statement.executeUpdate(SQL_populateAnimalTable);                       // Populate Table w/ Predefined initial values

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");                  // Re-enable foreign key constraints
        //initializeAnimals();

        closeAndDisconnectAll();
        System.out.println("Animals Table: Initialized");
	}


	public List<Animal> listAllAnimals() throws SQLException {

        String SQL_allAnimals;
        List<Animal> listAnimals;
        ArrayList<String> traits;
        Animal tempAnimal;
        TraitDAO traitAccessor;

        connect_func();

		SQL_allAnimals = "SELECT * FROM animals";
	    listAnimals = new ArrayList<>();
        traitAccessor = new TraitDAO();

		statement = (Statement) connect.createStatement();
		resultSet = statement.executeQuery(SQL_allAnimals);

        while (resultSet.next())
		{
			int animalID = resultSet.getInt("animalID");
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
			String ownerUsername = resultSet.getString("ownerUsername");

			traits = traitAccessor.getAnimalsTraits(animalID);                      // Get all the traits for this animal
            System.out.println("ID: " + animalID);
            tempAnimal = new Animal(animalID, name, species, birthDate, adoptionPrice, ownerUsername, traits);
			listAnimals.add(tempAnimal);
		}

        closeAndDisconnectAll();
		return listAnimals;
	}

    // IF TIME: Change to insertHelper to de-couple and add boolean return for updateTraitsTable()
	public boolean insert(Animal animal, String traitsRawData) throws SQLException {

	    String SQL_insertAnimal;
        boolean rowInserted;

	    connect_func();

        SQL_insertAnimal = "INSERT INTO animals(name, species, birthDate, adoptionPrice, ownerUsername) " +
                            "values (?, ?, ?, ?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertAnimal);
		preparedStatement.setString(1, animal.name);
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setInt(4, animal.adoptionPrice);
		preparedStatement.setString(5, animal.ownersUsername);
		rowInserted = preparedStatement.executeUpdate() > 0;

		updateTraitsTable(traitsRawData);                                       // Add the new animal's traits to the Traits table

        closeAndDisconnectAll();
		return rowInserted;
	}


	private void updateTraitsTable(String traitsRawData) throws SQLException {

	    String SQL_getAnimalID;
        TraitDAO traitDAO;                                                      // Establish access to the methods in TraitDAO
        int newAnimalsID;

        SQL_getAnimalID = "SELECT LAST_INSERT_ID() FROM animals";               // See: Notes
        traitDAO = new TraitDAO();

        statement = (Statement) connect.createStatement();
        resultSet = statement.executeQuery(SQL_getAnimalID);
        newAnimalsID = resultSet.getInt("animalID");                            // Get the new animals ID (ID is AUTO_INCREMENT)

        traitDAO.addTraitsToTable(traitsRawData, newAnimalsID);                 // Add the animal's traits
    }


	public boolean delete(int animalID) throws SQLException {

		String SQL_deleteAnimal;
        boolean rowDeleted;

		connect_func();

        SQL_deleteAnimal = "DELETE FROM animals WHERE animalID = ?";
		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteAnimal);
		preparedStatement.setInt(1, animalID);
		rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
		return rowDeleted;
	}


	public boolean update(Animal animal) throws SQLException {

		String SQL_updateAnimal;
        boolean rowUpdated;

		connect_func();

        SQL_updateAnimal = "UPDATE animals SET Name=?, Species=?, BirthDate=?, AdoptionPrice=? " +
                "WHERE animalID = ?";

		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_updateAnimal);
		preparedStatement.setString(1, animal.name);                            // Attach updated details
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setInt(4, animal.adoptionPrice);
		preparedStatement.setInt(5, animal.animalID);                           // ID of animal to update
		rowUpdated = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
		return rowUpdated;
	}

	public Animal getAnimal(int animalID) throws SQLException {

		String SQL_getAnimal;
		Animal tempAnimal = null;                                               // Init. to null to handle Animal not existing

		connect_func();

        SQL_getAnimal = "SELECT * FROM animals WHERE animalID = ?";
		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_getAnimal);
		preparedStatement.setInt(1, animalID);
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownersUsername = resultSet.getString("ownerID");

			tempAnimal = new Animal(name, species, birthDate, adoptionPrice, ownersUsername);
		}

        closeAndDisconnectAll();
		return tempAnimal;
	}


	// !! This method should ultimately be replaced
    public List<Animal> getMostExpensive() throws SQLException {

        String SQL_getMostExpensive;
        List<Animal> listAnimals;
        Animal tempAnimal;

        connect_func();

        SQL_getMostExpensive = "SELECT * FROM animals";
        listAnimals = new ArrayList<>();

        statement = (Statement) connect.createStatement();
        resultSet = statement.executeQuery(SQL_getMostExpensive);

        while (resultSet.next()) {
            int animalID = resultSet.getInt("animalID");
            String name = resultSet.getString("name");
            String species = resultSet.getString("species");
            String birthDate = resultSet.getString("birthDate");
            int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownerUsername = resultSet.getString("username");

            tempAnimal = new Animal(animalID, name, species, birthDate, adoptionPrice, ownerUsername);
            listAnimals.add(tempAnimal);
        }

        closeAndDisconnectAll();
        return listAnimals;
    }

}// END CLASS [ AnimalDAO ]
