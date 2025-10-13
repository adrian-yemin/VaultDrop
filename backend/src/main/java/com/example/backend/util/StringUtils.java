package com.example.backend.util;

import java.util.UUID;

public class StringUtils {
    public static String createShareLinkUrl(UUID token){
        return "https://localhost:8080/api/download/" + token.toString();
    }
}
