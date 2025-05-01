package com.ecommerce.nashtech.shared.util;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Router {
    String base;
    public Router(String base){
        this.base = base;
    }

    public String getURI(Object... pathArgs){
        StringBuilder path = new StringBuilder();
        path.append(base);
        for (Object arg : pathArgs) {
            path.append("/").append(arg.toString());
        }
        return path.toString();
    }


}
