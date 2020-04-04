package Database_TermProject;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - The whole "traits" String needs to match <textarea ...> maxlength of 60 characters            //
//      - As per instructions, the info displayed after a trait search will be the items in Feature 3   //
//          ( Animal Name, Species, User who posted animal, and adoption price )                        //
//      - (initializeTable) For initial trait values instead of executing a single, long String as an   //
//           SQL statement, we are invoking the addTraitsToTable() since it will parse the single       //
//           String into the individual traits and add it to the table with the corresponding animalID  //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/TraitDAO")
public class TraitDAO {
//  private static final long serialVersionUID = 1L;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    TraitDAO() {}

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
        if (resultSet != null)          resultSet.close();
        if (statement != null)          statement.close();
        if (preparedStatement != null)  preparedStatement.close();
        if (connect != null)            connect.close();
    }

    // IF TIME: Fill with better example names
    public void initializeTable() throws SQLException {

        String SQL_dropTable;
        String SQL_createTable;

        SQL_dropTable = "DROP TABLE IF EXISTS traits";

        SQL_createTable = "CREATE TABLE IF NOT EXISTS traits (" +
                                        "trait varchar(60) NOT NULL, " +
                                        "animalID INTEGER NOT NULL, " +
                                        "PRIMARY KEY (trait, animalID)," +
                                        "FOREIGN KEY (animalID) REFERENCES Animals(animalID) );";

        connect_func();                                                         // Ensure active connection
        statement = connect.createStatement();                                  // Create the statement
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");                  // Disable foreign key constraints (req'd to drop tables w/ references)

        statement.executeUpdate(SQL_dropTable);                                // Drop any preexisting Trait table
        statement.executeUpdate(SQL_createTable);                               // Establish new Traits table

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");                  // Re-enable foreign key constraints

        // See: Notes
        addTraitsToTable("happy drooly hyper", 2);                              // Add traits for initial animals
        addTraitsToTable("rude", 3);
        addTraitsToTable("calm scratchy soft", 4);
        addTraitsToTable("happy drooly hyper", 5);
        addTraitsToTable("happy drooly hyper", 6);
        addTraitsToTable("happy drooly hyper", 7);
        addTraitsToTable("dramatic", 8);
        addTraitsToTable("happy drooly hyper", 9);
        addTraitsToTable("happy drooly hyper", 10);

        closeAndDisconnectAll();
        System.out.println("Traits Table: Initialized");
    }

    // Extra Function for TESTING
    public void addInitial() throws SQLException {
        System.out.println("ADDING TRAITS");
        // See: Notes
        addTraitsToTable("happy drooly hyper", 2);                              // Add traits for initial animals
        addTraitsToTable("rude", 3);
        addTraitsToTable("calm scratchy soft", 4);
        addTraitsToTable("happy drooly hyper", 5);
        addTraitsToTable("happy drooly hyper", 6);
        addTraitsToTable("happy drooly hyper", 7);
        addTraitsToTable("dramatic", 8);
        addTraitsToTable("happy drooly hyper", 9);
        addTraitsToTable("happy drooly hyper", 10);
    }


    public void addTraitsToTable(String traitRawData, int newAnimalID) throws SQLException {

        String[] parsedTraits = parseRawTraitData(traitRawData);                // Process the raw, single string of traits entered

        connect_func();

        String SQL_insertTrait = "INSERT INTO traits(trait, animalID) VALUES (?, ?)";

        for (String traitToAdd : parsedTraits) {
            preparedStatement = connect.prepareStatement(SQL_insertTrait);      // Prepare the statement
            preparedStatement.setString(1, traitToAdd);                         // Set the values
            preparedStatement.setInt(2, newAnimalID);
            preparedStatement.executeUpdate();                                  // Add values to table
        }
    }


    private String[] parseRawTraitData(String traitRawData){

        String[] individualTraits;                                              // Intermediate variable for ease of reading

        traitRawData = traitRawData.toLowerCase();                              // Convert to all lowercase (Intermediate step for ease of reading)

        individualTraits = traitRawData.split(" ");                             // Store all the delimited strings

        for(String test : individualTraits)
            System.out.print(test);

        return individualTraits;                                                // Return the String array
    }


    // !! TODO: Fix name
    public ArrayList<String> getAnimalsTraits(int animalID) throws SQLException {

        ArrayList<String> animalsTraits = new ArrayList<>();
        String SQL_getAnimalsTraits = "SELECT * FROM traits WHERE animalID = ?";

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_getAnimalsTraits);
		preparedStatement.setInt(1, animalID);

		resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            animalsTraits.add(resultSet.getString("trait"));
        }

        closeAndDisconnectAll();
        return animalsTraits;
    }


    public List<Animal> getAnimalsWithTrait(String trait) throws SQLException {

        List<Animal> listOfAnimalsWithTrait = new ArrayList<>();
        String SQL_getAnimalIDs = "SELECT * FROM traits WHERE trait = ?";
        Animal tempAnimal;

        connect_func();

        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_getAnimalIDs);
        preparedStatement.setString(1, SQL_getAnimalIDs);

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");                          // Extract data from each row (i.e. each animal with the trait)
            String species = resultSet.getString("species");
            String birthDate = resultSet.getString("birthDate");
            int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownerUsername = resultSet.getString("ownerUsername");

            tempAnimal =
                    new Animal(name, species, birthDate, adoptionPrice, ownerUsername);

            listOfAnimalsWithTrait.add(tempAnimal);                                 // Add the temp Animal obj to the list
        }

        closeAndDisconnectAll();
        return listOfAnimalsWithTrait;                                          // Return the list of all the animals with the desired trait
    }

    public boolean deleteTraitsByAnimal(int animalID) throws SQLException {

        String SQL_deleteTraits;
        boolean rowsDeleted;

        SQL_deleteTraits = "DELETE FROM traits WHERE animalID = ?";
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteTraits);
        preparedStatement.setInt(1, animalID);
        rowsDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        return rowsDeleted;
    }


}// END CLASS [ TraitDAO ]
