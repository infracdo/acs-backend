package com.acs_tr069.test_tr069.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RlDeviceService {

    @Autowired
    private Tr069TaskHandlerService task_handler;

    public void addWANConnectionDevice(String SN, String Params){
        try {
            JSONObject params = new JSONObject(Params);
            Integer wlan_id = params.getInt("wlan_id");
            Integer wlan_vlan_id = 2000;
            try {
                wlan_vlan_id = params.getInt("wlan_vlan");
            } catch (Exception e) {
                wlan_vlan_id = 2000;
            }
            String wlan_mode = "2";
            try {
                wlan_mode = params.get("wlan_mode").toString();
            } catch (Exception e) {
                wlan_mode = "2";
            }
            // task_handler.SaveTask(SN, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice", "None");
            
            String parent_object = "InternetGatewayDevice.WANDevice.1.WANConnectionDevice."+wlan_id;

            StringBuilder sb = new StringBuilder();
            sb.append("{,");
            
            sb.append(parent_object+".X_CT-COM_WANEponLinkConfig.Enable:1,");
            
            sb.append(parent_object+".X_CT-COM_WANEponLinkConfig.Mode:"+wlan_mode+",");
            
            sb.append(parent_object+".X_CT-COM_WANEponLinkConfig.VLANIDMark:"+wlan_vlan_id+",");          
            sb.append("}");
            
            task_handler.SaveTask(SN, "SetParameterValues",sb.toString(),"None");
            System.out.println("################ Add Wan Connection Device Pushed. Please check ONU. ################");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void AddWANIPConnection(String SN, String Params){
        try {
            JSONObject params = new JSONObject(Params);
            Integer parent_wlan_id = params.getInt("wlan_id");
            Integer wlan_connection_id = params.getInt("wlan_connection_id");
            
            //task_handler.SaveTask(SN, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice."+parent_wlan_id+".WANIPConnection", "None");

            String parent_object = "InternetGatewayDevice.WANDevice.1.WANConnectionDevice."+parent_wlan_id+".WANIPConnection."+wlan_connection_id;
            StringBuilder sb = new StringBuilder();
            
            sb.append("{,");
            sb.append(parent_object+".ConnectionType:IP_Routed,");
            sb.append(parent_object+".X_CMCC_ServiceList:INTERNET,");
            sb.append(parent_object+".X_CMCC_IPMode:1,");
            sb.append(parent_object+".AddressingType:DHCP,");
            sb.append(parent_object+".ExternalIPAddress:10.30.0.131,");
            sb.append(parent_object+".DefaultGateway:10.30.0.1,");
            sb.append(parent_object+".SubnetMask:255.255.255.0,");
            sb.append(parent_object+".DNSServers:8.8.8.8,");
            sb.append(parent_object+".NATEnabled:1,");
            sb.append(parent_object+".X_CMCC_LanInterface-DHCPEnable:1,");
            sb.append(parent_object+".X_CMCC_LanInterface:InternetGatewayDevice.LANDevice.1.WLANConfiguration.2&InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1,");
            sb.append(parent_object+".Enable:1,");
            sb.append("}");
            
            task_handler.SaveTask(SN, "SetParameterValues",sb.toString(),"None");
            System.out.println("################ Add WAN IP Connection Pushed. Please check ONU. ################");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void AddSSID(String SN, String Params){
        try {
            JSONObject params = new JSONObject(Params);
            Integer ssid_id = params.getInt("ssid_id");
            if(ssid_id<1){
                ssid_id = 1;
            }
            if(ssid_id>8){
                ssid_id = 8;
            }
            String parent_object = "InternetGatewayDevice.LANDevice.1.WLANConfiguration."+ssid_id;
            StringBuilder sb = new StringBuilder();
            
            sb.append("{,");
            sb.append(parent_object+".SSID:DC804GW-MTKSSID,");
            sb.append(parent_object+".PreSharedKey.1.PreSharedKey:123456789,");
            sb.append(parent_object+".Channel:0,");
            sb.append(parent_object+".AutoChannelEnable:1,");
            sb.append("}");
            
            task_handler.SaveTask(SN, "SetParameterValues",sb.toString(),"None");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    
}
