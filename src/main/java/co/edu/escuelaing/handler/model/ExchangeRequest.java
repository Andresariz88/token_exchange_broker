package co.edu.escuelaing.handler.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ExchangeRequest {
    private String jwt;
    private String appId;

}
