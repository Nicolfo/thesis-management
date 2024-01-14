package it.polito.se2.g04.thesismanagement.notification;


public class EmailConstants {
    private EmailConstants() {
        throw new IllegalStateException("Utility class");
    }
    public static final String GREETING_FORMULA = "Dear";
    public static final String HTML_LINE_BREAK = "<br>";
    public static final String HTML_ADD_ICON = "new.png";
    public static final String NEW_APPLICATION_RECEIVED = "A new application has been received";
    public static final String CO_SUPERVISOR_ASSIGNED = "\" for which you are assigned as co-supervisor.";
}
