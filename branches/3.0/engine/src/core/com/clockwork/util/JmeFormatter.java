
package com.clockwork.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * More simple formatter than the default one used in Java logging.
 * Example output: 
 * INFO Display3D 12:00 PM: Display created.
 */
public class JmeFormatter extends Formatter {

    private Date calendar = new Date();
    private String lineSeperator;
    private MessageFormat format;
    private Object args[] = new Object[1];
    private StringBuffer store = new StringBuffer();

    public JmeFormatter(){
        lineSeperator = System.getProperty("line.separator");
        format = new MessageFormat("{0,time}");
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        // Get the current time and log it
        calendar.setTime(record.getMillis());
        args[0] = calendar;
        store.setLength(0);
        format.format(args, store, null);

        String classStr = null;
        try{
            classStr = Class.forName(record.getSourceClassName()).getSimpleName();
        } catch (ClassNotFoundException ex){
            // Simply trigger the exception in the compiler - no response needed.
        }
        
        sb.append(record.getLevel().getLocalizedName()).append(" ");
        sb.append(classStr).append(" ");
        sb.append(store.toString()).append(" ");
        sb.append(formatMessage(record)).append(lineSeperator);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            
            }
        }

        return sb.toString();
    }
}
