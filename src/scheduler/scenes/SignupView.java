package scheduler.scenes;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import scheduler.utilities.Constants;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import scheduler.models.Controller;
import scheduler.models.User;
import scheduler.services.Firebase;


public class SignupView extends VBox {

    TextField firstNameField = new TextField();
    TextField lastNameField = new TextField();
    TextField emailTextField = new TextField();
    TextField zipCodeField = new TextField();
    PasswordField passwordField = new PasswordField();
    PasswordField confirmPasswordField = new PasswordField();
    Text errorMessage = new Text("");
    Button submitButton = new Button("Submit");
    Button goLoginButton = new Button("I already have an account");
    Label passLabel = new Label("Note: Valid passwords must be 8 or more \n"
                               +"characters long and contain upper case, \n"
                               +"lower case, numbers, and special characters. \n");
    int WIDTH = 200;
    boolean hasAccount;
    
    public SignupView() {
        super(5);

        //First name field
        firstNameField.setPromptText("First name");
        firstNameField.setPrefWidth(WIDTH);

        //Last name field
        lastNameField.setPromptText("Last name");
        lastNameField.setPrefWidth(WIDTH);

        //ZIP code field
        zipCodeField.setPromptText("ZIP code");
        zipCodeField.setPrefWidth(WIDTH);

        //Email field
        emailTextField.setPromptText("Email");
        emailTextField.setPrefWidth(WIDTH);

        //Password field
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(WIDTH);

        //Confirm password field
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefWidth(WIDTH);

        //Set error message color
        errorMessage.setFill(Constants.TEXT_ERROR_COLOR);

        //Submit button
        submitButton.setStyle(Constants.MAIN_THEME_COLOR);
        submitButton.setPrefWidth(WIDTH);
        submitButton.setOnAction((ActionEvent e) -> {
            errorMessage.setText("");
            String inputEmail = emailTextField.getText();
            System.out.println(inputEmail);
            
            ArrayList<String> ids = new ArrayList();
            ids = Firebase.getInstance().getContacts();
            Gson gson = new Gson();
            
            Iterator idsIt = ids.iterator();
            while (idsIt.hasNext()) {
                String id = (String) idsIt.next();
                String response = Firebase.getInstance().getUserById(id);
                User user = gson.fromJson(response, User.class);
                String userEmail = user.getEmail();
                
                if (userEmail.equals(inputEmail)) {
                    hasAccount = true;
                    errorMessage.setFill(Constants.TEXT_ERROR_COLOR);
                    errorMessage.setText("The email account you entered already has a Super Scheduler account.");
                    break; //end while loop because email match has been found. user cannot signup.
                } else { 
                    hasAccount = false; 
                } //end else
            }//end while idsIt.hasNext()
            
            if (hasAccount == false) {
                try {
                    Controller.getInstance().signUp(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        zipCodeField.getText(),
                        emailTextField.getText(),
                        passwordField.getText(),
                        confirmPasswordField.getText());
		} catch (IOException ex) {
                    errorMessage.setText(ex.getMessage());
		}//end try-catch
            }//end if hasAccount == false

        });

        //Back to login button
        goLoginButton.setPrefWidth(WIDTH);
        goLoginButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("login");
        });

        //Set the VBox and add the children
        this.setFillWidth(false);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(
                new Text("Sign up here. It's quick and easy!\n"),
                firstNameField,
                lastNameField,
                zipCodeField,
                emailTextField,
                passwordField,
                confirmPasswordField,
                errorMessage,
                submitButton,
                goLoginButton,
                passLabel
        );
    }
}