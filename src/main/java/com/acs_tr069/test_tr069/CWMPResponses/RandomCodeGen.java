package com.acs_tr069.test_tr069.CWMPResponses;

import java.util.Random;

public class RandomCodeGen {

    public static String CodeGenerator(Integer nuberofChar){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = nuberofChar;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

        System.out.println(generatedString);
        return generatedString;

    }
    
}
