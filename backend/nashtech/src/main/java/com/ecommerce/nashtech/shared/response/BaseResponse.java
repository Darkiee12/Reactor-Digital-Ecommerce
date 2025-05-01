package com.ecommerce.nashtech.shared.response;

import com.ecommerce.nashtech.shared.json.JSON;

public interface BaseResponse {
    default public String toJSON() {
        return JSON.stringify(this).unwrapOr("{}");
    }

}
