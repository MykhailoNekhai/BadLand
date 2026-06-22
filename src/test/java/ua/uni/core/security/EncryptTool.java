package ua.uni.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Dev-only tool to re-encrypt secrets when credentials change.
 * Run: mvn test-compile exec:java -Dexec.mainClass=ua.uni.core.security.EncryptTool
 * Paste output into RuntimeSecrets.java.
 * NOT included in the production JAR (test scope only).
 */
public class EncryptTool {

    // Must match exactly what is in RuntimeSecrets.java
    private static final byte[] KEY = {
        (byte)0xC3,(byte)0x7A,(byte)0x11,(byte)0xE5,(byte)0x82,(byte)0xF0,(byte)0x4D,(byte)0x9B,
        (byte)0x6E,(byte)0x28,(byte)0xB7,(byte)0x5C,(byte)0xA4,(byte)0x1F,(byte)0x93,(byte)0x3D,
        (byte)0xD0,(byte)0x76,(byte)0x2E,(byte)0x4C,(byte)0x88,(byte)0xB5,(byte)0x1A,(byte)0xF7
    };

    public static void main(String[] args) throws Exception {
        String[] secrets = {
            "badland-dev-key",
            "badland-dedicated-server-secret",
            "AIzaSyAonJMC727BWN11x7QEEjgHKXR6Ux9eSbw",
            "online.kiamran.uk"
        };
        String[] fields = {
            "ENC_NAKAMA_SERVER_KEY",
            "ENC_NAKAMA_TOKEN_SECRET",
            "ENC_FIREBASE_API_KEY",
            "ENC_NAKAMA_HOST"
        };

        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(KEY);
        System.out.println("// Paste into RuntimeSecrets.java\n");

        for (int i = 0; i < secrets.length; i++) {
            byte[] enc = encrypt(secrets[i], keyBytes);
            System.out.print("private static final byte[] " + fields[i] + " = {");
            for (int j = 0; j < enc.length; j++) {
                System.out.print(String.format("(byte)0x%02X", enc[j] & 0xFF));
                if (j < enc.length - 1) System.out.print(",");
            }
            System.out.println("};\n");
            if (!decrypt(enc, keyBytes).equals(secrets[i]))
                throw new RuntimeException("Verify FAILED for " + fields[i]);
        }
        System.out.println("// All verified OK");
    }

    public static byte[] encrypt(String plaintext, byte[] keyBytes) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(128, iv));
        byte[] ct = cipher.doFinal(plaintext.getBytes("UTF-8"));
        byte[] result = new byte[12 + ct.length];
        System.arraycopy(iv, 0, result, 0, 12);
        System.arraycopy(ct, 0, result, 12, ct.length);
        return result;
    }

    public static String decrypt(byte[] data, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"),
            new GCMParameterSpec(128, Arrays.copyOf(data, 12)));
        return new String(cipher.doFinal(Arrays.copyOfRange(data, 12, data.length)), "UTF-8");
    }
}
