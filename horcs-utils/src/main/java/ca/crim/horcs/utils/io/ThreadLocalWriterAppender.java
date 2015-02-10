package ca.crim.horcs.utils.io;

import java.io.Writer;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A WriterAppender used only for the current thread
 * 
 * @author bondst
 * 
 */
public class ThreadLocalWriterAppender extends WriterAppender {

    String threadName;

    public ThreadLocalWriterAppender(Layout layout, Writer writer) {
        super(layout, writer);
        this.threadName = Thread.currentThread().getName();
    }

    @Override
    public synchronized void doAppend(LoggingEvent arg0) {
        if (threadName.equals(Thread.currentThread().getName())) {
            super.doAppend(arg0);
        }
    }

}
