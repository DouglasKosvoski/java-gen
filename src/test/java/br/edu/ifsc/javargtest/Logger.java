package br.edu.ifsc.javargtest;

/**
 * A logging utility class for displaying messages based on severity levels.
 * The logging can be configured to show only messages with a certain level of severity or higher.
 */
public class Logger {

    /**
     * Enum representing different levels of message severity.
     */
    public enum Severity {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    // The current logging level; only messages at this level or higher will be shown.
    private static Severity currentLogLevel = Severity.ERROR;

    /**
     * Displays a message if its severity level is equal to or higher than the current log level.
     *
     * @param severity the severity level of the message.
     * @param message  the message to be logged.
     */
    public static void log(Severity severity, String message) {
        if (currentLogLevel.ordinal() >= severity.ordinal()) {
            System.out.println(message);
        }
    }

    /**
     * Sets the logging level. Only messages with a severity equal to or higher than this level will be shown.
     *
     * @param severity the severity level to be set.
     */
    public static void setLogLevel(Severity severity) {
        currentLogLevel = severity;
    }
}
