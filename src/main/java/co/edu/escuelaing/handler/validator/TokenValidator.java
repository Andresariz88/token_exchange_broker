package co.edu.escuelaing.handler.validator;

import com.nimbusds.jwt.SignedJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;

public class TokenValidator {

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode jwks;

    public TokenValidator() {
        loadJWKS();
    }

    private void loadJWKS() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("jwks.json")) {
            if (is == null) {
                throw new RuntimeException("JWKS file not found in resources");
            }
            this.jwks = mapper.readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("Error loading JWKS file", e);
        }
    }

    public JWTClaimsSet validate(String jwtString) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtString);

            // 1. Obtener el KID
            String kid = signedJWT.getHeader().getKeyID();
            if (kid == null) {
                throw new RuntimeException("JWT header does not contain 'kid'");
            }

            // 2. Buscar clave en JWKS
            RSAPublicKey publicKey = getPublicKeyFromJWKS(kid);

            // 3. Validar firma
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }

            // 4. Validar claims estándar
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            validateStandardClaims(claims);

            return claims;

        } catch (Exception e) {
            throw new RuntimeException("JWT validation failed: " + e.getMessage(), e);
        }
    }

    private RSAPublicKey getPublicKeyFromJWKS(String kid) {
        try {
            for (JsonNode key : jwks.get("keys")) {
                if (key.get("kid").asText().equals(kid)) {

                    String n = key.get("n").asText();
                    String e = key.get("e").asText();

                    byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
                    byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

                    BigInteger modulus = new BigInteger(1, modulusBytes);
                    BigInteger exponent = new BigInteger(1, exponentBytes);

                    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = keyFactory.generatePublic(spec);

                    return (RSAPublicKey) publicKey;
                }
            }
            throw new RuntimeException("No matching key found in JWKS for kid: " + kid);

        } catch (Exception ex) {
            throw new RuntimeException("Error parsing JWKS RSA key", ex);
        }
    }

    private void validateStandardClaims(JWTClaimsSet claims) {

        // Expiration (exp)
        if (claims.getExpirationTime() == null ||
                claims.getExpirationTime().toInstant().isBefore(Instant.now())) {
            throw new RuntimeException("Token is expired");
        }

        // Issuer (iss) – opcional pero recomendado
        if (!"ariza-idp".equals(claims.getIssuer())) {
            throw new RuntimeException("Invalid issuer");
        }

        // Subject (sub)
        if (claims.getSubject() == null) {
            throw new RuntimeException("Missing 'sub' claim");
        }
    }
}
