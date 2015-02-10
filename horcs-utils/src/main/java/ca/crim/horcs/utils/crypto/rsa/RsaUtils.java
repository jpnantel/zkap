package ca.crim.horcs.utils.crypto.rsa;

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
     * M�thode permettant le chiffrement par bloc avec RSA pour des donn�es sans
     * limite de taille. Les donn�es sont d�coup�es en bloc d'une taille
     * sufisamment petite pour que le data + le padding soit plus petit ou �gal
     * au modulo de la cl�. En pratique, les blocs peuvent avoir une taille
     * d'environ 116 ou 117 bytes pour une cl� de 1024 bits.
     * 
     * La taille maximale de donn�es encrypt�es avec RSA est limit�e par le
     * modulo n de la cl� (des donn�es plus grande provoqueront des collisions
     * �tant donn�e que ces donn�es seront dans la m�me classe d'�quivalence
     * modulo n qu'une autre donn�e plus petite que n). Donc en th�orie, on peut
     * chiffrer taille du modulo en bit / 8 -> nombre de bytes. On doit par
     * contre laisser de l'espace pour le padding. Par exemple, le padding PKCS1
     * n�cessite 11 byte. On a pas besoin de se pr�occuper de la taille du
     * padding lorsque l'on d�crypte.
     * 
     * 
     * @param dataInput
     *            InputStream des donn�es � chiffrer.
     * @param output
     *            OutputStream des donn�es chiffr�es.
     * @param cipher
     *            Algorithme de chiffrement � utiliser. Doit �tre compl�tement
     *            initialis� sinon une exception sera lanc�e.
     * @param keyModulusSize
     *            Taille du modulo de la cl� utilis�e pour chiffrer / d�chiffrer
     *            (la cl� publique et priv�e correspondante ont le m�me modulo).
     * @param paddingSchemeSize
     *            Taille en byte n�cessaire pour conserver l'information. Par
     *            exemple, PKCS1 utilise 11 bytes.
     * @param samplingModulus
     *            Fr�quence d'�chantillonage pour l'encryption des donn�es. Par
     *            exemple, si on donne la valeur 10 � ce param�tre, un paquet
     *            sur 10 sera crypt�. L'exception � cette r�gle est que le
     *            premier et le dernier paquet seront toujours crypt�s.
     * @throws IOException
     *             S'il est impossible de lire du flux d'entr�e ou d'�crire dans
     *             le flux de sortie.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur d'encryption due � une taille de
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
            // Si on tombe sur un bloc � chiffrer
            if (++nbBlocks % samplingModulus == 0) {
                int nbLeft = dataInput.read(cryptBuffer, 0, blockSize);
                if (nbLeft < 0) {
                    return;
                }
                // Si la taille du bloc � chiffrer est un bloc partiel (fin du
                // fichier), alors construire un bloc complet plus petit avec
                // ces donn�es, pour ne pas avoir de padding lors du
                // d�chiffrement.
                if (nbLeft < blockSize) {
                    byte[] tempSpecialBuffer = new byte[nbLeft];
                    System.arraycopy(cryptBuffer, 0, tempSpecialBuffer, 0, nbLeft);
                    output.write(cipher.doFinal(tempSpecialBuffer));
                } else {
                    output.write(cipher.doFinal(cryptBuffer));
                }

            } else { // �crire les donn�es telles quelles.
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
     * Encryption par bloc avec RSA pour un �chantillonage des donn�es.
     * 
     * @param data
     *            Donn�es � chiffrer.
     * @param cipher
     *            Chiffrement utilis�.
     * @param keyModulusSize
     *            Taille de la cl� RSA.
     * @param paddingSchemeSize
     *            Taille du padding utilis� dans le chiffrement (PKCS1 => 11
     *            byte)
     * @param samplingModulus
     *            Fr�quence d'�chantillonage. Un bloc par �chantillon de la
     *            taille sp�cifi� sera chiffr�.
     * @return Le message chiffr�.
     * @throws IOException
     *             S'il y a une erreur lors de l'�criture du r�sultat. sortie.
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

        // La taille maximale de donn�es encrypt�es avec RSA est limit�e par le
        // modulo n de la cl� (des donn�es plus grande provoqueront des
        // collisions �tant donn�e que ces donn�es seront dans la m�me classe
        // d'�quivalence modulo n qu'une autre donn�e plus petite que n). Donc
        // en th�orie, on peut chiffrer taille du modulo en bit / 8 -> nombre de
        // bytes. On doit par contre laisser de l'espace pour le padding. Par
        // exemple, le padding PKCS1 n�cessite 11 byte.
        // On a pas besoin de se pr�occuper de la taille du padding lorsque l'on
        // d�crypte.
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
     * D�chiffrement par bloc avec RSA � partir de flux de donn�es.
     * 
     * @param dataInput
     *            flux de donn�es entrant encrypt�.
     * @param output
     *            flux de donn�es sortant d�crypt�.
     * @param cipher
     *            Algorithme de chiffrement.
     * @param keyModulusSize
     *            Taille de la cl�.
     * @param samplingModulus
     *            Fr�quence d'�chantillonage. Un bloc par �chantillon de la
     *            taille sp�cifi� sera chiffr�.
     * @throws IOException
     *             S'il y a une erreur lors de l'�criture du r�sultat. sortie.
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

        // Unit� de traitement courante.
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
            // D�chiffement si le bloc est le premier de l'�chantillon
            if (++nbBlocks % samplingModulus == 0) {
                output.write(cipher.doFinal(localBuffer));
            } else {
                output.write(localBuffer);
            }
        }
    }

    /**
     * D�chiffrement par bloc avec RSA � partir de tableaux de donn�es.
     * 
     * @param data
     *            Donn�es � d�chiffrer.
     * @param cipher
     *            Algorithme de chiffrement.
     * @param keyModulusSize
     *            Taille de la cl�.
     * @param samplingModulus
     *            Fr�quence d'�chantillonage. Un bloc par �chantillon de la
     *            taille sp�cifi� sera chiffr�.
     * @return Les donn�es d�chiffr�es.
     * @throws IOException
     *             S'il y a une erreur lors de l'�criture du r�sultat. sortie.
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
