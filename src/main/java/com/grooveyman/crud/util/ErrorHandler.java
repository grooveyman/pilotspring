package com.grooveyman.crud.util;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {

        public static Map<String, String> errorResponse(String message){
            Map<String, String> error = new HashMap<>();
            error.put("error", message);
        return error;
    }
}
