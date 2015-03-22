package ca.jp.secproj.utils.crypto.rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * Classe utilitaire pour le chiffrement en bloc avec RSA.
 * 
 * @author nanteljp
 * 
 */
class RsaUtils {

    /**
     * Méthode permettant le chiffrement par bloc avec RSA pour des données sans
     * limite de taille. Les données sont découpées en bloc d'une taille
     * sufisamment petite pour que le data + le padding soit plus petit ou égal
     * au modulo de la clé. En pratique, les blocs peuvent avoir une taille
     * d'environ 116 ou 117 bytes pour une clé de 1024 bits.
     * 
     * La taille maximale de données encryptées avec RSA est limitée par le
     * modulo n de la clé (des données plus grande provoqueront des collisions
     * étant donnée que ces données seront dans la même classe d'équivalence
     * modulo n qu'une autre donnée plus petite que n). Donc en théorie, on peut
     * chiffrer taille du modulo en bit / 8 -> nombre de bytes. On doit par
     * contre laisser de l'espace pour le padding. Par exemple, le padding PKCS1
     * nécessite 11 byte. On a pas besoin de se préoccuper de la taille du
     * padding lorsque l'on décrypte.
     * 
     * 
     * @param dataInput
     *            InputStream des données à chiffrer.
     * @param output
     *            OutputStream des données chiffrées.
     * @param cipher
     *            Algorithme de chiffrement à utiliser. Doit être complètement
     *            initialisé sinon une exception sera lancée.
     * @param keyModulusSize
     *            Taille du modulo de la clé utilisée pour chiffrer / déchiffrer
     *            (la clé publique et privée correspondante ont le même modulo).
     * @param paddingSchemeSize
     *            Taille en byte nécessaire pour conserver l'information. Par
     *            exemple, PKCS1 utilise 11 bytes.
     * @param samplingModulus
     *            Fréquence d'échantillonage pour l'encryption des données. Par
     *            exemple, si on donne la valeur 10 à ce paramètre, un paquet
     *            sur 10 sera crypté. L'exception à cette règle est que le
     *            premier et le dernier paquet seront toujours cryptés.
     * @throws IOException
     *             S'il est impossible de lire du flux d'entrée ou d'écrire dans
     *             le flux de sortie.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur d'encryption due à une taille de
     *             bloc invalide.
     * @throws BadPaddingException
     */
    public static void blockCipherEncrypt(InputStream dataInput, OutputStream output, Cipher cipher,
            int keyModulusSize, int paddingSchemeSize, int samplingModulus) throws IOException,
            IllegalBlockSizeException, BadPaddingException {

        // taille des blocs
        int regularBlockSize = keyModulusSize / 8;
        int blockSize = regularBlockSize - paddingSchemeSize;

        byte[] cryptBuffer = new byte[blockSize];
        byte[] regBuffer = new byte[regularBlockSize];

        int nbBlocks = -1;
        while (true) {
            // Si on tombe sur un bloc à chiffrer
            if (++nbBlocks % samplingModulus == 0) {
                int nbLeft = dataInput.read(cryptBuffer, 0, blockSize);
                if (nbLeft < 0) {
                    return;
                }
                // Si la taille du bloc à chiffrer est un bloc partiel (fin du
                // fichier), alors construire un bloc complet plus petit avec
                // ces données, pour ne pas avoir de padding lors du
                // déchiffrement.
                if (nbLeft < blockSize) {
                    byte[] tempSpecialBuffer = new byte[nbLeft];
                    System.arraycopy(cryptBuffer, 0, tempSpecialBuffer, 0, nbLeft);
                    output.write(cipher.doFinal(tempSpecialBuffer));
                } else {
                    output.write(cipher.doFinal(cryptBuffer));
                }

            } else { // Écrire les données telles quelles.
                int nbLeft = dataInput.read(regBuffer, 0, regularBlockSize);
                if (nbLeft < 0) {
                    return;
                }
                if (nbLeft < regularBlockSize) {
                    byte[] tempSpecialBuffer = new byte[nbLeft];
                    System.arraycopy(regBuffer, 0, tempSpecialBuffer, 0, nbLeft);
                    output.write(tempSpecialBuffer);
                } else {
                    output.write(regBuffer);
                }
            }
        }
    }

    /**
     * Encryption par bloc avec RSA pour un échantillonage des données.
     * 
     * @param data
     *            Données à chiffrer.
     * @param cipher
     *            Chiffrement utilisé.
     * @param keyModulusSize
     *            Taille de la clé RSA.
     * @param paddingSchemeSize
     *            Taille du padding utilisé dans le chiffrement (PKCS1 => 11
     *            byte)
     * @param samplingModulus
     *            Fréquence d'échantillonage. Un bloc par échantillon de la
     *            taille spécifié sera chiffré.
     * @return Le message chiffré.
     * @throws IOException
     *             S'il y a une erreur lors de l'écriture du résultat. sortie.
     * @throws IllegalBlockSizeException
     *             S'il y a une erreur dans la taille des blocs lors du
     *             chiffrement.
     * @throws BadPaddingException
     *             S'il y a une erreur de padding lors du chiffrement.
     */
    public static byte[] blockCipherEncrypt(byte[] data, Cipher cipher, int keyModulusSize, int paddingSchemeSize,
            int samplingModulus) throws IOException, IllegalBlockSizeException, BadPaddingException {

        // taille des blocs
        int regularBlockSize = keyModulusSize / 8;
        int blockSize = regularBlockSize - paddingSchemeSize;

        // La taille maximale de données encryptées avec RSA est limitée par le
        // modulo n de la clé (des données plus grande provoqueront des
        // collisions étant donnée que ces données seront dans la même classe
        // d'équivalence modulo n qu'une autre donnée plus petite que n). Donc
        // en théorie, on peut chiffrer taille du modulo en bit / 8 -> nombre de
        // bytes. On doit par contre laisser de l'espace pour le padding. Par
        // exemple, le padding PKCS1 nécessite 11 byte.
        // On a pas besoin de se préoccuper de la taille du padding lorsque l'on
        // décrypte.
        byte[] cryptBuffer = new byte[blockSize];
        byte[] regBuffer = new byte[regularBlockSize];

        int nbBlocks = -1;
        int cursor = 0;
        int totalLenght = data.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(totalLenght);
        boolean dataEnd = false;
        while (!dataEnd) {
            if (++nbBlocks % samplingModulus == 0) {
                if (totalLenght - cursor < blockSize) {
                    dataEnd = true;
                    byte[] tempSpecialBuffer = new byte[totalLenght - cursor];
                    System.arraycopy(data, cursor, tempSpecialBuffer, 0, totalLenght - cursor);
                    baos.write(cipher.doFinal(tempSpecialBuffer));
                } else {
                    System.arraycopy(data, cursor, cryptBuffer, 0, blockSize);
                    cursor += blockSize;
                    baos.write(cipher.doFinal(cryptBuffer));
                }
            } else {
                if (totalLenght - cursor < regularBlockSize) {
                    dataEnd = true;
                    byte[] tempSpecialBuffer = new byte[totalLenght - cursor];
                    System.arraycopy(data, cursor, tempSpecialBuffer, 0, totalLenght - cursor);
                    baos.write(tempSpecialBuffer);
                } else {
                    System.arraycopy(data, cursor, regBuffer, 0, regularBlockSize);
                    cursor += regularBlockSize;
                    baos.write(regBuffer);
                }
            }
        }
        return baos.toByteArray();
    }

    /**
     * Déchiffrement par bloc avec RSA à partir de flux de données.
     * 
     * @param dataInput
     *            flux de données entrant encrypté.
     * @param output
     *            flux de données sortant décrypté.
     * @param cipher
     *            Algorithme de chiffrement.
     * @param keyModulusSize
     *            Taille de la clé.
     * @param samplingModulus
     *            Fréquence d'échantillonage. Un bloc par échantillon de la
     *            taille spécifié sera chiffré.
     * @throws IOException
     *             S'il y a une erreur lors de l'écriture du résultat. sortie.
     * @throws IllegalBlockSizeException
     *             S'il y a une erreur dans la taille des blocs lors du
     *             chiffrement.
     * @throws BadPaddingException
     *             S'il y a une erreur de padding lors du chiffrement.
     */
    public static void blockCipherDecrypt(InputStream dataInput, OutputStream output, Cipher cipher,
            int keyModulusSize, int samplingModulus) throws IOException, IllegalBlockSizeException, BadPaddingException {

        // taille des blocs
        int blockSize = keyModulusSize / 8;

        // Unité de traitement courante.
        byte[] buffer = new byte[blockSize];
        int nbBlocks = -1;
        while (true) {
            // Lecture d'un bloc
            int nbLeft = dataInput.read(buffer, 0, blockSize);
            if (nbLeft <= 0) {
                return;
            }
            byte[] localBuffer = null;
            if (nbLeft < blockSize) {
                localBuffer = new byte[nbLeft];
                System.arraycopy(buffer, 0, localBuffer, 0, nbLeft);
            } else {
                localBuffer = buffer;
            }
            // Déchiffement si le bloc est le premier de l'échantillon
            if (++nbBlocks % samplingModulus == 0) {
                output.write(cipher.doFinal(localBuffer));
            } else {
                output.write(localBuffer);
            }
        }
    }

    /**
     * Déchiffrement par bloc avec RSA à partir de tableaux de données.
     * 
     * @param data
     *            Données à déchiffrer.
     * @param cipher
     *            Algorithme de chiffrement.
     * @param keyModulusSize
     *            Taille de la clé.
     * @param samplingModulus
     *            Fréquence d'échantillonage. Un bloc par échantillon de la
     *            taille spécifié sera chiffré.
     * @return Les données déchiffrées.
     * @throws IOException
     *             S'il y a une erreur lors de l'écriture du résultat. sortie.
     * @throws IllegalBlockSizeException
     *             S'il y a une erreur dans la taille des blocs lors du
     *             chiffrement.
     * @throws BadPaddingException
     *             S'il y a une erreur de padding lors du chiffrement.
     */
    public static byte[] blockCipherDecrypt(byte[] data, Cipher cipher, int keyModulusSize, int samplingModulus)
            throws IOException, IllegalBlockSizeException, BadPaddingException {

        // taille des blocs
        int blockSize = keyModulusSize / 8;

        byte[] buffer = new byte[blockSize];

        int nbBlocks = -1;
        int cursor = 0;
        int totalLenght = data.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(totalLenght);
        boolean dataEnd = false;
        while (!dataEnd) {
            byte[] localBuffer = null;
            if (totalLenght - cursor < blockSize) {
                dataEnd = true;
                localBuffer = new byte[totalLenght - cursor];
                System.arraycopy(data, cursor, localBuffer, 0, totalLenght - cursor);
            } else {
                System.arraycopy(data, cursor, buffer, 0, blockSize);
                cursor += blockSize;
                localBuffer = buffer;
            }

            if (++nbBlocks % samplingModulus == 0) {
                baos.write(cipher.doFinal(localBuffer));
            } else {
                baos.write(localBuffer);
            }
        }
        return baos.toByteArray();
    }
}
