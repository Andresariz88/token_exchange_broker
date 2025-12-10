package co.edu.escuelaing.handler.model;


import java.util.List;
import java.util.Map;

public class NormalizedClaims {

    private String userId;
    private String identityId;
    private String orgId;
    private List<String> roles;
    private Map<String, Object> rawClaims;

    public NormalizedClaims(String userId,
                            String identityId,
                            String orgId,
                            List<String> roles,
                            Map<String, Object> rawClaims) {

        this.userId = userId;
        this.identityId = identityId;
        this.orgId = orgId;
        this.roles = roles;
        this.rawClaims = rawClaims;
    }

    /** Getters **/
    public String getUserId() { return userId; }
    public String getIdentityId() { return identityId; }
    public String getOrgId() { return orgId; }
    public List<String> getRoles() { return roles; }
    public Map<String, Object> getRawClaims() { return rawClaims; }
}