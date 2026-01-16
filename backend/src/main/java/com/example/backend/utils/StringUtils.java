package com.example.backend.utils;

import java.util.UUID;

public class StringUtils {
    public static String createShareLinkUrl(UUID token){
        return "http://vaultdrop-load-balancer-549238236.us-east-1.elb.amazonaws.com/api/share/" + token.toString();
    }
}
