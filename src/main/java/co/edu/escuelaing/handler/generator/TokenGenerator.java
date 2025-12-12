package co.edu.escuelaing.handler.generator;

import co.edu.escuelaing.handler.model.AppNormalizedClaims;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.SigningAlgorithmSpec;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class TokenGenerator {

    private final KmsClient kmsClient = KmsClient.create();
    private final String kmsKeyId = "arn:aws:kms:us-east-1:787156774370:key/e3e77703-fa95-4dd9-a55b-4f2a1c258372";


    public TokenGenerator() {
    }

    public String generateToken(
            String userId,
            String appId,
            List<String> appRoles,
            AppNormalizedClaims appNormalizedClaims
    ) {
        try {
            // ---------- HEADER ----------
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID("ariza-key-1")
                    .type(JOSEObjectType.JWT)
                    .build();

            // ---------- CLAIMS ----------
            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(3600); // 1 hora

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer("token-exchange-broker")
                    .subject(userId)
                    .audience(appId)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))

                    // claims normalizados
                    .claim("oid", appNormalizedClaims.getOrgId())
                    .claim("tenantId", "ariza-tenant")

                    // roles traducidos
                    .claim("appRoles", appRoles)

                    // metadatos opcionales Zero Trust
                    .claim("originApp", "portal-gateway")
                    .build();

            // ---------- FIRMA ----------
            SignedJWT signedJWT = new SignedJWT(header, claims);

            // Construir el signing input
            byte[] signingInput = signedJWT.getSigningInput();  // header.payload en Base64URL

            // Firmar con AWS KMS
            var signResponse = kmsClient.sign(SignRequest.builder()
                    .keyId(kmsKeyId)
                    .message(SdkBytes.fromByteArray(signingInput))
                    .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PKCS1_V1_5_SHA_256)
                    .build());

            byte[] signatureBytes = signResponse.signature().asByteArray();

            // Construir la representación final del JWT
            String headerAndPayload = new String(signingInput, StandardCharsets.US_ASCII);
            String signatureB64Url = Base64URL.encode(signatureBytes).toString();

            return headerAndPayload + "." + signatureB64Url;

        } catch (Exception ex) {
            throw new RuntimeException("Error generating JWT", ex);
        }
    }
}
