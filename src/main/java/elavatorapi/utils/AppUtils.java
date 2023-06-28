package elavatorapi.utils;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public class AppUtils {

    public static String elevator(int num) {

        String elevator = "ADMIN";
        switch (num) {
            case 1 -> elevator = "A";
            case 2 -> elevator = "B";
            case 3 -> elevator = "C";
            case 4 -> elevator = "D";
            case 5 -> elevator = "E";
            case 6 -> elevator = "F";
            default -> {
            }
        }
        return elevator;
    }
}
