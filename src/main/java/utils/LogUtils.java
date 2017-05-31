package utils;
/**
 * A class used to log meessages in the application.
 * For simplicity using stub implementations of printing to System.out, can be integrated with proper log packages
 * like Java Logger or apache commons logging etc
 */
public class LogUtils{

    /**
     * Logs a debug message. For simplicity using System.out.println().
     * In real world will integrate with proper logging package like Java Logger or apache commong logging etc
     */
    public static void logDebug(String msg){
       //TODO: Integrate with proper log package 
       System.out.println(msg);
    }

    /**
     * Logs an error message. For simplicity using System.out.println().
     * In real world will integrate with proper logging package like Java Logger or apache commong logging etc
     */
    public static void logError(String msg){
       //TODO: Integrate with proper log package 
       System.out.println("*********** " + msg + " *******************");
    }
}