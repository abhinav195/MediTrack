package com.airtribe.meditrack.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateMRN() {
        return "MRN"+UUID.randomUUID().toString().substring(0, 3).toUpperCase();
    }


}
