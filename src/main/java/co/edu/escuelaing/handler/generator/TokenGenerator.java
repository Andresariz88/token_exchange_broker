package co.edu.escuelaing.handler.generator;

import co.edu.escuelaing.handler.model.NormalizedClaims;
import software.amazon.awssdk.services.kms.KmsClient;

import java.util.HashMap;
import java.util.Map;

public class TokenGenerator {

    private final KmsClient kmsClient = KmsClient.create();

    public String generate(NormalizedClaims claims, String appId) {

        // 1. Obtener permisos de DynamoDB (pendiente)
        // 2. Construir claims finales
        Map<String, Object> finalClaims = new HashMap<>();
        finalClaims.put("sub", claims.userId());
        finalClaims.put("app", appId);
        finalClaims.put("permissions", new String[]{"read", "write"});

        // 3. Firmar con KMS (pendiente)
        return "<signed-jwt>";
    }
}
