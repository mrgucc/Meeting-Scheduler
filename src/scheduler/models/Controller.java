package scheduler.models;

import scheduler.services.Firebase;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduler.scenes.*;
import scheduler.widgets.Inviter;
import scheduler.widgets.MeetingDetail;

// This is the central class to control the flow of the application
public class Controller {

    private static Controller instance; //The singleton, accessed through the getter    
    private Parent[] sceneParents;
    private Scene scene;
    private User user;
    UserSecurity userSec = new UserSecurity();
    private ArrayList<User> contacts = new ArrayList();

    // Private controller prevents new instances
    private Controller() {
    }

    // Access the instance only through this getter
    public static Controller getInstance() {
        instance = instance == null ? new Controller() : instance; //Look up ternary operators if confused
        return instance;
    }

    // Initial entry point into the application
    public void runProgram(Stage mainWindow) {
        buildScenes();
        getInstance(); // Prevents attempted method calls on uninstantiated instance (was happening on first getInstance().login() method because didnt exist)
        scene = new Scene(new LoginView());
        mainWindow.setMinHeight(750);
        mainWindow.setMinWidth(1100);
        mainWindow.setTitle("Super Scheduler");
        mainWindow.setScene(scene);
        mainWindow.show();
    }

    // Login process. All login steps should occur in this method
    public void login(String email, String password) throws IOException {

        UserSecurity userSec = new UserSecurity();
        userSec.loginCheck(email, password);
        String jsonResponse = Firebase.getInstance().sendLoginRequest(email, password);
        user = buildUser(jsonResponse);
        user = updateUserObjectData();
        buildScenes(); //New user, rebuild scenes for this user
        goToScene("dashboard");
    }

    // Delete the user object and return to the login screen
    public void logout() {
        user = null;  //is this good enough for deleting?
        goToScene("login");
    }

    // Sign up method
    public void signUp(String fName, String lName, String zip, String email, String pWord, String pWord2) throws IOException {

        userSec.emailValidation(email);
        userSec.accountInputs(fName, lName, zip, email, pWord, pWord2);

        //Throw more exceptions for security, formatting, bad response from network, etc here
        //This method needs A LOT of work before safely building the user
        String jsonResponse = Firebase.getInstance().sendSignupRequest(fName, lName, email, zip, pWord);
        user = buildUser(jsonResponse);
        jsonResponse = Firebase.getInstance().putNewUserData(user.getId(), fName, lName, email, zip);
        user = updateUserObjectData();
        buildScenes();
        goToScene("dashboard");
    }

    //Create the user from the response and update the user info
    private User buildUser(String loginResponse) {
        Gson gson = new Gson();
        user = gson.fromJson(loginResponse, User.class);
        return user;
    }

    //Update the user information by querying the database and applying response to the user object
    private User updateUserObjectData() {
        Gson gson = new Gson();
        String databaseResponse = Firebase.getInstance().getUserInfo(user.getId());
        UserInfo data = gson.fromJson(databaseResponse, UserInfo.class);
        user.updateData(data);
        return user;
    }

    //Add the meeting ID to the user account, and the actual meeting to the list of meetings
    public String addNewMeeting(LocalDate date, int startHour, int startMin, int endHour, int endMin, String organizerId, String subject, ArrayList<String> attendees) {

        // TODO: ADD SECURITY CHECKING OF USER INPUT HERE !!IMPORTANT!!        
        Meeting mtg = new Meeting(date, startHour, startMin, endHour, endMin, organizerId, subject, attendees);
        user = updateUserObjectData(); // Get the latest data from the database in case it changed        
        String result = Firebase.getInstance().putNewMeeting(user, mtg);
        DashboardView dashView = new DashboardView(user);     // Build new dashboard using the updated user
        sceneParents[3] = dashView;                           // Replaces old dashboard view
        goToScene("dashboard");
        return result;
    }

    public String deleteMeeting(Meeting meetingToDelete) {
        ArrayList<Meeting> meetings = user.getMeetings();
        String msg;
        try {
            meetings.remove(meetingToDelete);
            user.setMeetings(meetings);
            Firebase.getInstance().replaceMeetings(user, meetings);
            user = updateUserObjectData();
            DashboardView dbView = new DashboardView(user);     // Build new dashboard using the updated user
            sceneParents[3] = dbView;                           // Replaces old dashboard view
            goToScene("dashboard");
            msg = "Successful deletion. " + meetings.size() + " meetings remain after deletion";

        } catch (Exception e) {
            msg = "Failed to delete the meeting";
            System.out.println();
        }
        return msg;
    }

    public void changePassword(String tokenId, String newPass) throws IOException {
        String jsonResponse = Firebase.getInstance().changePassRequest(tokenId, newPass);
        System.out.println("Change password result: " + jsonResponse);
    }

    public void editAccountInfo(String fName, String lName, String zip, String email) throws IOException {

        // RUN SECURITY METHODS ON THE INPUT HERE
        // Set the new values conditionally if they are not empty
        String newFirst = fName.equals("") ? user.getFirstName() : fName;
        String newLast = lName.equals("") ? user.getLastName() : lName;
        String newZipCode = zip.equals("") ? Integer.toString(user.getZipCode()) : zip;
        String newEmail = email.equals("") ? user.getEmail() : email;
        User editedUser = user.setEditValues(newFirst, newLast, newEmail, newZipCode);
        Firebase.getInstance().putEditedUserData(editedUser);
        user = updateUserObjectData();
        buildScenes();
        goToScene("dashboard");
    }

    //method to delete account. 
    public void deleteAccount(String tokenId) throws IOException {
        String jsonResponse = Firebase.getInstance().deleteAccountRequest(tokenId);
        System.out.println(jsonResponse);
    }

    //Create all the scene nodes. Holding them in an object allows persistent data entered
    //for now, null user means user is logged out. Do not build scenes for null user.
    private void buildScenes() {
        if (user == null) {
            sceneParents = new Parent[]{
                new LoginView(),
                new SignupView(),
                new ForgotView()
            };
        } else {
            sceneParents = new Parent[]{
                new LoginView(),
                new SignupView(),
                new ForgotView(),
                new DashboardView(user),
                new AccountView(user),
                new MapView(user),};
        }
    }

    //Navigate to specified scene
    public void goToScene(String sceneName) {
        switch (sceneName) {
            case "login":
                scene.setRoot(sceneParents[0]);
                break;
            case "signup":
                scene.setRoot(sceneParents[1]);
                break;
            case "forgot":
                scene.setRoot(sceneParents[2]);
                break;
            case "dashboard":
                scene.setRoot(sceneParents[3]);
                break;
            case "account":
                scene.setRoot(sceneParents[4]);
                break;
            case "map":
                scene.setRoot(sceneParents[5]);
                break;
            default:
                System.out.println("There was an error going to \'" + sceneName + "\'. Is it spelled correctly?");
                break;
        }
    }

    public User getUser() {
        return user;
    }

    void populateContacts() {
        ArrayList<String> ids = Firebase.getInstance().getContacts();
        Gson gson = new Gson();

        ids.forEach((id) -> {
            String response = Firebase.getInstance().getUserById(id);
            User contact = gson.fromJson(response, User.class);
            contacts.add(contact);
        });
        System.out.println("Fetched " + contacts.size() + " of " + ids.size() + " users");

    }

    public void openAttendeeManagement(MeetingDetail meetingPanel) {
        // Doesn't check the database if entries already exist.
        if (contacts.size() < 1) {
            populateContacts();
        }
        Inviter attendeeManager = new Inviter(meetingPanel);
        attendeeManager.show();

    }

    // Search all the attendees by making everything lowercase and searching within name and emails
    public ArrayList<User> searchAttendees(String search) {
        ArrayList<User> found = new ArrayList();
        String lower = search.toLowerCase();
        contacts.forEach((c) -> {
            String zip = c.getZipCode() + "";
            if (c.getEmail().toLowerCase().contains(lower) || c.getFullName().contains(lower) || zip.contains(lower)) {
                if (!found.contains(c)) {
                    found.add(c);
                }
            }
        });

        return found;
    }

    // you were creating a search algorithnm
}
