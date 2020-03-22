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


@WebServlet("/ReviewDAO")
public class ReviewDAO extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	

	public ReviewDAO() {}

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

		String SQL_clearExistingTable = "DROP TABLE IF EXISTS reviews";

		String SQL_tableReviews = "CREATE TABLE IF NOT EXISTS reviews" +
                                    "(animalID INTEGER NOT NULL, " +
                                    "authorsUsername varchar(30)," +
                                    "PRIMARY KEY (animalID, authorsUsername)," +
                                    "FOREIGN KEY (animalID) REFERENCES Animals(animalID)," +
                                    "FOREIGN KEY (authorsUsername) REFERENCES Users(username)); ";

		connect_func();											        // Ensure active connection
		statement = connect.createStatement();                          // Create the statement

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");          // Disable foreign key constraints (req'd to drop tables w/ references)

		statement.executeUpdate(SQL_clearExistingTable);                // Drop any preexisting Reviews table
		statement.executeUpdate(SQL_tableReviews);                      // Establish new Animals table

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");          // Re-enable foreign key constraints

        closeAndDisconnectAll();
	}

	
	public boolean insert(Review newReview) throws SQLException {

	    connect_func();

	    String sql = "INSERT INTO reviews(animalID, authorsUsername, rating, comment) VALUES (?, ?, ?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(sql);          // Set values of the Review to be inserted
		preparedStatement.setInt(1, newReview.animalID);
		preparedStatement.setString(2, newReview.authorUsername);
		preparedStatement.setString(3, newReview.rating);
		preparedStatement.setString(4, newReview.comment);

		boolean rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowInserted;
	}


	/*
	!! CHECK: DOES not seem that reviews are directly deleted or updated (seemingly just when the animal is deleted?)
	public boolean delete(int AnimalID) throws SQLException {

		String sql = "DELETE FROM reviews WHERE id = ?";

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, AnimalID);
		
		boolean rowDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowDeleted;
	}


	public boolean update(Review review) throws SQLException {

		String sql = "UPDATE review SET Name=?, Species=?,BirthDate=?, AdoptionPrice=? where id = ?";

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setString(1, review.name);
		preparedStatement.setString(2, review.species);
		preparedStatement.setString(3, review.birthDate);
		preparedStatement.setString(4, review.adoptionPrice);
		preparedStatement.setInt(5, review.id);
		
		boolean rowUpdated = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();

		return rowUpdated;
	}*/


	public List<Review> getAllReviewsForAnimal(int animalID) throws SQLException {

        List<Review> listReviews = new ArrayList<>();
		String sql = "SELECT * FROM reviews WHERE animalID = ?";
        Review review = null;

		connect_func();
		
		preparedStatement = (PreparedStatement) connect.prepareStatement(sql);
		preparedStatement.setInt(1, animalID);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			// animalID = resultSet.getInt("animalID");                                     // animalID passed in, this line left for reading
			String authorUsername = resultSet.getString("birthDate");
            String rating = resultSet.getString("rating");
			String comment = resultSet.getString("comment");

            review = new Review(animalID, authorUsername, rating, comment);
            listReviews.add(review);
		}

        closeAndDisconnectAll();
		
		return listReviews;
	}

}// END [ CLASS: ReviewDAO ]
