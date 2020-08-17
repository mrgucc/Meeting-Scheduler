package scheduler.scenes;

import java.io.IOException;

import scheduler.widgets.*;
import scheduler.models.Controller;
import scheduler.models.User;
import scheduler.models.UserSecurity;
import scheduler.utilities.Constants;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class AccountView extends BorderPane {

    Button logoutButton = new Button("Log out");
    Button changePasswordButton = new Button("Change Password");
    Button editInfoButton = new Button("Edit Account Information");
    Button deleteAccountButton = new Button("Delete Account");
    Insets insets = new Insets(15);
    final BooleanProperty firstTime = new SimpleBooleanProperty(true);
    int WIDTH = 200;
    UserSecurity userSec = new UserSecurity();
    Text errorMessage = new Text("");

    public AccountView(User user) {
        super();

        errorMessage.setFill(Constants.TEXT_ERROR_COLOR);

        //Set buttons' attributes
        logoutButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().logout();
        });

        // --- change password --------------------------------------------
        //this removes the default blue highlight of the button. 
        //*note: it removes the highlight forever. it won't highlight blue if you click on it either.
        changePasswordButton.setStyle("-fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color; \n"
                + "    -fx-background-insets: 0, 1, 2;\n"
                + "    -fx-background-radius: 5, 4, 3;");

        changePasswordButton.setOnAction((ActionEvent e) -> {
            PasswordField passwordField = new PasswordField();
            PasswordField confirmPasswordField = new PasswordField();

            passwordField.setPromptText("Password");
            passwordField.setPrefWidth(WIDTH);
            confirmPasswordField.setPromptText("Re-enter password");
            confirmPasswordField.setPrefWidth(WIDTH);

            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Password Change Request");
            Button button = new Button("Save");
            button.setOnAction(f -> {
                try {
                    userSec.passwordValidation(passwordField.getText(), confirmPasswordField.getText());

                    String newPass = passwordField.getText();
                    String tokenId = user.getToken();

                    Controller.getInstance().changePassword(tokenId, newPass);

                    Stage popupwindow2 = new Stage();
                    popupwindow2.initModality(Modality.APPLICATION_MODAL);
                    popupwindow2.setTitle("Password Change Request");
                    VBox layout2 = new VBox(10);
                    Button closeButton = new Button("Close");
                    closeButton.setOnAction((ActionEvent g) -> {
                        popupwindow2.close();
                    });
                    layout2.setFillWidth(false);
                    layout2.getChildren().addAll(new Text("You've successfully changed your password.\n"), closeButton);
                    layout2.setAlignment(Pos.CENTER);
                    Scene scene2 = new Scene(layout2, 350, 200);
                    popupwindow2.setScene(scene2);
                    popupwindow2.showAndWait();
                    firstTime.setValue(true);
                    popupwindow.close();
                } catch (IOException ex) {
                    errorMessage.setText(ex.getMessage());
                }
            });
            VBox layout = new VBox(10);
            layout.setFillWidth(false);
            layout.getChildren().addAll(
                    new Text("Please enter your new password below.\n"),
                    passwordField,
                    confirmPasswordField,
                    errorMessage,
                    button
            );
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout, 350, 275);
            popupwindow.setScene(scene);

            passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue && firstTime.get()) {
                    layout.requestFocus();
                    firstTime.setValue(false);
                }
            });

            popupwindow.showAndWait();
        });

        // --- edit information --------------------------------------------
        //this removes the default blue highlight of the button. 
        //*note: it removes the highlight forever. it won't highlight blue if you click on it either.
        editInfoButton.setStyle("-fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color; \n"
                + "    -fx-background-insets: 0, 1, 2;\n"
                + "    -fx-background-radius: 5, 4, 3;");

        editInfoButton.setOnAction((ActionEvent e) -> {
            TextField firstNameField = new TextField();
            TextField lastNameField = new TextField();
            TextField emailTextField = new TextField();
            TextField zipCodeField = new TextField();

            firstNameField.setPromptText("First name");
            firstNameField.setPrefWidth(WIDTH);
            lastNameField.setPromptText("Last name");
            lastNameField.setPrefWidth(WIDTH);
            zipCodeField.setPromptText("ZIP code");
            zipCodeField.setPrefWidth(WIDTH);
            emailTextField.setPromptText("Email");
            emailTextField.setPrefWidth(WIDTH);
            
            //creating popup window for editting account info
            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Edit Account Information");
            Button button = new Button("Save");
            button.setOnAction(f -> {
                try {
                    userSec.updateAccountInputs(firstNameField.getText(),
                                                lastNameField.getText(),
                                                zipCodeField.getText(),
                                                emailTextField.getText());
                    
                    //method to edit user information
                    Controller.getInstance().editAccountInfo(
                            firstNameField.getText(),
                            lastNameField.getText(),
                            zipCodeField.getText(),
                            emailTextField.getText()
                    );

                    Stage popupwindow3 = new Stage();
                    popupwindow3.initModality(Modality.APPLICATION_MODAL);
                    popupwindow3.setTitle("Account Information Edited");
                    VBox layout3 = new VBox(10);
                    layout3.setFillWidth(false);
                    Button closeButton = new Button("Close");
                    closeButton.setOnAction((ActionEvent g) -> {
                        popupwindow3.close();
                    });
                    layout3.getChildren().addAll(new Text("You've successfully edited your account information.\n\n"
                            + "Your account information is now as follows:\n\n"
                            + "Full name: " + user.getFirstName() + " " + user.getLastName() + "\n"
                            + "Email: " + user.getEmail() + "\n"
                            + "Zipcode: " + user.getZipCode() + "\n"),
                            closeButton
                    );
                    layout3.setAlignment(Pos.CENTER);
                    Scene scene3 = new Scene(layout3, 350, 200);
                    popupwindow3.setScene(scene3);
                    popupwindow3.showAndWait();

                    firstTime.setValue(true);
                    popupwindow.close();

                } catch (IOException ex) {
                    errorMessage.setText(ex.getMessage());
//                    popupwindow.showAndWait();
                }

                firstTime.setValue(true);
                popupwindow.close();
            });
            VBox layout = new VBox(10);
            layout.setFillWidth(false);
            layout.getChildren().addAll(
                    new Text("Please edit your information below.\n"),
                    firstNameField,
                    lastNameField,
                    emailTextField,
                    zipCodeField,
                    errorMessage,
                    button
            );
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout, 350, 300);
            popupwindow.setScene(scene);

            firstNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue && firstTime.get()) {
                    layout.requestFocus();
                    firstTime.setValue(false);
                }
            });
            popupwindow.showAndWait();
        });

        // --- delete account --------------------------------------------
        //this removes the default blue highlight of the button. 
        //*note: it removes the highlight forever. it won't highlight blue if you click on it either.
        deleteAccountButton.setStyle("-fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color; \n"
                + "    -fx-background-insets: 0, 1, 2;\n"
                + "    -fx-background-radius: 5, 4, 3;");

        deleteAccountButton.setOnAction((ActionEvent e) -> {
            //creating popup window for editting account info
            Stage popupwindow = new Stage();
            popupwindow.initModality(Modality.APPLICATION_MODAL);
            popupwindow.setTitle("Delete Account");
            Button button = new Button("Delete Account");
            button.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);\n"
                    + "    -fx-font-weight: bold;\n"
                    + "    -fx-background-insets: 0;\n"
                    + "    -fx-text-fill: white;");
            button.setOnAction(f -> {
                try {
                    String tokenId = user.getToken();

                    Controller.getInstance().deleteAccount(tokenId);

                    Stage popupwindow2 = new Stage();
                    popupwindow2.initModality(Modality.APPLICATION_MODAL);
                    popupwindow2.setTitle("Delete Account Request");
                    VBox layout2 = new VBox(10);
                    Button closeButton = new Button("Close");
                    closeButton.setOnAction((ActionEvent g) -> {
                        Controller.getInstance().logout();
                        firstTime.setValue(true);
                        popupwindow.close();
                        popupwindow2.close();
                    });
                    layout2.setFillWidth(false);
                    layout2.getChildren().addAll(new Text("You've successfully deleted your account.\n"), closeButton);
                    layout2.setAlignment(Pos.CENTER);
                    Scene scene2 = new Scene(layout2, 350, 200);
                    popupwindow2.setScene(scene2);
                    popupwindow2.showAndWait();
                } catch (IOException ex) {
                    errorMessage.setText(ex.getMessage());
                }
            });
            VBox layout = new VBox(10);
            layout.setFillWidth(false);
            layout.getChildren().addAll(
                    new Text("Are you sure you'd like to delete your account?"),
                    new Text("This action cannot be undone.\n"),
                    button
            );
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout, 350, 200);
            popupwindow.setScene(scene);
            popupwindow.showAndWait();
        });

        //Set up a button that allows you to exit account view
        Button goDashboardButton = new Button("Return to Dashboard");
        goDashboardButton.setOnAction((ActionEvent e) -> {
            Controller.getInstance().goToScene("dashboard");
        });

        //Create the center node and children
        VBox center = new VBox();
        center.getChildren().addAll(
                new Text("\nName: " + user.getFullName()),
                new Text("E-mail: " + user.getEmail()),
                new Text("Zipcode: " + user.getZipCode() + "\n"),
                new Text("Would you like to edit your personal information?"),
                editInfoButton,
                new Text("\nWould you like to change your password?"),
                changePasswordButton,
                new Text("\nWould you like to delete your account?"),
                deleteAccountButton
        );

        //Create the left node and children
        VBox left = new VBox(5);
        left.getChildren().addAll(
                new Text("Personal Account Information:\t\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"),
                goDashboardButton,
                logoutButton
        );

        //Set the attributes of this scene
        AccountView.setMargin(left, insets);

        //Add the nodes to this object
        this.setTop(new SceneHeader("Account", user));
        this.setCenter(center);
        this.setLeft(left);
    }
}
