package org.example.securin.utility;

public class DataCleaner {

    public static Float cleanFloat(Object value) {
        if(value == null) return null;

        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer cleanInteger(Object value) {
        if(value == null) return null;

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
