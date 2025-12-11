package co.edu.escuelaing.handler.model;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Builder
public class AppNormalizedClaims {
    private final String userId;
    private final String identityId;
    private final String orgId;
    private final List<String> corporateRoles;
    private final List<String> appRoles;


}
