package Database_TermProject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - ControlServlet does not handle parsing and storing of the animal traits. Instead, the single  //
//          String returned is sent to TraitDAO which handles parsing and adding the traits to table.   //
//                                                                                                      //
//      [General Format of ControlServlet Methods]                                                      //
//          (1) Declare a local variable for holding info we want to output                             //
//          (2) Attach the variable from (1) to a new parameter in the request                          //
//          (3) Set the RequestDispatcher object equal to the target JSP                                //
//              (Object "dispatcher" is a field of ControlServlet.java)                                 //
//          (4) Forward the dispatcher with the newly-updated request object attached                   //
//          (!) ACID functions are to be handle by the DAO java files NOT ControlServlet                //
//                                                                                                      //
//      [Example]                                                                                       //
//           (1) List<Animal> allAnimals                                                                //
//           (2) request.setAttribute("listOfAnimals",allAnimals)                                       //
//           (3) dispatcher=request.getRequestDispatcher("PrintAllAnimals.jsp");                        //
//           (4) dispatcher.forward(request,response)                                                   //
//------------------------------------------------------------------------------------------------------//

// !! TODO: Confirm all Primary Keys
// IF TIME: Display notifications of errors (e.g. "Login details incorrect")

public class ControlServlet extends HttpServlet {
	private final long serialVersionUID = 1L;
	private HttpSession session = null;
	private RequestDispatcher dispatcher = null;

	private UserDAO         userDAO;
	private AnimalDAO       animalDAO;
	private TraitDAO        traitDAO;
	private ReviewDAO       reviewDAO;
	private FavAnimalDAO    favAnimalDAO;
	private FavBreederDAO   favBreederDAO;

    public ControlServlet() {}


	public void initializeAll(HttpServletResponse response)
			throws SQLException, IOException {

		userDAO = new UserDAO();                            // Initialize the local DAO objects
		animalDAO = new AnimalDAO();
		traitDAO = new TraitDAO();
		
		userDAO.initializeTable();                        	// Explicitly initialize all Tables
		animalDAO.initializeTable();
		traitDAO.initializeTable();
		reviewDAO.initializeTable();
		favAnimalDAO.initializeTable();
		favBreederDAO.initializeTable();
		
		userDAO.insert(new User("root", "pass1234", "rootFName", "rootLName", "r@root.com"));		// Add the root user to the Users Table

		response.sendRedirect("Login.jsp");					// Redirect to Login page once initialization is complete
	}


	protected void loginHelper(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {

        // PrintWriter out = response.getWriter();  // Purpose: popup window alerts

		String username = request.getParameter("username");				                // Extract user-provided login info from request (i.e. Login form)
		String password = request.getParameter("password");

		boolean loginSuccessful = userDAO.validateLoginAttempt(username, password);

		if (loginSuccessful) {
            // --[ LOGIN SUCCESS ]--
		    int retrievedID = userDAO.retrieveUserID(username, password);               // Retrieve user's ID with username and pass.
		    User currentUser = userDAO.getUser(retrievedID);                            // Retrieve User with retrieved ID

            // CHECK: Modify attribute names?
		    session = request.getSession();                                             // Record the current session
            session.setAttribute("currentUserID",   currentUser.getId());               // session requires getting info thru object
            session.setAttribute("currentUsername", currentUser.getUsername());
            session.setAttribute("currentFName",    currentUser.getFirstName());
            session.setAttribute("currentLName",    currentUser.getLastName());
            session.setAttribute("currentEmail",    currentUser.getEmail());

			response.sendRedirect("index.jsp");							                // Route to website homepage
		}
		else {
            // --[ LOGIN FAILED ]--
			response.sendRedirect("Login.jsp");							                // Re-Route *back* to login page
		}
		
	}

    // !! TODO: Attach logout functionality to a button or link somewhere on the web pages
    protected void logoutHelper(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

	    session = request.getSession();                                                     // Load the session instance
	    session.invalidate();                                                               // Unbind the object of the current user

	    response.sendRedirect("login.jsp");
    }
	
	
	protected void showNewUserForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		dispatcher = request.getRequestDispatcher("UserForm.jsp");
		dispatcher.forward(request, response);
	}
	

	protected void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		
		int id = Integer.parseInt(request.getParameter("id"));								// ID of User to be modified
		
		User existingUser = userDAO.getUser(id);											// Load the User to be modified
		
		dispatcher = request.getRequestDispatcher("UserForm.jsp");
		request.setAttribute("user", existingUser);											// Attach loaded User to request
		dispatcher.forward(request, response);
	}

	
	protected void insertUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {
		
		String username = request.getParameter("currentUsername");									// Extract data entered (UsersForm.jsp)
		String password = request.getParameter("currentPassword");
		String firstName = request.getParameter("currentFName");
		String lastName = request.getParameter("currentLName");
		String email = request.getParameter("currentEmail");
		
		User newUser = new User(username, password, firstName, lastName, email);			// Build new user
		
		userDAO.insert(newUser);															// Add the new user to the Users table
		response.sendRedirect("Login.jsp");													// Return to login page
	}

	
	protected void updateUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {
		
		int id = Integer.parseInt(request.getParameter("id"));
		
		String username = request.getParameter("currentUsername");
		String password = request.getParameter("currentPassword");
		String firstName = request.getParameter("currentFName");
		String lastName = request.getParameter("currentLName");
		String email = request.getParameter("currentEmail");

		User user = new User(id, username, password, firstName, lastName, email);
		
		userDAO.update(user);
		response.sendRedirect("list");
	}


	protected void deleteUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException {

		int id = Integer.parseInt(request.getParameter("id"));

		userDAO.delete(id);
		response.sendRedirect("list");
	}


	protected void checkNumberOfAnimalsPosted(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int currentUserID = (int) session.getAttribute("currentUserID");            // Get the ID of the current user
        User currentUser = userDAO.getUser(currentUserID);                          // Establish a reference to the current user

	    if (!currentUser.maxPostsReached()) {
            response.sendRedirect("AnimalForm.jsp");                                // <5 posted animals, user may post an animal
        }
	    else {
            response.sendRedirect("index.jsp");                                     // Maxed out, route back to homepage
        }
    }


    // Tasks Accomplished by New Animal Helper:
    //  - Add the animal (with currentUser's username) to the Animals table
    //  - Add the animal's ID and traits to the Traits table
    protected void newAnimalPostHelper(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int currentUserID = (int) session.getAttribute("currentUserID");                            // Get the ID of the current user
	    String ownersUsername = (String)session.getAttribute("currentUsername");                    // Get the username of the current user

        String name = request.getParameter("name");                                                 // Extract data entered (AnimalForm.jsp)
        String species = request.getParameter("species");
        String birthDate = request.getParameter("birthDate");
        int adoptionPrice = request.getParameter("adoptionPrice");  // !! CRITICAL: Why is parameter "adoptionPrice" a String?
        String traitsRawData = request.getParameter("traits");

        Animal newAnimal = new Animal(name, species, birthDate, adoptionPrice, ownersUsername);     // Build the new animal object

        animalDAO.insert(newAnimal);                                                                // Add the new animal to the Animals table
        traitDAO.addAnimalTraitsToTable(traitsRawData, newAnimal.id);                               // See: Notes section above for trait parser

        response.sendRedirect("index.jsp");                                                         // Return to home page
    }


    /*protected void openSearchForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        List<String> allCurrentTraits = traitDAO.getAllUniqueTraits();

        dispatcher = request.getRequestDispatcher("DynamicDropSearch.jsp");
        request.setAttribute("allTraits", allCurrentTraits);
        dispatcher.forward(request, response);
    }*/


    protected void searchForAnimalByTrait(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

	    String trait = request.getParameter("trait");
        List<Animal> searchResults = traitDAO.listAllAnimalsByTrait(trait);

        //dispatcher = request.getRequestDispatcher("AdoptionListByTrait.jsp"); which JSP?
        dispatcher = request.getRequestDispatcher("AdoptionList.jsp");

        request.setAttribute("listAnimals", searchResults);
        dispatcher.forward(request, response);
    }


    protected void animalReviewFormHelper(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        // !! CHECK: Right place for ID's? i think so..
        int currentUserID = (int) session.getAttribute("currentUsername");          // Get the ID of the current user
        int animalID = (int) session.getAttribute("animalID");                      // Get the ID of the animal the user selected to review

        request.setAttribute("currentUserID", currentUserID);                       // Attach the values to the request
        request.setAttribute("animalID", animalID);

        dispatcher = request.getRequestDispatcher("ReviewForm.jsp");
        dispatcher.forward(request, response);
    }


    // !! TODO: Getting the Animal (animalID) to review will be done from the adoption list
    //    TODO         by getting the AnimalID: [ (int)request.getParameter("animalID") ]
    // !! CHECK: Confirm that above line is accurate
    protected void submitReview(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        // !! CRITICAL: Same as newAnimalPostHelper, why is parameter "adoptionPrice" a String?
        int animalID = request.getParameter("animalID");                                    // animalID is set when the link was clicked from the animal list

        String authorsUsername = (String) session.getAttribute("currentUsername");          // Author is current user

        String rating = request.getParameter("currentFName");                               // Extract data entered (ReviewForm.jsp)
        String comment = request.getParameter("currentEmail");

        Review newReview = new Review(animalID, authorsUsername, rating, comment);            // Build new user

        reviewDAO.insert(newReview);                                                            // Add the new user to the Users table
        response.sendRedirect("Login.jsp");                                                    // Return to login page
    }

}// END [ CLASS: ControlServlet ]

