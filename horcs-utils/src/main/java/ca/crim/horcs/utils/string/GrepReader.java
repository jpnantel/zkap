package ca.crim.horcs.utils.string;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * Un {@code reader} qui ne considère que les lignes satisfaisant une expression
 * régulière.
 * 
 * Pour des exemples d'utilisation, consultez {@code GrepReaderTest}.
 * 
 * Attention, cette classe n'est pas thread-safe.
 * 
 * @author patryal
 */
public class GrepReader extends Reader {

    public enum Mode {
        NORMAL, INVERTED
    };

    /** Reader interne. */
    private Reader reader;

    /** Tampon pour la prochaine ligne. */
    private StringBuffer buffer = new StringBuffer();

    /** Inverse la sélection. */
    private boolean invert = false;

    private Pattern pattern;

    /**
     * @param in
     *            Le flux à filtrer.
     * @param pattern
     *            L'expression régulière servant de test pour les lignes. Le
     *            caractère de fin de ligne ne fait pas partie de la chaine
     *            testé.
     * @param mode
     *            Mode d'appariement.
     */
    public GrepReader(Reader in, Pattern pattern, Mode mode) {
        this.reader = in;
        this.pattern = pattern;
        this.invert = mode == Mode.INVERTED;
    }

    /**
     * @see GrepReader(Reader, Pattern, Mode)
     */
    public GrepReader(Reader in, Pattern pattern) {
        this(in, pattern, Mode.NORMAL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        boolean eof = false;

        // remplit le tampon avec assez de caractère pour la lecture
        while (buffer.length() < off + len) {
            String line = nextLine();
            if (line != null) {
                buffer.append(line);
            } else {
                eof = true;
                break;
            }
        }

        // saute off caractères dans le tampon
        int realOffset = Math.min(off, buffer.length());
        buffer.delete(0, realOffset);

        // copie len caractères dans cbuf
        int realLen = Math.min(Math.min(len, cbuf.length), buffer.length());

        System.arraycopy(buffer.toString().toCharArray(), 0, cbuf, 0, realLen);
        buffer.delete(0, realLen);

        // véfirie si c'est la fin du fichier et que rien n'a été lu
        int all = realOffset + realLen;
        return (all == 0 && eof) ? -1 : all;
    }

    /**
     * @return La prochaine ligne satisfaisant {@code pattern} et {@code invert}
     *         .
     * @throws IOException
     */
    private String nextLine() throws IOException {
        String line = null;

        do {
            line = readLine();
        } while (line != null && pattern.matcher(line.trim()).matches() == invert);

        return line;
    }

    /**
     * @return La prochaine ligne du flux ({@literal \n} inclu).
     * @throws IOException
     */
    private String readLine() throws IOException {
        StringBuilder line = new StringBuilder();

        int c;
        while ((c = reader.read()) != -1) {
            line.append((char) c);

            if (c == '\n') {
                break;
            }
        }
        int trim = line.lastIndexOf(";");
        if (trim != -1 && line.length() > 0) {
            line.setCharAt(trim, '\n');
            return line.substring(0, trim + 1);
        }
        return null;
    }
}
