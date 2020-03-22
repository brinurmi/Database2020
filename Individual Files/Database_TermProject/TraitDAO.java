package Database_TermProject;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - The method closeAndDisconnectAll() is implemented to replace the methods                      //
//              resultSet.close() statement.close(), preparedStatement.close(), connect.disconnect().   //
//              with intent to eliminate guess work as to which SQL items need to be closed and when.   //
//                                                                                                      //
// IntelliJ Warning Suppression:                                                                        //
/*       */ @SuppressWarnings("ALL")                                                                    //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


public class TraitDAO {

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
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connect != null) connect.close();
    }

    public void initializeTable() throws SQLException {

        String SQL_clearExistingTraitsTable = "DROP TABLE IF EXISTS traits";

        String SQL_tableTraits= "CREATE TABLE IF NOT EXISTS traits" +
                                        "(trait varchar(30) NOT NULL, " +
                                        "animalID INTEGER NOT NULL, " +
                                        "PRIMARY KEY (trait, animalID)," +
                                        "FOREIGN KEY (animalID) REFERENCES Animals(animalID))";

        connect_func();                                                 // Ensure active connection
        statement = connect.createStatement();                          // Create the statement

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");          // Disable foreign key constraints (req'd to drop tables w/ references)

        statement.executeUpdate(SQL_clearExistingTraitsTable);          // Drop any preexisting Trait table
        statement.executeUpdate(SQL_tableTraits);                       // Establish new FavAnimals table

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");          // Re-enable foreign key constraints
        closeAndDisconnectAll();
    }


    public void addAnimalTraitsToTable(String traitRawData, int newAnimalID) throws SQLException {

        String[] parsedTraits = processRawTraitData(traitRawData);              // Process the raw, single string of traits entered

        connect_func();

        String sql = "INSERT INTO traits(trait, animalID) VALUES (?, ?)";

        for (String traitToAdd : parsedTraits) {
            preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
            preparedStatement.setString(1, traitToAdd);
            preparedStatement.setInt(2, newAnimalID);
        }

        //boolean rowInserted = preparedStatement.executeUpdate() > 0;
    }


    private String[] processRawTraitData(String traitRawData){

        String[] individualTraits = null;                                       // Intermediate variable for ease of reading

        traitRawData = traitRawData.toLowerCase();                              // Convert to all lowercase (Intermediate step for ease of reading)

        individualTraits = traitRawData.toLowerCase().split(" ");               // Store all the delimited strings

        return individualTraits;                                                // Return the String array
    }


    // !! CRITICAL: Check proper method of retrieving info
    public List<Animal> listAllAnimalsByTrait(String trait) throws SQLException {

        List<Animal> listOfAnimalsWithTrait = new ArrayList<>();
        String SQL_getAnimalIDs = "SELECT * FROM traits WHERE trait = ?";

        connect_func();

        statement = (Statement) connect.createStatement();
        resultSet = statement.executeQuery(SQL_getAnimalIDs);

        while (resultSet.next()) {
            String name = resultSet.getString("name");                  // Extract data from each resultset row (i.e. the animals with the trait)
            String species = resultSet.getString("species");
            String birthDate = resultSet.getString("birthDate");
            int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownerUsername = resultSet.getString("owner");

            Animal animal = new Animal(name, species,
                    birthDate, adoptionPrice, ownerUsername);           // Build a copy of the animal object
            listOfAnimalsWithTrait.add(animal);                         // Add the temp Animal obj to the list
        }

        closeAndDisconnectAll();

        return listOfAnimalsWithTrait;                                  // Return the list of all the animals with the desired trait
    }

}// END [ CLASS: TraitDAO ]
