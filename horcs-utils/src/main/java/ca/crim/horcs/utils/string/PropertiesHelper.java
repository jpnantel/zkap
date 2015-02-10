package ca.crim.horcs.utils.string;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe utilitaire pour accéder aux propriétés système. Évite des constantes
 * dans le code.
 * 
 * @author nanteljp
 * 
 */
public class PropertiesHelper {

    private static final String PROPERTY_USERNAME = "ca.crim.horcs.username";

    private static final String PROPRETY_VERSION_PATH = "ca.crim.horcs.version";

    private static final String PROPERTY_LICENSE = "ca.crim.horcs.license";

    private static final String PROPERTY_SERVER_URL = "ca.crim.horcs.server";

    private static final String PROPERTY_OUTPUT_FILE = "ca.crim.horcs.output";

    private static final String PROPERTY_TASK_TOKEN = "ca.crim.horcs.taskToken";

    private static final String PROPERTY_TASK_PARAMS = "ca.crim.horcs.task.parameters";

    private static final String PROPERTY_PROXY_HOST = "proxyHost";

    private static final String PROPERTY_PROXY_PORT = "proxyPort";

    // Pour info seulement; utilisable pour le démarrage de l'agent. Pour
    // spécifier un URL différent pour accèder aux versions de l'application et
    // ne pas avoir à utiliser les vieux serveurs et leur path hardcodés.
    // private static final String PROPERTY_SERVER_VERSION_ACCESS_URL =
    // "ca.crim.horcs.server.version";

    public static String getUsername() {
        return System.getProperty(PROPERTY_USERNAME);
    }

    public static void setUsername(String username) {
        System.setProperty(PROPERTY_USERNAME, username);
    }

    public static String getVersionPath() {
        return System.getProperty(PROPRETY_VERSION_PATH);
    }

    public static void setVersionPath(String version) {
        System.setProperty(PROPRETY_VERSION_PATH, version);
    }

    public static String getVersion() {
        if (StringUtils.isEmpty(getVersionPath())) {
            return null;
        }
        return getVersionPath().substring(getVersionPath().lastIndexOf("/") + 1);
    }

    public static String getLicense() {
        return System.getProperty(PROPERTY_LICENSE);
    }

    public static void setLicense(String license) {
        System.setProperty(PROPERTY_LICENSE, license);
    }

    public static String getServerUrl() {
        return System.getProperty(PROPERTY_SERVER_URL);
    }

    public static void setServerUrl(String serverUrl) {
        System.setProperty(PROPERTY_SERVER_URL, serverUrl);
    }

    public static String getOutputFile() {
        return System.getProperty(PROPERTY_OUTPUT_FILE);
    }

    public static void setOutputFile(String outputFile) {
        System.setProperty(PROPERTY_OUTPUT_FILE, outputFile);
    }

    public static String getTaskToken() {
        return System.getProperty(PROPERTY_TASK_TOKEN);
    }

    public static void setTaskToken(String taskToken) {
        System.setProperty(PROPERTY_TASK_TOKEN, taskToken);
    }

    public static String getProxyHost() {
        return System.getProperty(PROPERTY_PROXY_HOST);
    }

    public static void setProxyHost(String proxyHost) {
        System.setProperty(PROPERTY_PROXY_HOST, proxyHost);
    }

    public static Integer getProxyPort() {
        return Integer.getInteger(PROPERTY_PROXY_PORT, 80);
    }

    public static void setProxyPort(String proxyPort) {
        System.setProperty(PROPERTY_PROXY_PORT, proxyPort);
    }

    public static String getTaskParams() {
        return System.getProperty(PROPERTY_TASK_PARAMS);
    }

    public static void setTaskParams(String taskParams) {
        System.setProperty(PROPERTY_TASK_PARAMS, taskParams);
    }

}
