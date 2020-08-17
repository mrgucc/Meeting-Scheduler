package scheduler.widgets;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import scheduler.models.Meeting;
import scheduler.utilities.Constants;

public class CalendarWeek extends VBox {

    private LocalDate dateToDisplay = LocalDate.now();
    private YearMonth yearMonth;
    private final Text weekLabel = new Text("Error"); //Ignore NetBeans, this should not be final.

    String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    GridPane grid = new GridPane();
    Text headerText = new Text("Error displaying header");
    ScrollPane scroll = new ScrollPane();
    MeetingDetail meetingDetail;

    HBox header = new HBox();
    ArrayList<Meeting> meetings;

    public CalendarWeek(ArrayList<Meeting> meetings, MeetingDetail meetingDetail) {
        headerText.setFont(Constants.SUB_TITLE_FONT);
        this.meetings = meetings;
        this.meetingDetail = meetingDetail;
        setGridAttributes();
        rebuild();
        scroll.setVvalue(.3); //Give some initial scroll so user doesn't have to scroll down
    }

    //Add time labels to the base grid
    private void buildTimeLabels() {
        for (int hour = 0; hour < 24; hour++) {
            Text timeText = new Text(hour + ":00");
            GridPane.setValignment(timeText, VPos.TOP);
            int labelRow = hour == 0 ? 1 : hour * 4 + 1;      //The y should be placed every 4 (quarter-hours) or on 0
            grid.add(timeText, 0, labelRow);
        }
    }

    //Builds the base of the grid
    private void buildBasePane() {
        //Add day labels and the quarter-hour buttons
        for (int day = 0; day < 7; day++) {
            LocalDate dateOfDay = getPreviousMonday().plusDays(day);
            String dayText = dateOfDay.getDayOfMonth() + "\n" + days[day];
            grid.add(new Text(dayText), day + 1, 0);

            LocalDateTime blockDate = dateOfDay.atStartOfDay();

            for (int row = 1; row < 97; row++) {
                QuarterHourTimeBlock block = new QuarterHourTimeBlock(blockDate, meetingDetail);
                blockDate = blockDate.plusMinutes(15);
                grid.add(block, day + 1, row);
            }
        }

        //Set the scrollpane and add the header and scrollpane to this object
        scroll.setFocusTraversable(false);
        scroll.setContent(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;");
        getChildren().add(scroll);

    }

    private void buildMeetings() {

        for (Meeting meeting : meetings) {

            LocalDate start = meeting.getStartDate();

            // If start date is equalto or after the previous monday, add it to this grid
            if ((start.isEqual(getPreviousMonday()) || start.isAfter(getPreviousMonday())) && start.isBefore(getNextMonday())) {

                MeetingBlock meetingBtn = new MeetingBlock(meeting, meetingDetail);
                meetingBtn.setPrefWidth(80); //This gets overwritten by the setmaxsize
                meetingBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);  //allow button to grow      
                grid.add(meetingBtn, meeting.getDay(), meeting.getStartingRow(), 1, meeting.getSpan());

            }
        }
    }

    private void buildHeader() {
        //Set the "previous week" button
        Button prevWeekButton = new Button("<");
        prevWeekButton.setFocusTraversable(false);
        prevWeekButton.setOnAction((ActionEvent e) -> {
            dateToDisplay = dateToDisplay.minusWeeks(1);
            rebuild();
        });
        header.getChildren().add(prevWeekButton);

        //Set the "next week" button
        Button nextWeekButton = new Button(">");
        nextWeekButton.setFocusTraversable(false);
        nextWeekButton.setOnAction((ActionEvent e) -> {
            dateToDisplay = dateToDisplay.plusWeeks(1);
            rebuild();
        });
        header.getChildren().add(nextWeekButton);

        //Set the month text label
        String month = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
        weekLabel.setText("Week of " + getPreviousMonday().getDayOfMonth() + " " + month + " " + yearMonth.getYear());
        weekLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));

        header.getChildren().add(weekLabel);
        header.setSpacing(5);
        header.setAlignment(Pos.CENTER_LEFT);
        getChildren().add(header);

    }

    //Allows this object to rebuild all children 
    private void rebuild() {
        // current yearmonth may have been changed by the button. Update here then rebuild children
        double currentScroll = scroll.getVvalue();
        yearMonth = YearMonth.of(dateToDisplay.getYear(), dateToDisplay.getMonth());
        removeAllChildren();
        buildHeader();
        buildTimeLabels();
        buildBasePane();
        buildMeetings();
        scroll.setVvalue(currentScroll);
    }

    private void removeAllChildren() {
        meetingDetail.clear();
        this.getChildren().clear();
        header.getChildren().clear();
        grid.getChildren().clear();
        scroll.setContent(null);
    }

    // Should only be called once and not on every rebuild
    private void setGridAttributes() {
        //Set width resize ability to each day column
        for (int j = 0; j < 8; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (j > 0) {
                cc.setHgrow(Priority.ALWAYS);   // allow column to grow
                cc.setFillWidth(true);          // ask timeblocks to fill space for column
            }
            grid.getColumnConstraints().add(cc);
        }
        setPadding(new Insets(15));
    }

    // If today is not monday, gets the previous monday to display as week start
    private LocalDate getPreviousMonday() {
        if (dateToDisplay.getDayOfWeek() != DayOfWeek.MONDAY) {
            LocalDate prevMonday = dateToDisplay.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
            return prevMonday;
        } else {
            return dateToDisplay;
        }
    }

    private LocalDate getNextMonday() {
        LocalDate monday = dateToDisplay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        return monday;
    }
}
