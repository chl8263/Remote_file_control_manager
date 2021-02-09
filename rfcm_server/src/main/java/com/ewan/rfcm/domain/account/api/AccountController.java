package com.ewan.rfcm.domain.account.api;

import com.ewan.rfcm.global.security.token.JwtPostProcessingToken;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Wongyun Choi
 */
@RestController
@RequestMapping(value = {"/api/accounts"}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {

    @GetMapping("/{accountName}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity a (@PathVariable String accountName, Authentication authentication){

        JwtPostProcessingToken token = (JwtPostProcessingToken) authentication;

        return ResponseEntity.ok(token.getPrincipal());
    }

    @GetMapping("/check")
    public ResponseEntity check (){
        return (ResponseEntity) ResponseEntity.ok();
    }
}
