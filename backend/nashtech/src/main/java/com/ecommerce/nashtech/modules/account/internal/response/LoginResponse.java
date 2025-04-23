package com.ecommerce.nashtech.modules.account.internal.response;

import com.ecommerce.nashtech.shared.json.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class LoginResponse {
    String accessToken;
    String refreshToken;

    public static final String Example = """
            {
                "accessToken": "string",
                "refreshToken": "string"
            }
        """;
    
    public String toJSON(){
        return JSON.stringify(this).unwrapOr("{}");
    }

    public static String build(String accessToken, String refreshToken){
        return new LoginResponse(accessToken, refreshToken).toJSON();
    }

    
}
