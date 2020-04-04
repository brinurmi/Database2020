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
// [Project-Wide Notes]                                                                                 //
//      - Tables with foreign keys need to be declared AFTER the table they reference                   //
//      - The password for our Default Root user was changed to differentiate between:                  //
//          [Root: InitializeDB.jsp] AND [Root: Local MySQL Instance Admin, Workbench]                  //
//      - Attributes defined as "NOT NULL" are critical to functionality (e.g. password in User table)  //
//      - Attributes that are dependent on an Animal existing will be auto-deleted once said animal     //
//          is removed (either deleted or adopted). These attributes can be identified in their         //
//          tables as they'll be defined with "ON DELETE CASCADE".                                      //
//      - Attributes that get their data from what a user types in (Reviews, species, etc) must         //
//          match the maxlength as set in the JSP files (e.g. In Reviews Table: "comments varchar(140)" //
//          and thus: In ReviewForm.jsp <textarea maxlength="140"> )                                    //
//      - The method closeAndDisconnectAll() is implemented to replace the methods                      //
//          resultSet.close() statement.close(), preparedStatement.close(), connect.close().            //
//          with intent to eliminate guess work as to which, and when, SQL items need to be closed,     //
//          as well as cleaning up the code.                                                            //
//          The limitation of this method however, is that any actions needed with any of               //
//          those objects needs to be carried out before closeAndDisconnectAll() is called.             //
//          A good example of this is UserDAO.validateUser().                                           //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//
//                                                                                                      //
//      [Rough Format of ControlServlet Methods]                                                        //
//          (!) CRUD functions are to be handle by the DAO java files NOT ControlServlet                //
//          (1) Declare a local variable for holding info we want to output                             //
//          (2) Attach the variable from (1) to a new parameter in the request                          //
//          (3) Set the RequestDispatcher object equal to the target JSP                                //
//              (Object "dispatcher" is a field of ControlServlet.java)                                 //
//          (4) Forward the dispatcher with the newly-updated request object attached                   //
//                                                                                                      //
//      [Example]                                                                                       //
//           (1) List<Animal> allAnimals                                                                //
//           (2) request.setAttribute("listOfAnimals",allAnimals)                                       //
//           (3) dispatcher=request.getRequestDispatcher("PrintAllAnimals.jsp")                         //
//           (4) dispatcher.forward(request,response)                                                   //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//

/*                                            ~   ~   ~                                                 */

//------------------------------------------------------------------------------------------------------//
// Notes:                                                                                               //
//      - ControlServlet does not handle parsing and storing of the animal traits. Instead, the single  //
//          String returned is sent to TraitDAO which handles parsing and adding the traits to table    //
//      - The output format of t handle parsing and storing of the animal traits. Instead, the single   //
//      - As per instructions, the info displayed after a trait search will be the items in Feature 3   //
//          ( Animal Name, Species, User who posted animal, and adoption price )                        //
//      - JSPs that ultimately output the same TYPE of info are reused. What is actually OUTPUT in      //
//          them is handled in this file (ControlServlet.java) via the DAO objects                      //
//          (e.g Listing all animals and listing a user's fav animals will both use AdoptionList.jsp,   //
//          but the List<Animals> list passed to JSP will have different contents: either ALL animals   //
//          or a User's fav)                                                                            //
//      - The boolean returns of CRUD methods (e.g. insertUser()) are only being used for testing       //
//          and demo purposes)                                                                          //
//      - Our Traits table is set up to associate animal traits to the animal's ID. However, animalID   //
//          is auto-incremented. Thus, we need a special SELECT statement to get the animal's ID from   //
//          the DB once it is added.                                                                    //
//                                                                                                      //
//------------------------------------------------------------------------------------------------------//


public class ControlServlet extends HttpServlet {
//  private final long serialVersionUID = 1L;
    private HttpSession session = null;
    private RequestDispatcher dispatcher = null;

    private UserDAO userDAO;
    private AnimalDAO animalDAO;
    private TraitDAO traitDAO;
    private ReviewDAO reviewDAO;
    private FavAnimalDAO favAnimalDAO;
    private FavBreederDAO favBreederDAO;

    public void initializeAll(HttpServletResponse response)
            throws SQLException, IOException {

        userDAO         = new UserDAO();                                        // Initialize all the local DAO objects
        animalDAO       = new AnimalDAO();
        traitDAO        = new TraitDAO();
        reviewDAO       = new ReviewDAO();
        favAnimalDAO    = new FavAnimalDAO();
        favBreederDAO   = new FavBreederDAO();

        userDAO         .initializeTable();                                     // Explicitly initialize all Tables
        animalDAO       .initializeTable();
        traitDAO        .initializeTable();
        reviewDAO       .initializeTable();
        favAnimalDAO    .initializeTable();
        favBreederDAO   .initializeTable();

        userDAO.insert(new User("root", "123",
                "rootFName", "rootLName", "r@root.com"));                       // Add the root user to the Users Table

        response.sendRedirect("Login.jsp");                                     // Redirect to Login page once initialization is complete
    }


    //     ---------------------| doPost() Landing |---------------------     //

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    //     --------------------------| SWITCH |---------------------------    //

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getServletPath();                // Store action (supplied by the JSP files)

        try {
            switch (action) {
                case "/InitializeDB":
                    initializeAll(response);                                    // Initialize components
                    break;
                case "/CheckLogin":
                    // TEST
                	System.out.println("GOING TO LOG IN");
                    checkLogin(request, response);                              // Verify login information
                    break;
                case "/Logout":
                    // TEST
                    logoutHelper(request, response);                            // Log out the current user
                    break;
                case "/NewUser":
                    // GOOD
                    showNewUserForm(request, response);                         // Add a new user (Will load UsersForm.jsp)
                    break;
                case "/UpdateUser":
                    // BAD (updating creates "user already exists" error)
                    updateUser(request, response);                              // Update User information
                    break;
                case "/InsertUser":
                    // TEST
                    insertUser(request, response);                              // Insert the user to the table
                    break;
                case "/delete":
                    deleteUser(request, response);                              // Delete a user
                    break;
                // TODO: "Edit User" functionality not done
                case "/edit":
                    showEditForm(request, response);                            // Edit existing user (Will load: UsersForm.jsp)
                    break;
                case "/ListBreeders":
                    // TEST
                    listAllBreeders(request, response);                         // Get a list of all breeders
                    break;
                case "/BeginPostAnimalProcess":
                    // TEST
                    checkNumberOfAnimalsPost(request, response);                // Check animals user posted is <5 (SUCCESS: Redirect to AnimalForm.jsp)
                    break;
                case "/SubmitNewAnimal":
                    // TEST
                    postAnimal(request, response);                              // Update relevant tables (i.e. Animals and Traits)
                    break;
                case "/ListAnimals":
                    // TEST
                    listAllAnimals(request, response);                          // Get a list of all animals
                    break;
                /*
                // !! CHECK: Can user be routed directly to SearchByTrait.jsp instead?
                case "/SearchForAnimalByTrait":
                    searchForAnimalByTrait(request, response);                  // Redirect to the search form
                    break;*/
                case "/ProcessAnimalTraitSearch":
                    // IMPLEMENT
                    processAnimalTraitSearch(request, response);                // Get a list of animals by trait
                    break;
                case "/ReviewAnimal":
                    // IMPLEMENT
                    // Getting error "animal.animalID does not exist" AdoptionList.jsp
                    animalReviewFormHelper(request, response);                  // Attach User and Animal's IDs to request then load: ReviewForm.jsp
                    break;
                case "/SubmitReview":
                    // IMPLEMENT
                    submitReview(request, response);                            // Have user enter a review (Will Load: ReviewForm.jsp)
                    break;
                case "DeleteAnimal":
                    // IMPLEMENT
                    animalDeletionHelper(request, response);                    // Remove animal from adoption list (incl'd its traits and reviews)
                    break;
                case "/ListFavoriteAnimals":
                    // TEST
                    showListFavAnimalsPage(request, response);                  // List a user's favorite animals (Will load AdoptionList.jsp)
                    break;
                case "/ListFavoriteBreeders":
                    // TEST
                    showListFavBreedersPage(request, response);                 // List a user's favorite breeders (Will load UsersList.jsp)
                    break;
                default:
                    System.out.println("\n[ SWITCH: default ]\n");
                    response.sendRedirect("login.jsp");                         // Default action: Login page
                    break;
            }

        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }// END METHOD [ doGet() ]


    //     -------------------------| METHODS |---------------------------    //

    protected void checkLogin(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String username;
        String password;
        boolean loginSuccessful;
        User currentUser;

        username = request.getParameter("username");                            // Extract login info typed in by user (i.e. from Login.jsp)
        password = request.getParameter("password");
        loginSuccessful = userDAO.validateLoginAttempt(username, password);     // Validate credentials

        if (loginSuccessful) {
            currentUser = userDAO.getUser(username, password);                  // Retrieve User

            session = request.getSession();                                     // Record the current session

            session.setAttribute("sUsername", currentUser.getUsername());       // Session requires info retrieval thru objects
            session.setAttribute("sFirstName", currentUser.getFirstName());
            session.setAttribute("sLastName", currentUser.getLastName());
            session.setAttribute("sEmail", currentUser.getEmail());

            response.sendRedirect("index.jsp");                                 // Route to website homepage
        }
        else {
            response.sendRedirect("Login.jsp");                                 // Re-Route *back* to login page
        }

    }


    // !! TODO: Attach logout functionality to a button or link somewhere on the web pages
    protected void logoutHelper(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        session = request.getSession();                                         // Load the session instance
        session.invalidate();                                                   // Unbind the object of the current user

        response.sendRedirect("login.jsp");                                     // Return to login page (since no user logged in)
    }


    protected void showNewUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        dispatcher = request.getRequestDispatcher("UsersForm.jsp");             // Route user to registration form
        dispatcher.forward(request, response);
    }


    protected void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String username;
        String password;
        User existingUser;

        username = request.getParameter("username");                            // Username of User to be modified
        password = request.getParameter("password");                            // Password of User to be modified

        existingUser = userDAO.getUser(username, password);                     // Load the User (Will be used with <c:if ... >)

        request.setAttribute("user", existingUser);                             // Attach loaded User to request

        dispatcher = request.getRequestDispatcher("UsersForm.jsp");
        dispatcher.forward(request, response);
    }


    protected void listAllBreeders(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        List<User> listBreeders;

        listBreeders = userDAO.listAllUsers();                                  // Build the list of all users currently in the DB
        request.setAttribute("listUsers", listBreeders);

        dispatcher = request.getRequestDispatcher("UsersList.jsp");
        dispatcher.forward(request, response);
    }


    // !! TODO: Make the JSP form check minimum length name, etc... (email is already done somehow..?)
    protected void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String username;
        String password;
        String firstName;
        String lastName;
        String email;
        User newUser;
        boolean insertSuccessful;

        username = request.getParameter("username");                            // Extract data entered in by user (UsersForm.jsp)
        password = request.getParameter("password");
        firstName = request.getParameter("firstName");
        lastName = request.getParameter("lastName");
        email = request.getParameter("email");

        newUser = new User(username, password, firstName, lastName, email);     // Build the temp new User object

        insertSuccessful = userDAO.insert(newUser);                             // Add the new user to the Users table

        // TESTING CONFIRMATION
        /*System.out.print("INSERT User (" + username + "): ");
        if (insertSuccessful)
            System.out.println("SUCCESS");
        else
            System.out.println("FAILED");*/

        response.sendRedirect("Login.jsp");                                     // Return to login page for new user to login
    }


    protected void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String username;
        String password;
        String firstName;
        String lastName;
        String email;
        int id;
        User updatedUser;

        username = request.getParameter("username");                            // Extract data entered in by user (UsersForm.jsp)
        password = request.getParameter("password");
        firstName = request.getParameter("firstName");
        lastName = request.getParameter("lastName");
        email = request.getParameter("email");
        id = Integer.parseInt(request.getParameter("id"));

        updatedUser = new User(id, username, password, firstName, lastName, email);     // Build the temp new User object

        userDAO.update(updatedUser);                                            // Update the Users table
        response.sendRedirect("index.jsp");                                     // Route back to homepage
    }


    // !! NOT BEING USED CURRENTLY
    protected void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        userDAO.delete("temp");
        response.sendRedirect("list");
    }// !!


    protected void checkNumberOfAnimalsPost(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String ownersUsername;
        boolean userReachedMaxPosts;

        ownersUsername = (String) session.getAttribute("sUsername");            // Get the username of the current user
        userReachedMaxPosts = userDAO.maxAnimalsReached(ownersUsername);

        if (userReachedMaxPosts) {                                              // Query the Animals table (See: UserDAO.java)
            // IF TIME: Prompt to let user know max reached
            response.sendRedirect("index.jsp");                                 // Maxed out, route back to homepage
        }
        else {
            response.sendRedirect("AnimalForm.jsp");                            // <5 posted animals, route to AnimalForm.jsp
        }
    }


    protected void postAnimal(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String name;
        String species;
        String birthDate;
        int adoptionPrice;
        String traitsRawData;
        String ownersUsername;
        Animal newAnimal;

        name = request.getParameter("name");                                    // Extract data entered (AnimalForm.jsp)
        species = request.getParameter("species");
        birthDate = request.getParameter("birthDate");
        adoptionPrice = Integer.parseInt(request.getParameter("adoptionPrice"));
        traitsRawData = request.getParameter("traits");
        ownersUsername = (String) session.getAttribute("sUsername");            // "Owner" is simply the current user

        newAnimal = new Animal(name, species, birthDate, adoptionPrice, ownersUsername);
                                                                                // (↑ Above): Build the temp Animal object to add

        // !! CRITICAL See: Notes about adding Traits
        animalDAO.insert(newAnimal, traitsRawData);                             // Add the new animal to the Animals table

        response.sendRedirect("index.jsp");                                     // Return to home page
    }

    protected void openSearchForm(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        dispatcher = request.getRequestDispatcher("SearchByTrait.jsp");
        dispatcher.forward(request, response);
    }


    protected void listAllAnimals(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        System.out.println("LISTING ALL ANIMALS - START");
        List<Animal> listAnimals;

        listAnimals = animalDAO.listAllAnimals();                               // Build the list of animals

        dispatcher = request.getRequestDispatcher("AdoptionList.jsp");
        System.out.println("TEST VALUE:" + listAnimals.get(0).animalID);


        request.setAttribute("listAnimals", listAnimals);                       // !! CRITICAL: Make sure we can put the "setAttribute" here

        dispatcher.forward(request, response);
    }


    protected void searchForAnimalByTrait(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        dispatcher = request.getRequestDispatcher("SearchByTrait.jsp");
        dispatcher.forward(request, response);
    }


    protected void processAnimalTraitSearch(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        String trait;
        List<Animal> animalsWithTrait;

        trait = request.getParameter("trait");
        animalsWithTrait = traitDAO.getAnimalsWithTrait(trait);              // Build the list of animals with the desired trait
        request.setAttribute("listAnimals", animalsWithTrait);                  // !! CRITICAL: Make sure we can put the "setAttribute" here

        dispatcher = request.getRequestDispatcher("AdoptionList.jsp");
        dispatcher.forward(request, response);
    }


    protected void animalReviewFormHelper(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int animalID;

        animalID = Integer.parseInt(request.getParameter("animalID"));          // Get the ID of the animal the user selected to review

        request.setAttribute("user", animalID);                             // Attach the animalID to request
        request.setAttribute("animalID", animalID);                             // Attach the animalID to request

        dispatcher = request.getRequestDispatcher("ReviewForm.jsp");
        dispatcher.forward(request, response);
    }


    protected void submitReview(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int animalID;
        String authorsUsername;
        String rating;
        String comment;
        Review newReview;

        animalID = Integer.parseInt(request.getParameter("animalID"));          // animalID is set when the link was clicked from the animal list
        authorsUsername = (String) session.getAttribute("sUsername");           // Author is will be currently-logged-in user
        rating = request.getParameter("sFirstName");                            // Extract data entered (ReviewForm.jsp)
        comment = request.getParameter("sEmail");
        newReview = new Review(animalID, authorsUsername, rating, comment);     // Build temp Review object

        reviewDAO.insert(newReview);                                            // Add the new review

        response.sendRedirect("index.jsp");                                     // Return to home page
    }


    protected void animalDeletionHelper(HttpServletRequest request, HttpServletResponse response)
            throws SQLException {

        int animalID;

        animalID = Integer.parseInt(request.getParameter("animalID"));          // animalID is set when the link was clicked from the animal list
        animalDAO.delete(animalID);                                             // Delete the animal'

        /*
        // !! CRITICAL: Confirm "ON DELETE CASCADE" is functioning properly, otherwise below code is needed
        traitDAO.deleteTraitsByAnimal(animalID);                                // Delete the animal's traits
        reviewDAO.deleteReviewsByAnimal(animalID);                              // Delete the animal's reviews
        favAnimalDAO.delete(animalID);                                          // Remove the animal from any users' favorite list
        favBreederDAO.delete(animalID);                                         // Remove the animal from any users' favorite list */
    }

    protected void showListFavAnimalsPage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String username;
        List<Animal> listFavAnimals;

        // !! CHECK: Fav for certain user or current user?
        username = (String) session.getAttribute("sUsername");                  // Get current user's username
        listFavAnimals = favAnimalDAO.listAllFavAnimals(username);              // Get fav. animals for supplied username

        request.setAttribute("listAnimals", listFavAnimals);

        dispatcher = request.getRequestDispatcher("AdoptionList.jsp");
        dispatcher.forward(request, response);
    }

    protected void showListFavBreedersPage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String username;
        List<User> listFavBreeders;

        username = request.getParameter("username");
        listFavBreeders = favBreederDAO.listAllFavBreeders(username);           // Get all the fav. breeders for specified username

        request.setAttribute("listUsers", listFavBreeders);

        dispatcher = request.getRequestDispatcher("UsersList.jsp");
        dispatcher.forward(request, response);
    }

}// END CLASS [ ControlServlet ]













