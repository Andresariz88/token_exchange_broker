package co.edu.escuelaing.handler.model;


import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalizedClaims {

    private String userId;
    private String identityId;
    private String orgId;
    private List<String> roles;
    private Map<String, Object> rawClaims;

}