package com.acs_tr069.test_tr069.CWMPResponses;

public class Tr069Response {

    public static String AddObject(String ObjectName){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
        
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:AddObject>");
        sb.append("<ObjectName>");
        sb.append(ObjectName);
        sb.append("</ObjectName>");
        sb.append("<ParameterKey/>");
        sb.append("</cwmp:AddObject>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String GetParameterValues(String ParameterName){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
       
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");
       
        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:GetParameterValues>");
        sb.append("<ParameterNames");
        sb.append(" SOAP-ENC:arrayType=\"xsd:string[1]\">");
        sb.append("<string>");
        sb.append(ParameterName);
        sb.append("</string>");
        sb.append("</ParameterNames>");
        sb.append("</cwmp:GetParameterValues>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        
        return body;
    }

    public static String GetParameterNames(String ParameterPath){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
      
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");
      
        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:GetParameterNames>");

        sb.append("<ParameterPath>");
        sb.append(ParameterPath);
        sb.append("</ParameterPath>");

        sb.append("<NextLevel>");
        sb.append(1);            
        sb.append("</NextLevel>");
        
        sb.append("</cwmp:GetParameterNames>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        
        return body;
    }

    public static String SetParameterValues(String ParameterList){
        //Process String
        String[] ProcessedString = ParameterList.split(",",-1);
        
        StringBuilder newParamList = new StringBuilder();
        for(int i = 1; i < (ProcessedString.length-1) ; i++){
           
            String PstringIndex = ProcessedString[i]; 
            String[] valueList = PstringIndex.split(":",-1);

            valueList[1] = valueList[1].replace('&', ',');
            valueList[1] = valueList[1].replace("http1", "http:");

            newParamList.append("<ParameterValueStruct>");
            newParamList.append("<Name>");
            newParamList.append(valueList[0]);
            newParamList.append("</Name>");
            newParamList.append("<Value ");
            newParamList.append("xsi:type=\"xsd:"+Converter.DatatypeConverter(valueList[1])+"\">");
            newParamList.append(valueList[1]);
            newParamList.append("</Value>");
            newParamList.append("</ParameterValueStruct>");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
       
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:SetParameterValues>");
        sb.append("<ParameterList ");
        sb.append("SOAP-ENC:arrayType=\"cwmp:ParameterValueStruct["+(ProcessedString.length-2)+"]\">");
        sb.append(newParamList.toString());
        sb.append("</ParameterList>");
        sb.append("<ParameterKey>");
        sb.append("unsetCommandKey");
        sb.append("</ParameterKey>");
        sb.append("</cwmp:SetParameterValues>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;        
    }

    public static String SaveConfig(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
  
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:SetParameterValues>");
        sb.append("<ParameterList ");
        sb.append("SOAP-ENC:arrayType=\"cwmp:ParameterValueStruct[1]\">");
        
        sb.append("<ParameterValueStruct>");
        sb.append("<Name>");
        sb.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_SaveConfig");
        sb.append("</Name>");
        sb.append("<Value ");
        sb.append("xsi:type=\"xsd:int\">");
        sb.append("0");
        sb.append("</Value>");
        sb.append("</ParameterValueStruct>");

        sb.append("</ParameterList>");
        sb.append("<ParameterKey>");
        sb.append("unsetCommandKey");
        sb.append("</ParameterKey>");
        sb.append("</cwmp:SetParameterValues>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String InformResponse(){

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>\n");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");
   
        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");
        
        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:InformResponse>");
        sb.append("<MaxEnvelopes>");
        sb.append("1");
        sb.append("</MaxEnvelopes>");
        sb.append("</cwmp:InformResponse>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String GetRPCMethods(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");

        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:GetRPCMethods>");
        sb.append("<ParameterKey/>");
        sb.append("</cwmp:AddObject>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String Reboot(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");

        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:Reboot>");
        sb.append("<CommandKey/>");
        sb.append("</cwmp:Reboot>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String FactoryReset(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");

        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:FactoryReset>");
        sb.append("</cwmp:FactoryReset>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String Command(String ParameterList,String Mode){
        
        String[] ProcessedString = ParameterList.split(",",-1);
        
        StringBuilder newParamList = new StringBuilder();
        for(int i = 1; i < (ProcessedString.length-1) ; i++){
            String PstringIndex = ProcessedString[i]; 
            String[] valueList = PstringIndex.split(":",-1);
            newParamList.append("<Command>");
            if(valueList.length>2){
                for(int j=1;j<valueList.length;j++){
                    if(j<(valueList.length-1)){
                        newParamList.append(valueList[j]);
                        newParamList.append(":");
                    }else{
                        newParamList.append(valueList[j]);
                    }
                }
            }else{
                newParamList.append(valueList[1]);
            }
            newParamList.append("</Command>");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");

        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");

        sb.append("<cwmp:X_RUIJIE_COM_CN_ExecuteCliCommand>");
        sb.append("<Mode>");
        sb.append(Mode);
        sb.append("</Mode>");
        sb.append("<CommandList ");
        sb.append("SOAP-ENC:arrayType=\"xsd:string["+(ProcessedString.length-2)+"]\">");
        
        sb.append(newParamList.toString());

        sb.append("</CommandList>");
        sb.append("</cwmp:X_RUIJIE_COM_CN_ExecuteCliCommand>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;
    }

    public static String DeleteObject(String ObjectName){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope ");
        sb.append("xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
        sb.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        sb.append("xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">");
        sb.append("<SOAP-ENV:Header>");
        sb.append("<cwmp:ID SOAP-ENV:mustUnderstand=\"1\">");
        sb.append(""+null);
        sb.append("</cwmp:ID>");

        sb.append("<cwmp:NoMoreRequests>");
        sb.append("1");
        sb.append("</cwmp:NoMoreRequests>");

        sb.append("</SOAP-ENV:Header>");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<cwmp:DeleteObject>");
        sb.append("<ObjectName>");
        sb.append(ObjectName);
        sb.append("</ObjectName>");
        sb.append("<ParameterKey/>");
        sb.append("</cwmp:DeleteObject>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");
        String body = sb.toString();
        return body;

    }

}
