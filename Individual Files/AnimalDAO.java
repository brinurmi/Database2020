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
@WebServlet("/AnimalDAO")
public class AnimalDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	
	public AnimalDAO() {
		
	}
	
	/**
	 * @see HttpServlet#HttpServlet()
     * User. and Pass. updated to match local server instance (MySQL Workbench)
     * Password changed to differentiate between:
     *          Root: InitializeDB.jsp [AND] Root: Local Instance Admin
	 */
	protected void connect_func() throws SQLException {
		if (connect == null || connect.isClosed())
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch (ClassNotFoundException e)
			{
				throw new SQLException(e);
			}

			connect = (Connection) DriverManager
										   .getConnection("jdbc:mysql://127.0.0.1:3306/TermProject?"
																  + "user=root&password=admin");
			System.out.println("[AnimalDAO connect]: " + connect);
		}
	}
	
	protected void disconnect() throws SQLException {
		if (connect != null && !connect.isClosed())
			connect.close();
	}
	
	// [ REFERENCE: Exercise 3 ]
	public void initializeTable() throws SQLException {
		
		String SQL_clearExistingTable = "DROP TABLE IF EXISTS animals";
		
		// CHECK: Primary Key(s)
		String SQL_table = "CREATE TABLE IF NOT EXISTS animals" +
								   "(id INTEGER NOT NULL AUTO_INCREMENT, " +
								   "name varchar(50)," +
								   "species varchar(24)," +
								   "birthDate varchar(50)," +
								   "adoptionPrice varchar(50)," +
								   "PRIMARY KEY ( id ));";
		
		// TODO: Fill with better example names
		String SQL_populateTable = "INSERT INTO animals(name, species, birthDate, adoptionPrice) values" +
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
		
		connect_func();											// Ensure active connection
		statement = connect.createStatement();
		
		statement.executeUpdate(SQL_clearExistingTable);        // Drop an existing Animals table
		statement.executeUpdate(SQL_table);						// Establish new Table
		statement.executeUpdate(SQL_populateTable);				// Populate Table w/ Predefined initial values
		
		statement.close();
		disconnect();
	}
	
	public List<Animal> listAllAnimals() throws SQLException {
		List<Animal> listAnimals = new ArrayList<Animal>();
		String sql = "SELECT * FROM animals";
		connect_func();
		statement = (Statement) connect.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		
		while (resultSet.next())
		{
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			String adoptionPrice = resultSet.getString("adoptionPrice");
			String traits = resultSet.getString("traits");
			
			Animal animal = new Animal(id, name, species, birthDate, adoptionPrice, traits);
			listAnimals.add(animal);
		}
		resultSet.close();
		statement.close();
		disconnect();
		return listAnimals;
	}
	
	public boolean insert(Animal animal) throws SQLException {
		connect_func();
		String sql = "INSERT INTO animals(name, species, birthDate, adoptionPrice) values (?, ?, ?, ?)";
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, animal.name);
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setString(4, animal.adoptionPrice);
//		preparedStatement.executeUpdate();
		
		boolean rowInserted = preparedStatement.executeUpdate() > 0;
		preparedStatement.close();
//        disconnect();
		return rowInserted;
	}
	
	public boolean delete(int Animalid) throws SQLException {
		String sql = "DELETE FROM animals WHERE id = ?";
		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, Animalid);
		
		boolean rowDeleted = preparedStatement.executeUpdate() > 0;
		preparedStatement.close();
//        disconnect();
		return rowDeleted;
	}
	
	public boolean update(Animal animal) throws SQLException {
		String sql = "UPDATE animals SET Name=?, Species=?,BirthDate=?, AdoptionPrice=? where id = ?";
		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, animal.name);
		preparedStatement.setString(2, animal.species);
		preparedStatement.setString(3, animal.birthDate);
		preparedStatement.setString(4, animal.adoptionPrice);
		preparedStatement.setInt(5, animal.id);
		
		boolean rowUpdated = preparedStatement.executeUpdate() > 0;
		preparedStatement.close();
//        disconnect();
		return rowUpdated;
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public Animal getAnimal(int id) throws SQLException {
		Animal animal = null;
		String sql = "SELECT * FROM animals WHERE id = ?";
		
		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			String name = resultSet.getString("name");
			String species = resultSet.getString("species");
			String birthDate = resultSet.getString("birthDate");
			String adoptionPrice = resultSet.getString("adoptionPrice");
			String traits = resultSet.getString("traits");

			animal = new Animal(id, name, species, birthDate, adoptionPrice, traits);
		}
		
		resultSet.close();
		statement.close();
		
		return animal;
	}
}
