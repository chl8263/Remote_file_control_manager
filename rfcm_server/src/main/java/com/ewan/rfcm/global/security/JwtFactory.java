package com.ewan.rfcm.global.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class JwtFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String signingKey = "jwttest";
    protected static final String ISSUER = "RFCM";
    protected static final String USER_ID = "USER_ID";
    protected static final String USER_ROLE = "USER_ROLE";

    public String generateToken(AccountContext accountContext){
        String token = null;
        try{
            token = JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim(USER_ID, accountContext.getAccount().getUserId())
                    .withClaim(USER_ROLE, accountContext.getAccount().getAccountRole().getRoleName())
                    .sign(generateAlgorithm(signingKey));

        }catch (Exception e){
            logger.error("[JwtFactory] Failed to generate JWT token", e);
        }
        return token;
    }

    private Algorithm generateAlgorithm(String signingKey) {
        return Algorithm.HMAC256(signingKey);
    }
}
