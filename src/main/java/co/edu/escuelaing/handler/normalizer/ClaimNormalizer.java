package co.edu.escuelaing.handler.normalizer;

import co.edu.escuelaing.handler.model.NormalizedClaims;
import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Map;

public class ClaimNormalizer {

    public NormalizedClaims normalize(JWTClaimsSet claimsSet) {

        String userId = (String) originalClaims.get("oid");
        String role = (String) originalClaims.get("roles"); // simplificado

        return new NormalizedClaims(userId, role, "Finance");
    }
}
