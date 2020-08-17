package scheduler.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class UserSecurity {

    public UserSecurity() {

    }

    // Validates all fields are present while logging in
    public void loginCheck(String email, String password) throws IOException {

        if (password.equals("") && email.equals("")) {
            throw new IOException("Please enter a valid email and password");
        }
        if (password.equals("")) {
            throw new IOException("Please enter a password");
        }
        if (email.equals("")) {
            throw new IOException("Please enter an email account");
        }
    }

    public void emailValidation(String email) throws IOException {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        if (!emailPattern.matcher(email).find()) {
            throw new IOException("Invalid email format");
        }
    }

    // validates all inputs for sign up process
    public void accountInputs(String fName, String lName, String zip, String email,
            String pWord, String pWord2) throws IOException {

        String[] inputs = new String[]{fName, lName, zip, email, pWord, pWord2};
        ArrayList<String> missingInputs = new ArrayList();

        for (String input : inputs) {
            if (input.equals("")) {
                missingInputs.add(input);
            }
        }
        if (missingInputs.size() > 0) {
            String errorMsg = missingInputs.size() + " missing field(s) - all fields are required";
            throw new IOException(errorMsg);
        }

        passwordValidation(pWord, pWord2);

    }

    public void updateAccountInputs(String fName, String lName, String zip, String email) throws IOException {

        String[] inputs = new String[]{fName, lName, zip, email};
        ArrayList<String> missingInputs = new ArrayList();

        for (String input : inputs) {
            if (input.equals("")) {
                missingInputs.add(input);
            }
        }
        if (missingInputs.size() > 0) {
            String errorMsg = missingInputs.size() + " missing field(s) - all fields are required";
            throw new IOException(errorMsg);
        }

        emailValidation(email);

    }

    // validates user password to meet application requirements
    public void passwordValidation(String pWord, String pWord2) throws IOException {

        Pattern specialCharPattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePattern = Pattern.compile("[A-Z]");
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Pattern digitCasePattern = Pattern.compile("[0-9]");

        String errorString = "The following errors were found:\n";
        StringBuffer sb = new StringBuffer(errorString);

        boolean flag = true;

        if (!pWord.equals(pWord2)) {
            throw new IOException("Passwords do not match");
        }
        if (pWord.length() < 8) {
            sb.append(" -Password is not 8 chars long\n");
            flag = false;
        }
        if (!specialCharPattern.matcher(pWord).find()) {
            sb.append(" -Password does not contain special characters\n");
            flag = false;
        }
        if (!UpperCasePattern.matcher(pWord).find()) {
            sb.append(" -Password does not contain upper case letters\n");
            flag = false;
        }
        if (!lowerCasePattern.matcher(pWord).find()) {
            sb.append(" -Password does not contain lower case letters\n");
            flag = false;
        }
        if (!digitCasePattern.matcher(pWord).find()) {
            sb.append(" -Password does not contain numbers\n");
            flag = false;
        }

        if (flag == false) {
            throw new IOException(sb.toString());
        }
    }
}
