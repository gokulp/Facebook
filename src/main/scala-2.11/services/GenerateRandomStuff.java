package services;

import java.util.GregorianCalendar;
import java.util.Random;
//import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by gokul on 11/30/15.
 */
public class GenerateRandomStuff {
    public static String getDOB(){
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(1957, 1997);
        gc.set(gc.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        return(gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
    }

    public static String getEmail() {
        return("abcdef@facebook.com");
    }
    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
    public static String getGender(int x){
        if (x == 0)
            return "Male";
        return "Female";
    }
}
