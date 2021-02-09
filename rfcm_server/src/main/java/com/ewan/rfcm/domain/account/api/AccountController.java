package com.ewan.rfcm.domain.account.api;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity a (@PathVariable String accountName){


        String a = "a";

        return ResponseEntity.ok(a);

    }
}
