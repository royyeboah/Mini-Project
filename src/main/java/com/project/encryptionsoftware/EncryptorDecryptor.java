package com.project.encryptionsoftware;

import javafx.scene.control.Alert;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class EncryptorDecryptor {
    public File destinationFile;
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);

    // Compares the password hash at the beginning of the file with the password hash from the password
    boolean arePasswordHashesEqual(File file, String passwordHash) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        StringBuffer keyHashFromFile = new StringBuffer(128);
        for (int i = 0; i < 128; i++) {
            keyHashFromFile.append((char) bis.read());

        }
        bis.close();

        return keyHashFromFile.toString().equals(passwordHash);

    }

    // Returns the password hash in a string
    private String getHashInString(String password) throws NoSuchAlgorithmException {

        byte[] passwordHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        passwordHash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte hash : passwordHash) {
            sb.append(Integer.toString((hash & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    // Returns the password hash in a byte array
    private byte[] getHashBytes(String password) throws NoSuchAlgorithmException {

        byte[] passwordHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        passwordHash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passwordHash.length; i++) {
            sb.append(Integer.toString((passwordHash[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString().getBytes();
    }

    // Encrypts the file
    public void encryptFile(File file, String password) {

        byte[] passwordHash;

        // Checks if the file is not a directory and not null
        if (!(file == null) && !file.isDirectory()) {
            try {

                // Gets the password hash
                passwordHash = getHashBytes(password);
                destinationFile = new File(file.getAbsolutePath().concat(".enc"));

                if (destinationFile.exists()) {
                    destinationFile.delete();
                    destinationFile = new File(file.getAbsolutePath().concat(".enc"));
                }

                // Creates a new file with the password hash and then writes the content of the encryption to it
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                FileOutputStream fos = new FileOutputStream(destinationFile);

                // Writes the password hash to the beginning of the encrypted file
                fos.write(passwordHash, 0, 128);

                byte[] buffer = new byte[262144];
                int bufferSize = buffer.length;
                int passwordSize = password.length();

                // Writes the new encrypted content to the new encrypted file
                while (bis.available() > 0) {
                    int bytesCopied = bis.read(buffer);
                    for (int i = 0, keyCounter = 0; i < bufferSize; i++, keyCounter %= passwordSize) {

                        buffer[i] += password.toCharArray()[keyCounter];
                    }
                    fos.write(buffer, 0, bytesCopied);
                }

                // Closes the streams and deletes the old file
                bis.close();
                fos.close();
                file.delete();

                successAlert.setContentText("File encrypted successfully!");
                successAlert.show();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                errorAlert.setContentText("There was an error during encryption!");
                errorAlert.show();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }

        }

    }

    public void decryptFile(File file, String password) throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        String passwordHash;

        try {

            //
            passwordHash = getHashInString(password);

            if (arePasswordHashesEqual(file, passwordHash)) {

                // Recovers the original file by removing the .enc extension from it
                FileInputStream bis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath().replace(".enc", ""), true);

                byte[] buffer = new byte[262144];
                int bufferSize = buffer.length;
                int passwordSize = password.length();

                // Skips the password hash
                bis.skip(128);


                // Writes the new decrypted file content to the original file
                while (bis.available() > 0) {

                    int bytesCopied = bis.read(buffer);
                    for (int i = 0, keyCounter = 0; i < bufferSize; i++, keyCounter %= passwordSize) {
                        buffer[i] -= password.toCharArray()[keyCounter];
                    }
                    fos.write(buffer, 0, bytesCopied);
                }

                bis.close();
                fos.close();
                file.delete();
                successAlert.setContentText("File decrypted successfully!");
                successAlert.show();

            } else {
                errorAlert.setContentText("Wrong password!");
                errorAlert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorAlert.setContentText("Decryption error!");
            errorAlert.show();
        }

    }
}
