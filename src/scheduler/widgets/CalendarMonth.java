package scheduler.widgets;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.*;

// Shows a calendar based on the dateToDisplay. 
public class CalendarMonth extends GridPane {

    private LocalDate dateToDisplay = LocalDate.now();
    private YearMonth yearMonth;
    private final Text monthLabel = new Text("Error"); 


    public CalendarMonth() {
        super();

        this.setVgap(2);
        this.setHgap(2);
        this.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        rebuild();

    }

    //Adds the previous/forward buttons and month label to the grid
    private void buildHeader() {
        //Set the "previous month" button
        Button prevMonthButton = new Button("<");
        prevMonthButton.setOnAction((ActionEvent e) -> {
            dateToDisplay = dateToDisplay.minusMonths(1);
            rebuild();
        });
        this.add(prevMonthButton, 0, 0);
        setHalignment(prevMonthButton, HPos.CENTER); //Centers these objects

        //Set the month text label
        String month = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
        monthLabel.setText(month + " " + yearMonth.getYear());
        monthLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
        setColumnSpan(monthLabel, 5);
        setHgrow(monthLabel, Priority.ALWAYS);
        setHalignment(monthLabel, HPos.CENTER); //Centers these objects
        this.add(monthLabel, 1, 0);

        //Set the "next month" button
        Button nextMonthButton = new Button(">");
        nextMonthButton.setOnAction((ActionEvent e) -> {
            dateToDisplay = dateToDisplay.plusMonths(1);
            rebuild();
        });
        this.add(nextMonthButton, 6, 0);
        setHalignment(nextMonthButton, HPos.CENTER); //Centers these objects
    }

    //Adds the date buttons to the grid
    private void buildDateButtons() {
        int firstDayOfMonth = LocalDate.of(dateToDisplay.getYear(), dateToDisplay.getMonth(), 1).getDayOfWeek().getValue();

        int numDaysInMonth = yearMonth.lengthOfMonth();
        int numDaysInWeek = firstDayOfMonth - 1; //Set the initial value to avoid always first day of week is first of month
        int numWeeksAdded = 2; //starts at 2 because 0 is month label/buttons, and 1 is days of week

        for (int i = 0; i < numDaysInMonth; i++) {
            int date = i + 1;
            Button dateButton = new Button("" + date); //We can replace this with anything we want
            //add button method here
            dateButton.setMinSize(40, 40);

            this.add(dateButton, numDaysInWeek, numWeeksAdded);
            numDaysInWeek++;
            //We have completed adding to a full week, reset 
            if (numDaysInWeek % 7 == 0) {
                numWeeksAdded++;
                numDaysInWeek = 0;
            }
        }
    }

    //Adds the days of the week labels to row 1 (2nd row)
    private void buildDayLabels() {
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Text day = new Text(days[i]);
            setHalignment(day, HPos.CENTER); //Centers these objects
            this.add(day, i, 1);
        }
    }

    //Allows this object to rebuild all children at will
    private void rebuild() {
        this.getChildren().clear();
        yearMonth = YearMonth.of(dateToDisplay.getYear(), dateToDisplay.getMonth());
        buildHeader();
        buildDayLabels();
        buildDateButtons();
        setPadding(new Insets(15));
    }

}
