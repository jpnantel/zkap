package ca.crim.horcs.utils.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageBundle {

    private ResourceBundle rb;

    public MessageBundle(String resource) {
        rb = ResourceBundle.getBundle(resource, Locale.getDefault());
    }

    public String getString(String key, Object... args) {
        String str = rb.getString(key);
        if (str != null && args != null) {
            for (int i = 0; i < args.length; i++) {
                str = str.replaceAll("\\{" + i + "\\}", args[i].toString());
            }
        } else if (str == null) {
            str = key;
        }
        return str;
    }

    public boolean containsKey(String key) {
        return rb.getString(key) != null;
    }

    public Enumeration<String> getKeys() {
        return rb.getKeys();
    }

    public Locale getLocale() {
        return rb.getLocale();
    }

}
