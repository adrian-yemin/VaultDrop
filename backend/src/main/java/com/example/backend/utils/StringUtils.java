package com.example.backend.utils;

import java.util.UUID;

public class StringUtils {
    public static String createShareLinkUrl(UUID token){
        return "http://localhost:8080/api/share/" + token.toString();
    }
}
