package com.acs_tr069.test_tr069.Services;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.acs_tr069.test_tr069.CWMPResponses.GetSoapFromString;
import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.Entity.ClientList;
import com.acs_tr069.test_tr069.Entity.CpeResponseLog;
import com.acs_tr069.test_tr069.Entity.Device;
import com.acs_tr069.test_tr069.Entity.Devices;
import com.acs_tr069.test_tr069.Entity.GroupCommand;
import com.acs_tr069.test_tr069.Entity.GroupSsid;
import com.acs_tr069.test_tr069.Entity.HttpRequestLog;
import com.acs_tr069.test_tr069.Entity.TaskHandler;
import com.acs_tr069.test_tr069.Entity.WebcliResponseLog;
import com.acs_tr069.test_tr069.Repo.ClientListRepository;
import com.acs_tr069.test_tr069.Repo.CpeResponseLogRepository;
import com.acs_tr069.test_tr069.Repo.DeviceFrontendRepository;
import com.acs_tr069.test_tr069.Repo.DevicesRepository;
import com.acs_tr069.test_tr069.Repo.GroupCommandRepository;
import com.acs_tr069.test_tr069.Repo.HttpLogReqRepository;
import com.acs_tr069.test_tr069.Repo.SsidRepository;
import com.acs_tr069.test_tr069.Repo.TaskHandlerRepository;
import com.acs_tr069.test_tr069.Repo.WebcliResponseLogRepository;
import com.acs_tr069.test_tr069.UDP.udp_sender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

@Service
public class HelperService {
    
    @Autowired
    private HttpLogReqRepository httplogreqRepo; 
    @Autowired
    private TaskHandlerRepository taskhandlerRepo; 
    @Autowired
    private DevicesRepository devicesRepo; 
    @Autowired
    private WebcliResponseLogRepository webCliRepo;
    @Autowired
    private CpeResponseLogRepository cpe_response_repo; 
    @Autowired
    private SsidRepository ssidRepo;
    @Autowired
    private DeviceFrontendRepository device_front;
    @Autowired
    private ClientListRepository client_listRepo; 
    @Autowired
    private GroupCommandRepository GroupCommandRepo;

    private tr069Response tr069response;
    private GetSoapFromString getSoap;
    private StringBuilder faults = new StringBuilder();

    // @RequestMapping(value="/TestSendConnectionRequest/{SN}")
    public void SendUDPRequest(@PathVariable String SN) throws IOException { 
        new Thread(() -> {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }

            Instant instant = Instant.now();
            long timeStampSeconds = instant.toEpochMilli();

            Devices current_device = devicesRepo.getBySerialNum(SN);
            String udp_url = current_device.getUdpConReqUrl();
            String[] device_udp_url = udp_url.split(":");
            String host = device_udp_url[0];
            Integer portnum = Integer.parseInt(device_udp_url[1]);
            StringBuilder sb = new StringBuilder();

            sb.append("GET http://" + udp_url + "?ts=" + timeStampSeconds + "&id=" + timeStampSeconds
                    + "&un=&cn=XTG&sig=DEFAULTSIGDEFAULTSIGDEFAULTSIGDEFAULTSIG HTTP/1.1\r\n");
            sb.append("Accept:*/*\r\n");
            sb.append("Accept-Language:zh-cn\r\n");
            sb.append("host:localhost\r\n");
            sb.append("Content-Length:0\r\n");

            String msg = sb.toString();
            for (int i = 0; i < 2; i++) {
                udp_sender udpclient = null;
                try {
                    udpclient = new udp_sender();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                try {
                    udpclient.sendConnectionRequest(host, portnum, msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                udpclient.close();
            }
        }).start();
    }

    public void SaveTask(String SN, String Method, String Parameters, String Optional) {
        TaskHandler newTasK = new TaskHandler();
        newTasK.set_SN(SN);
        newTasK.set_method(Method);
        newTasK.set_parameters(Parameters);
        newTasK.set_optional(Optional);
        taskhandlerRepo.save(newTasK);
        try {
            Devices current_device = devicesRepo.getBySerialNum(SN);
            if (current_device.getCwmpCycleEnd()) {
                SendUDPRequest(SN);
            }
        } catch (IOException e) {
            faults.append("SerialNumber Not Found");
            e.printStackTrace();
        }
    }

    public void ApplyOldCommand(String serial_num, String device_group) {
        List<GroupCommand> CommandsInGroup = GroupCommandRepo.findByParent(device_group);
        Devices currentDevice = devicesRepo.getBySerialNum(serial_num);
        String deviceModel = currentDevice.getModel();

        for (int i = 0; i < CommandsInGroup.size(); i++) {
            GroupCommand current_command = CommandsInGroup.get(i);
            String[] command_in_line = current_command.getCommand().split("\n", -1);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (int j = 0; j < command_in_line.length; j++) {
                sb.append(",Command:" + command_in_line[j]);
            }

            sb.append(",}");
            if (current_command.getModel().contains("ALL")) {
                SaveTask(serial_num, "Command", sb.toString(), "config");
            } else {
                if (current_command.getModel().contains(deviceModel)) {
                    SaveTask(serial_num, "Command", sb.toString(), "config");
                }
            }
        }
    }
    
    public String GetNumberOfParameters(String serial_num, String Method) {
        CpeResponseLog cpe_response = null;
        try {
            cpe_response = cpe_response_repo.getBySerialNumEquals(serial_num);
        } catch (Exception e) {
            cpe_response = null;
        }
        //System.out.println(cpe_response);
        if(cpe_response == null){
            return "None";
        }else{
            if (cpe_response.getMethod().contains(Method)) {
                SOAPBody soapBody = null;
                try {
                    soapBody = getSoap.StringToSAOP(cpe_response.getPayload()).getSOAPBody();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
                Integer numOfParam = soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
                if (numOfParam == 0) {
                    return "zero";
                } else {
                    StringBuilder result = new StringBuilder();
                    for (int j = 0; j < numOfParam; j++) {
                        result.append(soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().item(j).getChildNodes().item(0).getTextContent() + ",");
                    }
                    cpe_response_repo.delete(cpe_response);
                    return result.toString();
                }
            }
        }
        return "None";
    }

    public void DeleteMultipleObjects(String serial_num, String[] ObjName, Integer NumOfObj) {
        for (int i = 0; i < NumOfObj; i++) {
            if (ObjName[0].matches(".*\\d+.*")) {
                SaveTask(serial_num, "DeleteObject", ObjName[i], "None");
            }
        }
    }

    public void AddNewSSID(String SSIDSettings, String SerialNum, String ObjectName) { 
        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID." + ObjectName + ".X_WWW-RUIJIE-COM-CN_ExistStatus", "None");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.SSID.[" + ObjectName + "].", "AddSSID");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.AccessPoint.[" + ObjectName + "].", "None");
        SaveTask(SerialNum, "SetParameterValues", SSIDSettings, "None");
        SaveTask(SerialNum, "Save", "None", "None");
    }

    public void AddNewAuth(String SSIDSettings, String SerialNum, String ObjectName) { 
        String[] ProcessedString = SSIDSettings.split(",", -1);
        StringBuilder authSetting = new StringBuilder();

        authSetting.append("'{,Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_ModeEnabled:" + ProcessedString[1] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalIP:" + ProcessedString[2] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalUrl:http1//" + ProcessedString[3] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayIP:1.2.3.4,");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_RedirectMode:" + ProcessedString[4] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayID:" + ProcessedString[5] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_OffDetectEnable:" + ProcessedString[6] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_AuthenticationGlobal.X_WWW-RUIJIE-COM-CN_StaPerceptionEnable:" + ProcessedString[7] + ",}");

        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID." + ObjectName + ".X_WWW-RUIJIE-COM-CN_ExistStatus", "None");
        SaveTask(SerialNum, "Command", "'{,Command:dot11 wlan " + ObjectName + ",Command:no band-select enable,}'", "AddAuth");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication.[" + ObjectName + "].", "None");
        SaveTask(SerialNum, "SetParameterValues", authSetting.toString(), "None");
        SaveTask(SerialNum, "Save", "None", "None");
    }

    public void AddOldSSID(String serial_num, String deviceGroup) { 
        List<GroupSsid> ssids = ssidRepo.findByGroup(deviceGroup);
        Integer num_ssid = ssids.size();
        for (int i = 0; i < num_ssid; i++) {
            GroupSsid currentSsid = ssids.get(i);
            Integer wlan_id = currentSsid.getWlanId();
            StringBuilder SSIDSettings = new StringBuilder();
            String encryptionMode = null;
            String encrypModetoConvert = currentSsid.getEncryptionMode();

            if (encrypModetoConvert.contains("Open")) {
                encryptionMode = "None";
            }
            if (encrypModetoConvert.contains("WPA-PSK")) {
                encryptionMode = "WPA-Personal";
            }
            if (encrypModetoConvert.contains("WPA2-PSK")) {
                encryptionMode = "WPA2-Personal";
            }
            SSIDSettings.append("{,Device.WiFi.SSID." + wlan_id + ".SSID:" + currentSsid.getSsid());
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".LowerLayers:1&2");
            if (currentSsid.getForwardMode().contains("Nat")) {
                SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:true");
            } else {
                SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:false");
            }
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_FowardType:" + currentSsid.getForwardMode());

            if (currentSsid.getForwardMode().contains("Bridge")) {
                SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_VLANID:" + currentSsid.getVlanId());
            }
            SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.ModeEnabled:" + encryptionMode);
            if (encryptionMode.contains("None") == false) {
                SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.KeyPassphrase:" + currentSsid.getPassphrase() + ",}");
            } else {
                SSIDSettings.append(",}");
            }

            AddNewSSID(SSIDSettings.toString(), serial_num, wlan_id.toString());
            if (currentSsid.isAuth()) {
                StringBuilder AuthSettings = new StringBuilder();
                AuthSettings.append("{,WiFiDog");
                AuthSettings.append("," + currentSsid.getPortalIp());
                AuthSettings.append("," + currentSsid.getPortalUrl());
                AuthSettings.append(",js");
                AuthSettings.append("," + currentSsid.getGatewayId());
                AuthSettings.append(",true");
                AuthSettings.append("," + currentSsid.isSeamless() + ",}");

                AddNewAuth(AuthSettings.toString(), serial_num, wlan_id.toString());
            }
        }
    }

    public String GetDeviceSerialNum(HttpServletRequest request) { 
        String DeviceSN = null;
        try {
            String currentCookie = request.getHeader("Cookie").split(";")[0];
            if (httplogreqRepo.findByCookie(currentCookie).isEmpty() == false) {
                DeviceSN = httplogreqRepo.getByCookie(currentCookie).getSerialNum();
            }
            return DeviceSN;
        } catch (Exception e) {
            return "None";
        }
    }

    public void SaveSNandCookie(String SN, String Cookie) { 
        if (httplogreqRepo.findBySerialNumEquals(SN).isEmpty()) {
            HttpRequestLog newHttpLog = new HttpRequestLog();
            newHttpLog.setSerialNum(SN);
            newHttpLog.setCookie("session=" + Cookie);
            httplogreqRepo.save(newHttpLog);
        } else {
            HttpRequestLog newHttpLog = httplogreqRepo.getBySerialNumEquals(SN);
            newHttpLog.setSerialNum(SN);
            newHttpLog.setCookie("session=" + Cookie);
            httplogreqRepo.save(newHttpLog);
        }
    }

    public void AddWebCLiTask(String Modes, String SerialNum, String ObjectName) throws JSONException {
        if (webCliRepo.findBySerialNumEquals(SerialNum).isEmpty()) { 
            String Head = "web_cli \"exec\" \"0\" \"0\" \"0\" \"\" \"\" ";
            SaveTask(SerialNum, "WebCli", "{,\"Command\":" + Head + '"' + ObjectName + '"' + ",}", "shell");
        } else {
            String[] modez = Modes.split(",", -1);
            StringBuilder Head = new StringBuilder();
            Head.append("web_cli ");
            // Head.append('"'+modez[0].replaceAll("[^a-zA-Z0-9]", "")+'"'+" ");
            Head.append('"' + modez[1] + '"' + " ");
            Head.append('"' + modez[2] + '"' + " ");
            Head.append('"' + modez[3] + '"' + " ");
            Head.append("\"" + modez[4] + "\" ");
            Head.append("\"" + modez[5] + "\" ");
            Head.append("\"" + modez[6] + "\" ");

            SaveTask(SerialNum, "WebCli", "{,\"Command\":" + Head.toString() + '"' + ObjectName + '"' + ",}", "shell");
        }
        System.out.println("Commited CLI Request: " + new Timestamp(System.currentTimeMillis()));
    }
     
    public void SaveClients(String serial_num, String content) throws JSONException { // sa ruijie specific nga CPE / Routers
        System.out.println(content);
        JSONObject data = new JSONObject(content);
        new Thread(() -> {
            List<ClientList> clients = client_listRepo.findBySerialNumEquals(serial_num);
            for (ClientList client_list : clients) {
                client_listRepo.delete(client_list);
            }
            String string_data = null;
            try {
                string_data = data.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] data_array = string_data.split("\r\n", -1);
            for (int i = 3; i < data_array.length; i++) {
                String[] datas = data_array[i].split("\\s+", -1);
                ClientList client = new ClientList();
                if (datas[0].contains("1")) {
                    client.setBand("2.4G");
                } else {
                    client.setBand("5G");
                }
                client.setMacc(datas[2]);
                client.setRssi(datas[7]);
                DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                client.setUp(dt_date.format(now));
                StringBuilder ssid = new StringBuilder();
                if (datas.length > 16) {
                    for (int y = 16; y < datas.length; y++) {
                        if (y == datas.length) {
                            ssid.append(datas[y]);
                        } else {
                            ssid.append(datas[y] + " ");
                        }
                    }
                } else {
                    ssid.append(datas[16]);
                }
                client.setTraffic(datas[5]);
                client.setSsid(ssid.toString());
                client_listRepo.save(client);
            }
        }).start();
    }

    public void update_client(String serial_num, String content) throws JSONException { 
        JSONObject data = new JSONObject(content);
        String data_content = data.getString("content");
        new Thread(() -> {
            String[] lines = data_content.split("\r\n", -1);

            List<ClientList> clients = client_listRepo.findBySerialNumEquals(serial_num);
            for (ClientList client_list : clients) {
                String[] mac = client_list.getMacc().split(":", -1);
                int i = 0;
                for (String string_line : lines) {
                    String mac_address = mac[0] + mac[1] + "." + mac[2] + mac[3] + "." + mac[4] + mac[5];
                    if (string_line.contains(mac_address)) {
                        String[] ip = lines[i + 1].split("\\s+", -1);
                        client_list.setIp(ip[1]);
                        client_listRepo.save(client_list);
                        break;
                    }
                    i++;
                }
            }
        }).start();
    }

    public void LogRequest(String Method, String Payload, String serial_num) { 
        new Thread(() -> {
            CpeResponseLog newCPE_log = new CpeResponseLog();
            newCPE_log.setMethod(Method);
            newCPE_log.setPayload(Payload);
            newCPE_log.setSerialNum(serial_num);

            cpe_response_repo.save(newCPE_log);
        }, "logging " + serial_num).start();
    }

    public void UpdateDeviceStatus(String SerialNum, String Status) { 
        Device devicestat = device_front.getBySerialNum(SerialNum);
        if (devicestat.getStatus().contains("syncing") == false) {
            devicestat.setStatus(Status);
        }
        device_front.save(devicestat);
    }

    public String Tr069ResponseHandler(String Method, String Parameters, String Option) { 
        if (Method.contains("AddObject")) {
            String body = tr069response.AddObject(Parameters);
            return body;
        }
        if (Method.contains("GetParameterValues")) {
            String body = tr069response.GetParameterValues(Parameters);
            return body;
        }
        if (Method.contains("GetParameterNames")) {
            String body = tr069response.GetParameterNames(Parameters);
            return body;
        }
        if (Method.contains("SetParameterValues")) {
            String body = tr069response.SetParameterValues(Parameters);
            return body;
        }
        if (Method.contains("Command")) {
            String body = tr069response.Command(Parameters, "config");
            return body;
        }
        if (Method.contains("WebCli")) {
            String body = tr069response.Command(Parameters, Option);
            return body;
        }
        if (Method.contains("GetRPCMethods")) {
            String body = tr069response.GetRPCMethods();
            return body;
        }
        if (Method.contains("Reboot")) {
            String body = tr069response.Reboot();
            return body;
        }
        if (Method.contains("DeleteObject")) {
            String body = tr069response.DeleteObject(Parameters);
            return body;
        }
        if (Method.contains("Save")) {
            String body = tr069response.SaveConfig();
            return body;
        }
        if (Method.contains("FactoryReset")) {
            String body = tr069response.FactoryReset();
            return body;
        }
        return "Wrong RPC_Method";
    }

    public String GetCLIOutput(String SerialNum, String ObjectName) { 
        String Outputbody = null;
        String CommandUsed = null;
        List<WebcliResponseLog> cliOutput = webCliRepo.findBySerialNumEquals(SerialNum);
        if (cliOutput != null) {
            Integer NumOutput = cliOutput.size();
            for (int i = 0; i < NumOutput; i++) {
                WebcliResponseLog currentCheck = cliOutput.get(i);
                CommandUsed = new String(currentCheck.getCommandUsed(), Charsets.UTF_8);
                if (CommandUsed.contains("\"" + ObjectName + "\"")) {
                    Outputbody = new String(currentCheck.getCommandOutput(), Charsets.UTF_8);
                    webCliRepo.delete(webCliRepo.getByID(currentCheck.getId()));
                    return Outputbody;
                }
            }
        }
        return Outputbody;
    }
    
    public void SaveWebCLIOutput(String WebCLIOutput, String CommandUsed, String SN) { 
        new Thread(() -> {
            byte[] webcli_byte = WebCLIOutput.getBytes(Charsets.UTF_8);
            byte[] command_byte = CommandUsed.getBytes(Charsets.UTF_8);

            System.out.println("Saving CLI Response: " + new Timestamp(System.currentTimeMillis()));
            WebcliResponseLog webCLIlog = new WebcliResponseLog();
            webCLIlog.setCommandOutput(webcli_byte);
            webCLIlog.setDeviceSn(SN);
            webCLIlog.setCommandUsed(command_byte);
            webCLIlog.setTimeSaved(new Timestamp(System.currentTimeMillis()));
            webCliRepo.save(webCLIlog);
            System.out.println("Saved CLI Response: " + new Timestamp(System.currentTimeMillis()));
        }).start();
    }

    public void requestAssociatedClientOfSerialNumber(String SerialNumber) {
        System.out.println("Call to AutoProv Server for SN Association");
        String apiUrl = "http://10.160.0.61:7549/getClientBySerialNumber/" + SerialNumber;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            String ipAssigned = jsonNode.get("ip_assigned").asText(); // Assuming "ip_assigned" is a String field, if not, you might need to adjust accordingly

            System.out.println("IP Assigned: " + ipAssigned);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
    }

    public void AddNewRLSSID(String SerialNum) {
        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues", "{,InternetewayDevice.LANDevice.1.WLANConfiguration.1.SSID: ACSTest,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:50rangePips,}", "None");
    }

    public void ConfigureSSID(String SerialNum, String NewSSID) {
        NewSSID = NewSSID.replace(" ", "_");
        String Password = "" + NewSSID + "1234";

        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID: " + NewSSID + "-2.4G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:" + Password + ",}", "None");
        SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.SSID: " + NewSSID + "-5G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.PreSharedKey.1.PreSharedKey:" + Password + ",}", "None");
    }

    public void ConfigureDefaultSSID(String SerialNum) {
        String ssid = "SSID";
        String password = "12345678";

        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID: " + ssid + "-2.4G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:" + password + ",}", "None");
        SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.SSID: " + ssid + "-5G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.5.PreSharedKey:" + password + ",}", "None");
    }

    public void toggleService(String serialNum) {
        System.out.println("ACS: Pushed task to revert WAN1 to TR069");
        SaveTask(serialNum, "SetParameterValues", "{,InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.X_CMCC_ServiceList:TR069,}", "None");
    }

    public void AddRoutedWANConfiguration(String SerialNum, String VlanId, String ExternalIPAdd, String DefaultGateway, String SubnetMask, String DNSServers) {
        if (ExternalIPAdd == null || ExternalIPAdd == " ")
            ExternalIPAdd = "100.10.0.20";
        if (DefaultGateway == null || DefaultGateway == " ")
            DefaultGateway = "192.168.0.1";
        if (SubnetMask == null || SubnetMask == " ")
            SubnetMask = "255.255.255.0";
        if (DNSServers == null || DNSServers == " ")
            DNSServers = "8.8.8.8";

        System.out.println(ExternalIPAdd + DefaultGateway + SubnetMask + DNSServers);

        SaveTask(SerialNum, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection", "None");
        String parent_object_WAN_config = "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2";
        StringBuilder sb_config = new StringBuilder();

        sb_config.append("{,");
        sb_config.append(parent_object_WAN_config + ".Name:INTERNET,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_ServiceList:INTERNET,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_IPMode:1,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_VLANMode:2,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_VLANIDMark:" + VlanId + ",");
        sb_config.append(parent_object_WAN_config + ".AddressingType:Static,");
        sb_config.append(parent_object_WAN_config + ".ExternalIPAddress:" + ExternalIPAdd + ",");
        sb_config.append(parent_object_WAN_config + ".DefaultGateway:" + DefaultGateway + ",");
        sb_config.append(parent_object_WAN_config + ".SubnetMask:" + SubnetMask + ",");
        sb_config.append(parent_object_WAN_config + ".DNSServers:" + DNSServers + ",");
        sb_config.append(parent_object_WAN_config + ".NATEnabled:1,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_LanInterface-DHCPEnable:1,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_LanInterface:InternetGatewayDevice.LANDevice.1.WLANConfiguration.2&InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1,");
        sb_config.append(parent_object_WAN_config + ".Enable:1,");

        for (int i = 1; i <= 8; i++) {
            sb_config.append("InternetGatewayDevice.LANDevice.1.WLANConfiguration." + i + ".Enable:1,");
        }

        sb_config.append("}");
        SaveTask(SerialNum, "SetParameterValues", sb_config.toString(), "None");
    }

    public String ToggleWAN(String SerialNum, String Instance, String Toggle) {
        if (Instance.equals("1") || Instance.equals("0"))
            return "Not Allowed";
        try {
            SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection." + Instance + ".Enable:" + Toggle + ",}", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Error on Configuration. Fault: Serial Number Not Found";
        }
        return "WAN Instance Toggled";
    }

    public void AddBridgedWANConfiguration(String SerialNum, String VlanId) { 
        SaveTask(SerialNum, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection", "None");
        String parent_object_WAN_config = "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2";
        StringBuilder sb_config = new StringBuilder();

        sb_config.append("{,");
        sb_config.append(parent_object_WAN_config + ".Name:INTERNET,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_ServiceList:INTERNET,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_IPMode:1,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_VLANMode:2,");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_VLANIDMark:" + VlanId + ",");
        sb_config.append(parent_object_WAN_config + ".X_CMCC_LanInterface-DHCPEnable:0,");
        sb_config.append(parent_object_WAN_config + ".ConnectionType:IP_Bridged,");
        sb_config.append(parent_object_WAN_config + ".Enable:1,");

        for (int i = 1; i <= 8; i++) {
            sb_config.append("InternetGatewayDevice.LANDevice.1.WLANConfiguration." + i + ".Enable:0,");
        }

        sb_config.append("}");
        SaveTask(SerialNum, "SetParameterValues", sb_config.toString(), "None");
    }

    public String DeleteWANInstance(String SerialNum, String Instance) {
        if (Instance.equals("1") || Instance.equals("0"))
            return "Not Allowed";
        else {
            SaveTask(SerialNum, "DeleteObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection." + Instance, "None");
            return "WAN Instance Deleted";
        }
    }

    public void SetPPPUserCredentials(String SerialNum, String Username, String Password) {
        // TODO: Implement PPP username and password
        // Parameters
    }

    public void SetONUUserAdminCredentials(String SerialNum, String Username, String Password) {
        if (Username == null || Username == " ")
            Username = "useradmin";
        if (Password == null || Password == " ")
            Password = "RL87654321";

        SaveTask(SerialNum, "SetParameterValues", "{,InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetUserName:" + Username + ",InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetPassword:" + Password + ",}", "None");
    }

    public void UnrougeDevice(String SerialNum) {
        Device device = device_front.getBySerialNum(SerialNum);
        device.setParent("Residential");
        device_front.save(device);
    }
    
    public void setDeviceInformInterval(String SerialNum, Integer seconds) {
        if (seconds == null)
            seconds = 14400;
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.ManagementServer.PeriodicInformInterval:" + seconds.toString() + ",}",
                "None");
        System.out.println("Inform interval changed to " + seconds + " for " + SerialNum);
    }
}
