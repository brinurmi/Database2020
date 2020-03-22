package Database_TermProject;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionRouter extends HttpServlet {

    public ControlServlet servlet = new ControlServlet();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getServletPath();                // Determine action

        try {
            switch (action) {
                case "/InitializeDB":
                    servlet.initializeAll(response);                            // Initialize components
                    break;
                case "/LoginAttempt":
                    servlet.loginHelper(request, response);                     // Verify login information
                    break;
                case "/Logout":
                    servlet.logoutHelper(request, response);                    // Log out the current user
                    break;
                case "/New User":
                    servlet.showNewUserForm(request, response);                 // Add a new user (Will load UserForm.jsp)
                    break;
                case "/Insert User":
                    servlet.insertUser(request, response);                      // Insert the user to the table
                    break;
                case "/delete":
                    servlet.deleteUser(request, response);                      // Delete a user
                    break;
                case "/edit":
                    servlet.showEditForm(request, response);                    // Edit existing user (Will load: UserForm.jsp)
                    break;
                case "/update":
                    servlet.updateUser(request, response);                      // Update User information
                    break;
                case "/CheckUsersAnimals":
                    // CHECK: ^^Name action name?
                    servlet.checkNumberOfAnimalsPosted(request, response);      // Check animals user posted is <5 (SUCCESS: Redirect to AnimalForm.jsp)
                    break;
                case "/AddAnimalForAdoption":
                    servlet.newAnimalPostHelper(request, response);             // Post animal Adoption (Will load: AnimalAdoptionForm completed)
                    break;
                case "/SearchForAnimalByTrait":
                    servlet.searchForAnimalByTrait(request, response);          // Get a list of animals by trait
                    break;
                case "/ReviewForm":
                    servlet.animalReviewFormHelper(request, response);          // Attach User and Animal's IDs to request then load: ReviewForm.jsp
                    break;
                case "/SubmitReview":
                    servlet.submitReview(request, response);                    // Have user enter a review (Will Load: ReviewForm.jsp)
                    break;
                default:
                    response.sendRedirect("Login.jsp");                 // Default action: Login page
                    break;
            }

        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }// END [ doGet() ]

}// END [ CLASS: ControlServletController ]
