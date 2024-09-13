package com.acs_tr069.test_tr069.StoreRequestResult;

import javax.xml.soap.SOAPBody;

public class GetResponseResult {
    public static String getResult(SOAPBody convertedBody, String getResponsetype){
        if(getResponsetype.contains("GetParameterValues")){
            System.out.println(convertedBody.getElementsByTagName("Value").item(0).getTextContent());
            return convertedBody.getElementsByTagName("Value").item(0).getTextContent();
        }
        if(getResponsetype.contains("SetParameterValuesResponse")){
            System.out.println(convertedBody.getChildNodes().item(0).getTextContent());
            return convertedBody.getChildNodes().item(0).getTextContent();
        }
        if(getResponsetype.matches("X_RUIJIE_COM_CN_ExecuteCliCommandResponse")){
            if(convertedBody.getElementsByTagName("Command").item(0).getTextContent().contains("web_cli")){
                return convertedBody.getElementsByTagName("Response").item(0).getTextContent();
            }
            else{
                return "none";
            }
        }
        return "";
    }
}
