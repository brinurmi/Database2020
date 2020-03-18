import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ControllerServlet.java This servlet acts as a page controller for the
 * application, handling all requests from the user.
 *
 * @author www.codejava.net
 */
public class ControlServlet extends HttpServlet {
	private final long serialVersionUID = 1L;
	private HttpSession session = null;

	private UserDAO userDAO;
	private AnimalDAO animalDAO;

    // !! CHECK: Create a method solely to establish local User instance?
    //  User user = userDAO.getUser(session.getAttribute("currentUser"))
    //      [VS]
    //  User user = establishUser();
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	
	/**
	 * The cases in the following switch statement are controlled by the string variable "action"
	 * action's value is provided by the forms of the *.jsp files after the user submits.
	 * e.g.
	 * 		<form action="AttemptLogin" method="post"> ... </form>
	 * 			action == "AttemptLogin"
	 * 			The switch statement below will execute loginAttempt()
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getServletPath();			    // Determine action (See: Note above)
		System.out.println(action);							    // Display action to console for testing purposes

		try {
			switch (action) {
				case "/InitializeDB":
					initializeAll(response);                          // Initialize components
					break;
				case "/LoginAttempt":
					loginAttempt(request, response);                  // Verify login information
					break;
				case "/new":
					showNewForm(request, response);                   // Add a new user (Will load UserForm.jsp)
					break;
				case "/insert":
					insertUser(request, response);                    // Add a user to the table
					break;
				case "/delete":
					deleteUser(request, response);                    // Delete a user
					break;
				case "/edit":
					showEditForm(request, response);                  // Edit existing user (Will load: UserForm.jsp)
					break;
				case "/update":
					updateUser(request, response);                    // Update User information
					break;
                case "/AttemptAnimalPost":
                    attemptPostNewAnimal(request, response);          // Validate user's # of posts (SUCCESS: Redirect to AnimalForm.jsp)
                    break;
                case "/AddAnimalForAdoption":
                    addAnimalForAdoption(request, response);          // Post animal Adoption (Will load: AnimalAdoptionForm completed)
                    break;
				default:
					response.sendRedirect("Login.jsp");               // Default action: Login page
					break;
			}
			
		} catch (SQLException ex) {
			throw new ServletException(ex);
		}
		
	}// END [ doGet() ]
	

	// CHECK: Update constructor used below
	public void initializeAll(HttpServletResponse response)
			throws SQLException, IOException {
		
		userDAO = new UserDAO();                            // Initialize the local DAO objects
		animalDAO = new AnimalDAO();
		
		userDAO.initializeTable();                        	// Initialize the objects' corresponding Tables
		animalDAO.initializeTable();
		
		userDAO.insert(new User("root", "pass1234"));		// Add the root user to the Users Table

        // !! TESTING: REMOVE BEFORE SUBMISSION
        userDAO.insert(new User(" ", " "));
        // !! TESTING: REMOVE BEFORE SUBMISSION

		response.sendRedirect("Login.jsp");					// Load Login page once initialization is complete
	}

	
	// TODO: Display "login Failed" message after Login redirect
	// If-time: Separate username and pass validation to allow for targeted message (e.g. "That username is not registered")
	private void loginAttempt(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {
		
		String username = request.getParameter("username");				// Extract user-provided login info from request (i.e. Login form)
		String password = request.getParameter("password");

        // !! TESTING: REMOVE BEFORE SUBMISSION
        System.out.println("USERNAME PASSED IN: " + username);
        System.out.println("PASSWORD PASSED IN: " + password);
		if(username.equals(" ")) {
            response.sendRedirect("index.jsp");
            return;
        }// !! TESTING: REMOVE BEFORE SUBMISSION

		boolean loginSuccessful = userDAO.validateLoginAttempt(username, password);

		// [ SESSION REFERENCE: TA Demo (0:00 - 1:47) ]
		if (loginSuccessful)
		{
                                                                                // --[ LOGIN SUCCESS ]--
		    int retrievedID = userDAO.retrieveUserID(username, password);               // Retrieve user's ID with username and pass.
		    User currentUser = userDAO.getUser(retrievedID);                            // Retrieve User with retrieved ID

		    session = request.getSession();                                             // Record the current session
            session.setAttribute("currentUserID", retrievedID);                         // Set: User's ID
            session.setAttribute("currentUserFirstName", currentUser.getFirstName());   // Set: User's first name
            // CHECK: Any other variables needed?


			response.sendRedirect("index.jsp");							                // Route to website homepage
			System.out.println("LOGIN: Successful");
		}
		else {
                                                                                // --[ LOGIN FAILED ]--
			response.sendRedirect("Login.jsp");							                // Re-Route *back* to login page
			System.out.println("LOGIN: Failed");
		}
		
	}
	
	
	private void showNewForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("UserForm.jsp");
		dispatcher.forward(request, response);
	}
	

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		
		int id = Integer.parseInt(request.getParameter("id"));								// ID of User to be modified
		
		User existingUser = userDAO.getUser(id);											// Load the User to be modified
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("UserForm.jsp");
		request.setAttribute("user", existingUser);											// Attach loaded User to request
		dispatcher.forward(request, response);
	}

	
	private void insertUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {
		
		String username = request.getParameter("username");									// Extract data entered (UsersForm.jsp)
		String password = request.getParameter("password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		User newUser = new User(username, password, firstName, lastName, email);			// Build new user
		
		userDAO.insert(newUser);															// Add the new user to the Users table
		response.sendRedirect("Login.jsp");													// Return to login page
	}

	
	private void updateUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {
		
		int id = Integer.parseInt(request.getParameter("id"));
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		User user = new User(id, username, password, firstName, lastName, email);
		
		userDAO.update(user);
		response.sendRedirect("list");
	}


	private void deleteUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {

		int id = Integer.parseInt(request.getParameter("id"));

		userDAO.delete(id);
		response.sendRedirect("list");
	}


	private void attemptPostNewAnimal(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        // [ SESSION REFERENCE: TA Demo (1:47+) ]
        int currentUserID = (int) session.getAttribute("currentUserID");            // Get the ID of the current user
        User currentUser = userDAO.getUser(currentUserID);                          // Establish a reference to the current user

	    if(!currentUser.maxPostsReached()) {
            response.sendRedirect("AnimalForm.jsp");
        }
	    else {
	        // TODO: Implement a notification that user has reached max posts
            response.sendRedirect("index.jsp");
        }
    }


    private void addAnimalForAdoption(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

	    String name = request.getParameter("name");                                             // Extract data entered (AnimalForm.jsp)
        String species = request.getParameter("species");
        String birthDate = request.getParameter("birthDate");
        String adoptionPrice = request.getParameter("adoptionPrice");
        String traits = request.getParameter("traits");

        Animal newAnimal = new Animal(name, species, birthDate, adoptionPrice, traits);         // Build the new animal object

        int currentUserID = (int) session.getAttribute("currentUserID");                        // Get the ID of the current user
        User currentUser = userDAO.getUser(currentUserID);                                      // Establish a reference to the current user

        animalDAO.insert(newAnimal);                                                            // Add the new animal to the Animals table
        currentUser.addNewAnimalToMyPostedAnimals(newAnimal);                                   // Add animal to the user's list of posted animals

        response.sendRedirect("index.jsp");                                                     // Return to home page
    }

	
}// END [ CLASS: ControlServlet ]