package com.acs_tr069.test_tr069.CWMPResponses;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.springframework.ws.soap.SoapMessage;

public class GetSoapFromString {

    public static SOAPMessage StringToSAOP(String xmlPayload) {
        InputStream is = new ByteArrayInputStream(xmlPayload.getBytes());
        SOAPMessage converted = null;
        try {
			converted = MessageFactory.newInstance().createMessage(null, is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return converted;
    }
    
}
