package co.edu.escuelaing.handler.util;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class PemUtils {

    public static RSAPrivateKey loadPrivateKeyFromPem(String envVar) {
        try {
            String base64 = System.getenv(envVar);
            byte[] keyBytes = Base64.getDecoder().decode(base64);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing private key", e);
        }
    }
}
