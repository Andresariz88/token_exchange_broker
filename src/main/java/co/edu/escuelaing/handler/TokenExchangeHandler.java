package co.edu.escuelaing.handler;

import co.edu.escuelaing.handler.generator.TokenGenerator;
import co.edu.escuelaing.handler.model.ExchangeRequest;
import co.edu.escuelaing.handler.model.ExchangeResponse;
import co.edu.escuelaing.handler.model.NormalizedClaims;
import co.edu.escuelaing.handler.normalizer.ClaimNormalizer;
import co.edu.escuelaing.handler.validator.TokenValidator;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


//import com.example.teb.normalizer.ClaimNormalizer;
//import com.example.teb.generator.TokenGenerator;
//import com.example.teb.validator.TokenValidator;
//import com.example.teb.model.NormalizedClaims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenExchangeHandler implements RequestHandler<ExchangeRequest, ExchangeResponse> {

    private static final Logger logger = LogManager.getLogger(TokenExchangeHandler.class);

    private final TokenValidator validator = new TokenValidator();
    private final ClaimNormalizer normalizer = new ClaimNormalizer();
    private final TokenGenerator generator = new TokenGenerator();

    @Override
    public ExchangeResponse handleRequest(ExchangeRequest request, Context context) {

        logger.info("Token Exchange Broker - Request received for appId {}", request.getAppId());

        // 1. Validar token
        var claims = validator.validate(request.getJwt());

        // 2. Normalizar claims
        NormalizedClaims normalized = normalizer.normalize(claims);
//
//        // 3. Generar token final
//        String finalJwt = generator.generate(normalized, request.getAppId());

        System.out.println("HELLO LAMBDA");

        return null;

//        return new ExchangeResponse(finalJwt);
    }
}
