package com.ewan.rfcm.domain.account.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @JsonProperty("userId")
    String userId;

    @JsonProperty("password")
    String password;
}
