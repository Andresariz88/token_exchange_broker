package co.edu.escuelaing.handler;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

public class JwtGenerator {

    public static void main(String[] args) throws Exception {

        byte[] keyBytes = Files.readAllBytes(Paths.get("private.pem"));
        String privateKeyPem = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = java.util.Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(spec);

        JWSSigner signer = new RSASSASigner(privateKey);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("andres.ariza-p")
                .claim("roles", "Finance.Admin")
                .claim("oid", "00-ab-12345")
                .expirationTime(new Date(new Date().getTime() + 5 * 60 * 1000))
                .issuer("ariza-idp")
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID("ariza-key-1")
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(signer);

        System.out.println(jwt.serialize());
    }
}
