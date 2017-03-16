
package com.clockwork.system.ios;

import com.clockwork.util.CWFormatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 */
public class IosLogHandler  extends Handler {

    CWFormatter formatter = new CWFormatter();

    public IosLogHandler() {
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().equals(Level.SEVERE)) {
            System.err.println(formatter.formatMessage(record));
        }
        else if (record.getLevel().equals(Level.WARNING)) {
            System.err.println(formatter.formatMessage(record));
        }
        else {
            System.err.println(formatter.formatMessage(record));
        }
    }

    @Override
    public void flush() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws SecurityException {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
