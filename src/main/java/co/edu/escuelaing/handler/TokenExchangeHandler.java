package co.edu.escuelaing.handler;

import co.edu.escuelaing.handler.generator.TokenGenerator;
import co.edu.escuelaing.handler.model.AppNormalizedClaims;
import co.edu.escuelaing.handler.model.ExchangeRequest;
import co.edu.escuelaing.handler.model.ExchangeResponse;
import co.edu.escuelaing.handler.normalizer.ClaimNormalizer;
import co.edu.escuelaing.handler.normalizer.RoleMappingService;
import co.edu.escuelaing.handler.validator.TokenValidator;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


public class TokenExchangeHandler implements RequestHandler<ExchangeRequest, ExchangeResponse> {

    private final TokenValidator validator;
    private final ClaimNormalizer normalizer;
    private final TokenGenerator tokenGenerator;

    public TokenExchangeHandler() {

        // 1. Validator (usa JWKS local)
        this.validator = new TokenValidator();

        // 2. DynamoDB client
        DynamoDbClient dynamoClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // 3. RoleMappingService
        RoleMappingService mappingService = new RoleMappingService(dynamoClient);

        // 4. ClaimNormalizer + inyección del RoleMappingService
        this.normalizer = new ClaimNormalizer(mappingService);

        // 5. Token generator (usará KMS)
        this.tokenGenerator = new TokenGenerator(/*PemUtils.loadPrivateKeyFromPem("PRIVATE_KEY_B64")*/);
    }

    @Override
    public ExchangeResponse handleRequest(ExchangeRequest request, Context context) {

        // 1. Validar token
        var claims = validator.validate(request.getJwt());

        // 2. Normalizar claims
        AppNormalizedClaims normalized = normalizer.normalizeForApp(claims, request.getAppId());

//        // 3. Generar token final
        String finalJwt = tokenGenerator.generateToken(normalized.getUserId(), request.getAppId(), normalized.getAppRoles(), normalized);

        return new ExchangeResponse(finalJwt);
    }

}
