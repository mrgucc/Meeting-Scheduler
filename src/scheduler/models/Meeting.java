package scheduler.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class Meeting {

    public final String id;     // ID is created on construction, no need for getter?
    private String subject = "No subject";
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String organizerId = "FAKEUSERID";
    private ArrayList<String> attendeeIds = new ArrayList();

    public Meeting() {
        id = UUID.randomUUID().toString();
    }

    public Meeting(LocalDate date, int startHour, int startMin, int endHour, int endMin, String organizerId, String subject, ArrayList<String> attendees) {
        id = UUID.randomUUID().toString();
        this.organizerId = organizerId;
        this.subject = subject;
        this.attendeeIds = attendees;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        startDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), startHour, startMin);
        endDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), endHour, endMin);
    }

    public String getButtonDisplay() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        String formattedStart = startDateTime.format(formatter);
        String formattedEnd = endDateTime.format(formatter);

        String output = getSpan() > 1 ? subject + "\n" + formattedStart + " to " + formattedEnd : subject;
        return output;
    }

    // Gets the Y value for the grid placement based on 15 min intervals
    public int getRow(boolean start) {

        LocalDateTime dateTimeToUse = start ? startDateTime : endDateTime;
        LocalDateTime target = LocalDateTime.of(dateTimeToUse.getYear(), dateTimeToUse.getMonth(), dateTimeToUse.getDayOfMonth(), 00, 00);
        int row = 0;
        for (int i = 1; i < 97; i++) {

            if (target.isEqual(dateTimeToUse)) {
                row = i;
                break;
            } else {
                target = target.plusMinutes(15);
            }
        }
        return row;
    }

    public int getDay() {
        return startDateTime.getDayOfWeek().getValue();
    }

    public int getStartingRow() {
        return getRow(true);
    }

    // The row span for this meeting based on the duration
    public int getSpan() {
        int startRow = getRow(true);
        int endRow = getRow(false);
        int span = endRow - startRow;
        return span;
    }

    public LocalDate getStartDate() {
        return startDateTime.toLocalDate();
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDate getEndDate() {
        return endDateTime.toLocalDate();
    }

    public ArrayList<String> getAttendees() {
        return attendeeIds;
    }

    public String getOrganizer() {
        return organizerId;
    }

    public String getSubject() {
        return subject;
    }

}
