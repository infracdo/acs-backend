package com.acs_tr069.test_tr069.ZabbixApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;

public class ZabbixApiRPCCalls {
    public static String Authentication(URL zabbix_url) throws IOException, JSONException {
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"user.login\",\"params\": {\"user\": \"nemrod@apolloglobal.net\",\"password\": \"StudyInScarlet123\"},\"id\": 1,\"auth\": null}";
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        //System.out.println("ZabbixTest: " + contentbuffer.toString());
        JSONObject object = new JSONObject(contentbuffer.toString());
        //System.out.println("Auth: "+ object.get("result").toString());
        con.disconnect();
        return object.get("result").toString();
    }

    public static String CreateZabbixHost(URL zabbix_url, String Hostname,String ip,String groupId, String auth) throws IOException, JSONException {
        StringBuilder message = new StringBuilder(); 
        message.append("{");
        message.append("\"jsonrpc\": \"2.0\",");
        message.append("\"method\": \"host.create\",");
        message.append("\"params\": {");
        message.append("\"host\": \""+Hostname+"\",");
        message.append("\"interfaces\": [");
        message.append("{");
        message.append("\"type\": 1,");
        message.append("\"main\": 1,");
        message.append("\"useip\": 1,");
        message.append("\"ip\": \""+ip+"\",");
        message.append("\"dns\": \"\",");
        message.append("\"port\": \"10050\"");
        message.append("}");
        message.append("],");
        message.append("\"groups\": [");
        message.append("{");
        message.append("\"groupid\": \""+groupId+"\"");
        message.append("}");
        message.append("]");
        message.append("},");
        message.append("\"auth\": \""+auth+"\",");
        message.append("\"id\": 1");
        message.append("}");

        //System.out.println(message.toString());

        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message.toString());
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        return contentbuffer.toString();
        ////System.out.println(contentbuffer.toString());
    }
    public static String CreateItem(String hostid, String name, String key, String auth, URL zabbix_url) throws IOException, JSONException {
        //String message = "{\"jsonrpc\": \"2.0\",\"method\": \"item.create\",\"params\": {\"name\": \""+name+"\",\"key_\": \""+key+"\",\"hostid\": \""+hostid+"\",\"type\": 9,\"value_type\": 5,\"delay\": \"30s\"},\"auth\": \""+auth+"\",\"id\": 1}";
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"item.create\",\"params\": {\"name\": \""+name+"\",\"key_\": \""+key+"\",\"hostid\": \""+hostid+"\",\"type\": 2,\"value_type\": 4,\"interfaceid\": \"0\",\"delay\": \"30s\"},\"auth\": \""+auth+"\",\"id\": 1}";
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        //System.out.println(contentbuffer.toString());
        return contentbuffer.toString();
    }

    public static String GetSpecificHost(String host_name,String auth, URL zabbix_url) throws IOException, JSONException {
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"host.get\",\"params\": {\"filter\":{\"host\":\""+host_name+"\"}},\"id\": 2,\"auth\": \""+auth+"\"}";
        
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        JSONObject object = new JSONObject(contentbuffer.toString());
        JSONArray json_array = new JSONArray(object.get("result").toString());
        //System.out.println("SpecificHost: " + json_array.getJSONObject(0).get("hostid").toString());
        try {
            return json_array.getJSONObject(0).get("hostid").toString();
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }
    }

    public static String GetSpecificHostGroup(String host_name,String auth, URL zabbix_url) throws IOException, JSONException {
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"hostgroup.get\",\"params\": {\"output\": \"extend\",\"filter\":{\"name\":\""+host_name+"\"}},\"id\": 1,\"auth\": \""+auth+"\"}";
        
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        JSONObject object = new JSONObject(contentbuffer.toString());
        JSONArray json_array = new JSONArray(object.get("result").toString());
        //System.out.println("SpecificHost: " + json_array.getJSONObject(0).get("hostid").toString());
        return json_array.getJSONObject(0).get("groupid").toString();
    }
    
    public static JSONArray GetZabbixHosts(String host_group_id,String auth, URL zabbix_url) throws IOException, JSONException {
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"host.get\",\"params\": {\"output\": [\"host\"],\"groupids\": [\""+host_group_id+"\"]},\"id\": 2,\"auth\": \""+auth+"\"}";
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        ////System.out.println(contentbuffer.toString());
        JSONObject object = new JSONObject(contentbuffer.toString());
        JSONArray json_array = new JSONArray(object.get("result").toString());
        return json_array;
    }
    
    public static JSONArray GetItems(String host_id, String auth, URL zabbix_url) throws IOException, JSONException {
        
        String message = "{\"jsonrpc\": \"2.0\",\"method\": \"item.get\",\"params\": {\"output\": \"extend\",\"hostids\": \""+host_id+"\",\"search\": {\"key_\": \"\"},\"sortfield\": \"name\"},\"auth\": \""+auth+"\",\"id\": 1}";

        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        //System.out.println(contentbuffer.toString());
        
        JSONObject object = new JSONObject(contentbuffer.toString());
        JSONArray json_array = new JSONArray(object.get("result").toString());
        
        if(json_array.length()>0){
            return json_array;
        }else{
            return null;
        }
    }

    public static void UpdateItem(String zabbixhost,String key,String value) throws IOException {
        String host = "202.60.8.245";
        int port = 10051;
        ZabbixSender zabbixSender = new ZabbixSender(host, port);
        
        DataObject dataObject = new DataObject();
        dataObject.setHost(zabbixhost);
        dataObject.setKey(key);
        dataObject.setValue(value);
        // TimeUnit is SECONDS.
        dataObject.setClock(System.currentTimeMillis()/1000);
        SenderResult result = zabbixSender.send(dataObject);
    
        //System.out.println("result:" + result);
        if (result.success()) {
            System.out.println("send success.");
        } else {
            System.err.println("sned fail!");
        }
    }

    public static String Testing(String message,String auth, URL zabbix_url) throws IOException, JSONException {
        //String message = "{\"jsonrpc\": \"2.0\",\"method\": \"host.get\",\"params\": {\"output\": [\"host\"],\"groupids\": [\""+host_group_id+"\"]},\"id\": 2,\"auth\": \""+auth+"\"}";
        HttpURLConnection con = (HttpURLConnection) zabbix_url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json-rpc");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        
        out.writeBytes(message);
        out.flush();
        out.close();

        int status = con.getResponseCode();
        StringBuffer contentbuffer = new StringBuffer();
        String inputLine;

        inputLine = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while((inputLine = in.readLine()) != null) {
            contentbuffer.append(inputLine);
        }
        con.disconnect();
        //System.out.println(contentbuffer.toString());
        return contentbuffer.toString();
    }

}
