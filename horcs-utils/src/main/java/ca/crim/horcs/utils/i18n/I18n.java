package ca.crim.horcs.utils.i18n;

public class I18n {

    private static I18n instance = new I18n();

    /**
     * Constructor
     */
    private I18n() {

    }

    public static I18n getInstance(Class<?> clazz) {
        return instance;
    }

    // tr
    public String tr(String text) {
        return text;
    }

    // tr
    public String trc(String context, String text) {
        return text;
    }

    // tr
    public String tr(String text, Object... args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                text = text.replace("{" + i + "}", String.valueOf(args[i]));
            }
        }
        return text;
    }

    public String trn(String text, String pluralText, long n) {
        if (n > 1) {
            return tr(pluralText);
        } else {
            return tr(text);
        }
    }

    public String trn(String text, String pluralText, long n, Object... args) {
        if (n > 1) {
            return tr(pluralText, args);
        } else {
            return tr(text, args);
        }
    }

    public String trnc(String context, String text, String pluralText, long n) {
        if (n > 1) {
            return tr(pluralText);
        } else {
            return tr(text);
        }
    }

    public String trnc(String context, String text, String pluralText, long n, Object... args) {
        if (n > 1) {
            return tr(pluralText, args);
        } else {
            return tr(text, args);
        }
    }
}
