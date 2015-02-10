package ca.crim.horcs.utils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Duplication du stream vers une deuxième destination : copyStream
 * 
 * @author bondst
 * 
 */
public class PrintStreamInterceptor extends PrintStream {

    private OutputStream copyStream;

    public PrintStreamInterceptor(OutputStream out, OutputStream copyStream) {
        super(out);
        this.copyStream = copyStream;
    }

    public void write(int b) {
        super.write(b);
        try {
            copyStream.write(b);
        } catch (IOException x) {
        }
    }

    public void write(byte buf[], int off, int len) {
        super.write(buf, off, len);
        try {
            copyStream.write(buf, off, len);
        } catch (IOException x) {
        }
    }

}
