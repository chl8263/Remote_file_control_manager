package com.ewan.rfcm.global.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ewan.rfcm.global.security.error.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtDecoder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AccountContext decodeJwt(String token){
        DecodedJWT decodedJWT = isValidToken(token).orElseThrow(() -> new InvalidJwtException("Invalid token."));
        String userId = decodedJWT.getClaim(JwtFactory.USER_ID).asString();
        String role = decodedJWT.getClaim(JwtFactory.USER_ROLE).asString();

        return new AccountContext(userId, "***", role);
    }

    private Optional<DecodedJWT> isValidToken(String token){
        DecodedJWT jwt = null;
        try{
            Algorithm algorithm = Algorithm.HMAC256(JwtFactory.signingKey);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();

            jwt = jwtVerifier.verify(token);
        }catch (Exception e){
            logger.error("[JwtDecoder]", e);
        }
        return Optional.ofNullable(jwt);
    }
}
