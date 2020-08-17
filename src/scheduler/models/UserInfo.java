package scheduler.models;

// This class exists so GSON can map the database response to this object GSON is limited because it can only create 
import java.util.ArrayList;

// new objects from JSON, not update existing objects. Since we have to make a call to the auth 
// and a call to the db, this class will serve as a temporary object for GSON to map to. 
// This object will then be passed directly to the User object where it can be consolidated
// As fields in the database change and grow, this must be updated to reflect it or else GSON will fail to map
// The fields must be named exactly the same as in the database (JSON response more specifically)
class UserInfo {

    private String firstName;
    private String lastName;
    private String email;
    private final ArrayList<Meeting> meetings = new ArrayList();
    private int zipCode;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getZipCode() {
        return zipCode;
    }

    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

}
