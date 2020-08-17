package scheduler.widgets;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import scheduler.models.Controller;
import scheduler.models.User;
import scheduler.scenes.LoginView;
import scheduler.utilities.Constants;

public class Inviter extends Stage {

    public Inviter(MeetingDetail meetingPanel) {
        setMinHeight(600);
        setMinWidth(1050);
        setTitle("Super Scheduler");
        setScene(new Scene(new AttendeesView(meetingPanel, this)));
        show();
    }
}

class AttendeesView extends BorderPane {

    Insets insets = new Insets(15);
    TextField searchField = new TextField();
    ArrayList<User> searchResults = new ArrayList();
    ArrayList<User> addedUsers = new ArrayList();
    VBox addedUsersGui = new VBox();
    ScrollPane resultsScroller = new ScrollPane();
    MeetingDetail meetingPanel;

    public AttendeesView(MeetingDetail meetingPanel, Stage window) {
        super();

        this.meetingPanel = meetingPanel;
        this.addedUsers = meetingPanel.attendeesToAdd;

        Text label = new Text("Invite to Meeting");
        label.setFont(Constants.SCENE_TITLE_FONT);

        // Top elements----------------------      
        HBox top = new HBox(label);
        top.setPadding(insets);

        // Center elements-------------------        
        VBox center = new VBox();
        center.setSpacing(5);
        center.setPadding(insets);

        HBox searchFieldAndButton = new HBox();
        Button searchButton = new Button("Search");
        searchButton.setDefaultButton(true);
        searchButton.setStyle("-fx-base: white");
        searchFieldAndButton.setSpacing(5);
        searchFieldAndButton.getChildren().addAll(searchField, searchButton);

        addedUsersGui.setPadding(insets);
        addedUsersGui.setSpacing(3);

        ScrollPane attendeeScroller = new ScrollPane();
        attendeeScroller.setContent(addedUsersGui);
        attendeeScroller.setFocusTraversable(false);
        attendeeScroller.setStyle("-fx-background-color:transparent;");
        attendeeScroller.setFitToWidth(true);

        center.getChildren().addAll(
                new Text("Search for others to attend your meeting using first name, last name, email, or ZIP code"),
                searchFieldAndButton,
                new Text("\n\n"),
                attendeeScroller
        );

        // Right elements--------------------
        VBox right = new VBox(new Text("Search results"));
        VBox results = new VBox();
        results.setPadding(insets);
        results.setPrefWidth(250);
        results.setSpacing(5);

        resultsScroller.setContent(results);
        resultsScroller.setFocusTraversable(false);
        resultsScroller.setStyle("-fx-background-color:transparent;");
        resultsScroller.setPrefWidth(270);

        right.getChildren().add(resultsScroller);

        // Set search button functionality        
        searchButton.setOnAction((a) -> {
            searchResults = Controller.getInstance().searchAttendees(searchField.getText());
            results.getChildren().clear();

            if (searchResults.size() > 0) {
                ArrayList<Button> foundUsers = buildButtonsFromSearchResults(searchResults);
                results.getChildren().addAll(foundUsers);
            } else {
                results.getChildren().add(new Text("No results found"));
            }
        });

        // Bottom elements--------------------
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-base: " + Constants.MAIN_THEME_COLOR);
        saveButton.setOnAction((a) -> {
            meetingPanel.attendeesToAdd = addedUsers;
            
            
            
            window.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((a) -> {
            window.close();
        });

        HBox bottom = new HBox();
        bottom.setSpacing(5);
        bottom.setPadding(insets);
        bottom.getChildren().addAll(saveButton, cancelButton);

        // Add all the elements
        setTop(top);
        setCenter(center);
        setRight(right);
        setBottom(bottom);
        
        refreshAddedUsersGui();

    }

    private ArrayList<Button> buildButtonsFromSearchResults(ArrayList<User> contacts) {
        ArrayList<Button> found = new ArrayList();
        contacts.forEach((c) -> {
            found.add(buildUserButton(c));
        });
        return found;
    }

    private Button buildUserButton(User user) {
        Button b = new Button(user.getFullName() + "\n" + user.getEmail());
        b.setMinWidth(225);
        b.setAlignment(Pos.BASELINE_LEFT);
        b.setStyle("-fx-base: " + Constants.MAIN_THEME_COLOR);
        b.setOnAction((a) -> {
            if (!addedUsers.contains(user)) {
                addedUsers.add(user);
                refreshAddedUsersGui();
            }
        });
        return b;
    }

    private void refreshAddedUsersGui() {
        addedUsersGui.getChildren().clear();
        addedUsers.forEach((u) -> {
            addedUsersGui.getChildren().add(getUserAddedGui(u));
        });
    }

    private HBox getUserAddedGui(User user) {

        Button userAddedButton = new Button(user.getFullName());
        userAddedButton.setMinWidth(120);
        userAddedButton.setMaxWidth(120);
        userAddedButton.setAlignment(Pos.CENTER_LEFT);
        userAddedButton.setFocusTraversable(false);
        userAddedButton.setStyle("-fx-base: transparent; -fx-text-fill: black");

        // Build the availability display
        HBox availDisplay = new HBox();
        availDisplay.setSpacing(1);
        boolean[] availability = user.buildSelectedDayAvailability(meetingPanel.datePicker.getValue());
        for (boolean a : availability) {
            Rectangle rect = new Rectangle();
            rect.setWidth(4);
            rect.setHeight(25);
            Color clr = a ? Color.web(Constants.MAIN_THEME_COLOR) : Color.GREY;
            rect.setFill(clr);
            availDisplay.getChildren().add(rect);
        }

        // Build the availability display
        Button removeButton = new Button("Remove");
        removeButton.setAlignment(Pos.CENTER_LEFT);
        removeButton.setFocusTraversable(false);
        removeButton.setOnAction((a) -> {
            addedUsers.remove(user);
            refreshAddedUsersGui();
        });

        // Add items to the output
        HBox output = new HBox();
        output.setSpacing(5);
        output.setAlignment(Pos.CENTER);
        output.getChildren().addAll(
                userAddedButton,
                availDisplay,
                removeButton
        );
        return output;
    }

}
