package com.ewan.rfcm.global.security;

import com.ewan.rfcm.global.security.error.InvalidJwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HeaderTokenExtractor {
    public static final String HEADER_PREFIX = "Bearer ";

    public String extract(String header) {
        if(!StringUtils.hasText(header) || header.length() < HEADER_PREFIX.length()){
            throw new InvalidJwtException("Not valid token information");
        }

        return header.substring(HEADER_PREFIX.length(), header.length());
    }
}