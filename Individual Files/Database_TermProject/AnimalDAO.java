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
// IntelliJ Warning Suppression:                                                                     /*
/*       */ @SuppressWarnings("SqlNoDataSourceInspection")                                              //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


@WebServlet("/AnimalDAO")
public class AnimalDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
        if (resultSet != null) resultSet.close();
        if (statement != null) statement.close();
        if (preparedStatement != null) preparedStatement.close();
        if (connect != null) connect.close();
    }


	public void initializeTable() throws SQLException {

		String SQL_clearExistingTable = "DROP TABLE IF EXISTS animals";

		String SQL_tableAnimals = "CREATE TABLE IF NOT EXISTS animals" +
                                    "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                                    "name varchar(30)," +
                                    "species varchar(24)," +
                                    "birthDate varchar(10)," +
                                    "adoptionPrice INTEGER," +
                                    "PRIMARY KEY (id) );";

		// IF TIME: Fill with better example names
		String SQL_populateAnimalTable = "INSERT INTO animals(name, species, birthDate, adoptionPrice) values" +
                                            "('Animal_1', 'Cat', '2018/1/01', '$10'), " +
                                            "('Animal_2', 'Dog', '2018/2/01', '$20'), " +
                                            "('Animal_3', 'Cat', '2018/3/01', '$30'), " +
                                            "('Animal_4', 'Dog', '2018/4/01', '$40'), " +
                                            "('Animal_5', 'Cat', '2019/5/01', '$50'), " +
                                            "('Animal_6', 'Dog', '2019/6/01', '$60'), " +
                                            "('Animal_7', 'Cat', '2019/7/01', '$70'), " +
                                            "('Animal_8', 'Dog', '2019/8/01', '$80'), " +
                                            "('Animal_9', 'Cat', '2020/9/01', '$90'), " +
                                            "('Animal_10', 'Dog', '2020/10/01', '$100');"  ;

		connect_func();											        // Ensure active connection
		statement = connect.createStatement();                          // Create the statement

		statement.executeUpdate(SQL_clearExistingTable);                // Drop any preexisting Animals table
		statement.executeUpdate(SQL_tableAnimals);                      // Establish new Animals table
		statement.executeUpdate(SQL_populateAnimalTable);               // Populate Table w/ Predefined initial values

        closeAndDisconnectAll();
	}


	public List<Animal> listAllAnimals() throws SQLException {

		connect_func();
		
	    List<Animal> listAnimals = new ArrayList<>();
		String sql = "SELECT * FROM animals";

		statement = (Statement) connect.createStatement();
		resultSet = statement.executeQuery(sql);

		while (resultSet.next())
		{
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
			String ownerUsername = resultSet.getString("ownerID");

			Animal animal = new Animal(id, name, species, birthDate, adoptionPrice, ownerUsername);
			listAnimals.add(animal);
		}

        closeAndDisconnectAll();

		return listAnimals;
	}

	
	public boolean insert(Animal animal) throws SQLException {

	    connect_func();

	    String sql = "INSERT INTO animals(name, species, birthDate, adoptionPrice, owner) VALUES (?, ?, ?, ?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, animal.name);
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setInt(4, animal.adoptionPrice);
		preparedStatement.setString(4, animal.ownerUsername);

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


	public boolean update(Animal animal) throws SQLException {

		String sql = "UPDATE animals SET Name=?, Species=?,BirthDate=?, AdoptionPrice=? where id = ?";

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, animal.name);                                    // Attached updated details
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setInt(4, animal.adoptionPrice);

		preparedStatement.setInt(5, animal.id);                                         // ID of animal to be updated
		
		boolean rowUpdated = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowUpdated;
	}


	public Animal getAnimal(int id) throws SQLException {

		String sql = "SELECT * FROM animals WHERE id = ?";

		Animal animal = null;

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			int adoptionPrice = resultSet.getInt("adoptionPrice");
            String ownersUsername = resultSet.getString("ownerID");

			animal = new Animal(name, species, birthDate, adoptionPrice, ownersUsername);
		}

        closeAndDisconnectAll();
		
		return animal;
	}

}// END [ CLASS: AnimalDAO ]
