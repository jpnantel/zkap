package ca.crim.horcs.utils.io;

import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Contains functions to append logs stream from ETL into the specified writer
 * 
 * @author bondst
 * 
 */
public class LogsHelper {

    protected static class WriterWrap extends Writer {

        private Writer w;

        public WriterWrap(Writer w) {
            this.w = w;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            w.write(cbuf, off, len);
        }

        @Override
        public void flush() throws IOException {
            w.flush();
        }

        @Override
        public void close() throws IOException {
            // Nothing
        }

    }

    public static Appender addAppender(String packageName, Writer writer, String pattern) {
        Logger log4jLogger = Logger.getLogger(packageName);
        Layout layout = new PatternLayout(pattern);
        Appender appender = new ThreadLocalWriterAppender(layout, new WriterWrap(writer));
        log4jLogger.addAppender(appender);
        return appender;
    }

    public static void removeAppender(String packageName, Appender appender) {
        Logger log4jLogger = Logger.getLogger(packageName);
        log4jLogger.removeAppender(appender);
    }

}
