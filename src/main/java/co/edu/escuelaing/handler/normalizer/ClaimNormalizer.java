package co.edu.escuelaing.handler.normalizer;

import co.edu.escuelaing.handler.model.AppNormalizedClaims;
import co.edu.escuelaing.handler.model.NormalizedClaims;
import com.nimbusds.jwt.JWTClaimsSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClaimNormalizer {

    private final RoleMappingService roleMappingService;

    public ClaimNormalizer(RoleMappingService roleMappingService) {
        this.roleMappingService = roleMappingService;
    }

    /**
     * Normaliza claims corporativos y aplica traducciones para la app destino.
     */
    public AppNormalizedClaims normalizeForApp(JWTClaimsSet claims, String appId) {

        // 1. Normalización base
        NormalizedClaims base = normalizeCorporateClaims(claims);

        // 2. Traducción de roles por app (desde DynamoDB)
        List<String> translatedRoles = roleMappingService.translateRoles(base.getRoles(), appId);

        return new AppNormalizedClaims(
                base.getUserId(),
                base.getIdentityId(),
                base.getOrgId(),
                base.getRoles(),          // roles corporativos crudos
                translatedRoles           // roles traducidos para app
        );
    }

    /**
     * Convierte los claims del IdP en un modelo interno estándar.
     */
    public NormalizedClaims normalizeCorporateClaims(JWTClaimsSet claims) {

        Map<String, Object> map = claims.getClaims();

        // 1. userId (sub)
        String userId = claims.getSubject();
        if (userId == null) {
            userId = (String) map.getOrDefault("uid", null);
        }

        // 2. identityId (oid)
        String identityId = (String) map.getOrDefault("oid", userId);

        // 3. orgId (si no tiene issuer, se usa un placeholder)
        String orgId = claims.getIssuer();
        if (orgId == null || orgId.isEmpty()) {
            orgId = "unknown-issuer";
        }

        // 4. roles en formato uniformado de lista
        List<String> roles = extractRoles(map);

        // 5. Guardamos rawClaims por si otra parte del sistema los necesita
        return new NormalizedClaims(
                userId,
                identityId,
                orgId,
                roles,
                map
        );
    }

    /**
     * Extrae roles sin importar cómo vengan (string, lista, array…).
     * Compatible con Azure AD, Auth0, Keycloak y sistemas propios.
     */
    private List<String> extractRoles(Map<String, Object> claims) {

        Object rolesRaw = claims.get("roles");

        if (rolesRaw == null) {
            rolesRaw = claims.get("groups");
        }
        if (rolesRaw == null) {
            rolesRaw = claims.get("permissions");
        }

        // Caso 1: roles como string → ej. "Finance.Admin"
        if (rolesRaw instanceof String s) {
            return List.of(s);
        }

        // Caso 2: roles como lista → ej. ["admin","editor"]
        if (rolesRaw instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object o : list) {
                result.add(o.toString());
            }
            return result;
        }

        // Caso 3: nada encontrado
        return Collections.emptyList();
    }

}
