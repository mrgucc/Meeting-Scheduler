/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.services;

import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import scheduler.models.Meeting;
import scheduler.models.User;

//There is no need to instantiate new Firebase classes, so this is a singleton
// See https://firebase.google.com/docs/reference/rest/database for db api
public class Firebase {

    //Firebase should be accessed by the getInstance
    private static Firebase instance;

    //Private constructor prevents more instances
    private Firebase() {
    }

    //Access the instance only through this getter
    public static Firebase getInstance() {
        instance = instance == null ? new Firebase() : instance; //Look up ternary operators if confused
        return instance;
    }

    //Class variables - might remove many if not necessary
    //Some of these values like api key should not be saved in the app itself. Major security risk
    private final String API_KEY = "AIzaSyDAEG_Ynr-ewIov3Au1YUlR9breoQhXFHQ";
    private final String DB_ENDPOINT = "https://scheduler-cmsc.firebaseio.com/";
    private final String AUTH_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:";
    private final String SIGN_UP_ENDPOINT = AUTH_ENDPOINT + "signUp?key=" + API_KEY;
    private final String SIGN_IN_ENDPOINT = AUTH_ENDPOINT + "signInWithPassword?key=" + API_KEY;
    private final String SET_ACCOUNT_INFO_ENDPOINT = AUTH_ENDPOINT + "update?key=" + API_KEY;
    private final String DELETE_ACCOUNT_ENDPOINT = AUTH_ENDPOINT + "delete?key=" + API_KEY;

    private HttpURLConnection connection;

    // Sends GET http requests and returns a string response
    public String get(String urlString) {
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            System.out.println("Status: " + status);

            if (status > 299) {
                // Failure
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                // Successful
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            // Write the stream contents to the reponse
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (IOException e) {
            System.out.println(e);
        } finally {
            connection.disconnect();
        }

        return responseContent.toString();
    }

    // Sends POST or PUT request
    public String sendRequest(String type, String urlString, String payload) {
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        int status = 400;
        try {
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type);  //Should be "POST" or "PUT" 
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            status = connection.getResponseCode();
            System.out.println("Connection status: " + status);

            if (status > 299) {
                // Failure
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                // Successful
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            // Write the response to the string
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (IOException e) {
            System.out.println(e);
        } finally {
            connection.disconnect();
        }

        return responseContent.toString();
    }

    // Attempts a login request
    public String sendLoginRequest(String email, String password) {
        String payload = buildCredentialsPayload(email, password);
        String response = "";
        try {
            response = sendRequest("POST", SIGN_IN_ENDPOINT, payload);
        } catch (Exception e) {

        }
        return response;
    }

    // Creates JSON payloard for signing in and signing up
    private String buildCredentialsPayload(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
    }

    // Builds new user data JSON to send to the database
    // this should be updated as new data requirements are realized and the db schema is changed
    private String buildSignUpPayload(String first, String last, String email, String zip) {
        String newUserJson = "{\"firstName\":\"" + first + "\","
                + "\"lastName\":\"" + last + "\","
                + "\"email\":\"" + email + "\","
                + "\"zipCode\": \"" + zip + "\"}";
        return newUserJson;
    }

    // Query a user based on the userId
    public String getUserInfo(String userId) {
        String response = get(DB_ENDPOINT + "users/" + userId + ".json");
        return response;
    }

    // Attempts a signup request
    public String sendSignupRequest(String firstName, String lastName, String email, String zip, String password) {
        String response;
        String payload = buildCredentialsPayload(email, password);
        response = sendRequest("POST", SIGN_UP_ENDPOINT, payload);

        return response;
    }

    // Attempts to put user data into db
    public String putNewUserData(String userId, String firstName, String lastName, String email, String zip) {
        String payload = buildSignUpPayload(firstName, lastName, email, zip);
        String response = sendRequest("PUT", DB_ENDPOINT + "users/" + userId + ".json", payload);
        System.out.println(response);
        return response;
    }

    //methods to change password
    private String buildChangePassPayload(String tokenId, String password) {
        return "{\"idToken\":\"" + tokenId + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
    }

    public String changePassRequest(String tokenId, String password) {
        String response;
        String payload = buildChangePassPayload(tokenId, password);
        response = sendRequest("POST", SET_ACCOUNT_INFO_ENDPOINT, payload);
        return response;
    }

    //this method calls on other methods to essentially rewrite the entire user to update user-specified fields.
    public String putEditedUserData(User user) {
        Gson gson = new Gson();
        String payload = gson.toJson(user);
        String response = sendRequest("PUT", DB_ENDPOINT + "users/" + user.getId() + ".json", payload);
        System.out.println(response);
        return response;
    }

    public String deleteAccountRequest(String tokenId) {
        String response;
        String payload = "{\"idToken\":\"" + tokenId + "\"}";
        response = sendRequest("POST", DELETE_ACCOUNT_ENDPOINT, payload);
        return response;
    }

    // Attempts to put meeting into current user
    public String putNewMeeting(User user, Meeting meeting) {
        ArrayList<Meeting> meetings = user.getMeetings();
        meetings.add(meeting);
        Gson gson = new Gson();
        String payload = gson.toJson(meetings);
        String response = sendRequest("PUT", DB_ENDPOINT + "users/" + user.getId() + "/meetings.json", payload);

//        meeting.getAttendees().forEach((attendee) -> {
//            sendRequest("PUT", DB_ENDPOINT + "users/" + attendee + "/meetings.json", payload);
//        });

        return response;
    }

    //Replaces the meetings with the provided arraylist
    public String replaceMeetings(User user, ArrayList<Meeting> meetings) {
        String payload = new Gson().toJson(meetings);
        String response = sendRequest("PUT", DB_ENDPOINT + "users/" + user.getId() + "/meetings.json", payload);
        return response;
    }

    // Was having trouble mapping the JSON response to a user object, so getting a list of users first, then will fetch each user
    public ArrayList<String> getContacts() {
        String response = get(DB_ENDPOINT + "users.json?shallow=true&print=pretty");
        ArrayList<String> output = new ArrayList();
        boolean open = false;

        // Adds each string inside quote to the list
        String current = "";
        for (int i = 0; i < response.length(); i++) {
            if (response.charAt(i) == '\"') {
                open = !open;
                if (open == false) {
                    output.add(current);
                    current = "";
                }
                continue;
            }

            if (open) {
                current += response.charAt(i);
            }
        }

        return output;
    }

    public String getUserById(String id) {
        String response = get(DB_ENDPOINT + "users/" + id + ".json");
        return response;
    }
}
