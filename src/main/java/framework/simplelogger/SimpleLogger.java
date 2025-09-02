/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.simplelogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 *
 * @author Djurkovic
 */
public class SimpleLogger {

    public static LogLevel LOG_SENSITIVITY = LogLevel.LOG_INFO;
    private static Consumer<String> sink = System.out::println;

    public static void setSink(Consumer<String> newSink) {
        sink = newSink;
    }

    public static void log(LogLevel level, String msg) {
        if(LOG_SENSITIVITY.ordinal() > level.ordinal()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lvl = switch (level) {
            case LOG_INFO -> "[INFO] ";
            case LOG_WARN -> "[WARN] ";
            case LOG_FATAL -> "[FATAL] ";
            case LOG_ERROR -> "[ERROR] ";
        };
        sink.accept(lvl + now.format(f) + " - " + msg);
    }
}
