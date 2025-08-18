/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iznajmljivanjeapp.bezbednost;

import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import framework.simplelogger.LogLevel;
import framework.simplelogger.SimpleLogger;

/**
 *
 * @author Djurkovic
 */
public class PasswordHasher {

    public static String hash(String s, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(s.toCharArray(), salt, 65536, 256); // 65536 iteracija, 256bitni kljuc
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //siguran algoritam za hesiranje
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri hesiranju sifre, odgovarajuci algoritam nije podrzan od java verzije koja se izvrsava");
            return null;
        } catch (InvalidKeySpecException ex) {
            SimpleLogger.log(LogLevel.LOG_ERROR, "Greska pri hesiranju sifre, prosledjena specifikacija algoritma nije validna.");
            return null;
        }
    }

    public static String hash(String s) {
        byte[] salt = generateSalt();
        return hash(s, salt);
    }
    
    public static boolean hashEquals(String hashed,String salt,String s) {
        String sh = hash(s,Base64.getDecoder().decode(salt));
        if(sh == null) return false;
        return sh.equals(hashed);
    }

    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

}
