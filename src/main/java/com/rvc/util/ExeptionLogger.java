package com.rvc.util;

import java.io.*;
import java.util.Calendar;

public class ExeptionLogger {

    public static void saveException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String s = sw.toString(); // stack trace as a string

        Calendar cal = Calendar.getInstance();

        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("log" + cal.getTime() + ".txt"));
            file.write(s);
            file.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}
