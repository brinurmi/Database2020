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
//      - The longest "rating" (from the <select...> in ReviewForm.jsp) is "Cray-Cray" @ 9 chars long   //
//      - Attribute "comments" size needs to match the maxlength set for <textarea> of ReviewForm.jsp   //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//

@WebServlet("/ReviewDAO")
public class ReviewDAO extends HttpServlet {
//	private static final long serialVersionUID = 1L;
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
        if (resultSet != null)          resultSet.close();
        if (statement != null)          statement.close();
        if (preparedStatement != null)  preparedStatement.close();
        if (connect != null)            connect.close();
    }


	public void initializeTable() throws SQLException {

        String SQL_dropTable;
        String SQL_tableReviews;

		SQL_dropTable = "DROP TABLE IF EXISTS reviews";

		SQL_tableReviews = "CREATE TABLE IF NOT EXISTS reviews (" +
                                    "animalID INTEGER NOT NULL, " +
                                    "authorsUsername varchar(30) NOT NULL," +
                                    "rating varchar(9)," +
                                    "comments varchar(140)," +
                                    "PRIMARY KEY (animalID)," +
                                    "FOREIGN KEY (animalID) REFERENCES Animals(animalID)," +
                                    "FOREIGN KEY (authorsUsername) REFERENCES Users(username) ); ";

		connect_func();											                // Ensure active connection
		statement =  (Statement) connect.createStatement();
        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");                  // Disable foreign key constraints (req'd to drop tables w/ references)

        statement = connect.createStatement();                                  // Create the statement
		statement.executeUpdate(SQL_dropTable);                                 // Drop any preexisting Reviews table
		statement.executeUpdate(SQL_tableReviews);                              // Establish new Animals table

        statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");                  // Re-enable foreign key constraints

        closeAndDisconnectAll();
        System.out.println("Reviews Table: Initialized");
	}

	
	public boolean insert(Review newReview) throws SQLException {

	    String SQL_insertReview;
        boolean rowInserted;

	    connect_func();

        SQL_insertReview = "INSERT INTO reviews(animalID, authorsUsername, rating, comment) VALUES (?, ?, ?, ?)";

	    preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_insertReview);
		preparedStatement.setInt(1, newReview.animalID);
		preparedStatement.setString(2, newReview.authorUsername);
		preparedStatement.setString(3, newReview.rating);
		preparedStatement.setString(4, newReview.comment);
		rowInserted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
		return rowInserted;
	}


	public List<Review> getAllReviewsForAnimal(int animalID) throws SQLException {

        List<Review> listReviews;
		String SQL_selectReview;
        Review tempReview;

        listReviews = new ArrayList<>();
		SQL_selectReview = "SELECT * FROM reviews WHERE animalID = ?";

		connect_func();

		preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_selectReview);
		preparedStatement.setInt(1, animalID);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet.next())
		{
			// animalID = resultSet.getInt("animalID");                           // animalID passed in, this line left for reading
			String authorUsername = resultSet.getString("birthDate");
            String rating = resultSet.getString("rating");
			String comment = resultSet.getString("comment");

            tempReview = new Review(animalID, authorUsername, rating, comment);
            listReviews.add(tempReview);
		}

        closeAndDisconnectAll();
		return listReviews;
	}


	public boolean deleteReviewsByAnimal(int animalID) throws SQLException {

        String SQL_deleteReview;
        boolean rowsDeleted;

        connect_func();

        SQL_deleteReview = "DELETE FROM reviews WHERE aniamlID = ?";
        preparedStatement = (PreparedStatement) connect.prepareStatement(SQL_deleteReview);
        preparedStatement.setInt(1, animalID);
        rowsDeleted = preparedStatement.executeUpdate() > 0;

        closeAndDisconnectAll();
        return rowsDeleted;
    }

}// END CLASS [ ReviewDAO ]
