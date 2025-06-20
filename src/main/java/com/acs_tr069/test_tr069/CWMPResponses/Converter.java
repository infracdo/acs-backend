package com.acs_tr069.test_tr069.CWMPResponses;

public class Converter {
    public static String DatatypeConverter(String StringToConvert){
        try {
            Integer newinteger = Integer.parseInt(StringToConvert);
            return "int";
        } catch (NumberFormatException e) {
            if(StringToConvert.contains("true")||StringToConvert.contains("false")){
                return "boolean";
            }else{
                return "string";
            }
        }
    }
}
