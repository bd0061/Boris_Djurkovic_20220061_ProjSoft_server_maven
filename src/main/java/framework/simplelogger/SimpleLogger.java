/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.simplelogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Djurkovic
 */
public class SimpleLogger {

    public static LogLevel LOG_SENSITIVITY = LogLevel.LOG_INFO;


    public static void log(LogLevel level, String msg) {
        if(LOG_SENSITIVITY.ordinal() > level.ordinal()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lvl = switch (level) {
            case LogLevel.LOG_INFO -> "[INFO] ";
            case LogLevel.LOG_WARN -> "[WARN] ";
            case LogLevel.LOG_FATAL -> "[FATAL] ";
            case LogLevel.LOG_ERROR -> "[ERROR] ";
        };
        System.out.println(lvl + now.format(f) + " - " + msg);
    }
}
