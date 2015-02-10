package ca.crim.horcs.utils.net;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkUtils {

    private static Logger logger = Logger.getLogger(NetworkUtils.class.getName());

    public static String getMacAddress() {
        try {
            List<String> macs = new ArrayList<String>();
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();

            if (networks != null) {
                while (networks.hasMoreElements()) {
                    NetworkInterface network = networks.nextElement();
                    byte[] mac = network.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        String macAdr = sb.toString();
                        if (macAdr != null && !macAdr.trim().equals("")) {
                            macs.add(macAdr.trim());
                        }
                    }
                }
            }

            // Si la liste n'est pas vide, prendre la dernière adresse (s'il y a
            // plusieurs interfaces réseau) après avoir trié la liste pour
            // éviter d'utiliser une adresse synthétique du style
            // 00-00-00-00-00-00-00-00
            if (macs.size() > 0) {
                Collections.sort(macs);
                return macs.get(macs.size() - 1);
            }
        } catch (SocketException e) {
            logger.log(Level.WARNING, "Erreur lors de la récupération des interfaces réseau.", e);
        }
        return "";
    }
}
