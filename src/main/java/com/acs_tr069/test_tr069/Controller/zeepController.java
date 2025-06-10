package com.acs_tr069.test_tr069.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.CWMPResponses.GetSoapFromString;
import com.acs_tr069.test_tr069.Entity.httprequestlog;
import com.acs_tr069.test_tr069.Entity.taskhandler;
import com.acs_tr069.test_tr069.Entity.webcli_response_log;
import com.acs_tr069.test_tr069.Entity.zeep_devices;
import com.acs_tr069.test_tr069.Entity.group_command;
import com.acs_tr069.test_tr069.Entity.auto_complete;
import com.acs_tr069.test_tr069.Entity.cpe_response_log;
import com.acs_tr069.test_tr069.Entity.group_ssid;
import com.acs_tr069.test_tr069.Entity.device;

import com.acs_tr069.test_tr069.Repo.httplogreqRepo;
import com.acs_tr069.test_tr069.Repo.taskhandlerRepo;
import com.acs_tr069.test_tr069.Repo.webcli_response_logRepo;
import com.acs_tr069.test_tr069.Repo.auto_completeRepository;
import com.acs_tr069.test_tr069.Repo.cpe_response_logRepository;
import com.acs_tr069.test_tr069.Repo.zeepDevicesRepository;
import com.acs_tr069.test_tr069.Repo.group_commandRepo;
import com.acs_tr069.test_tr069.Repo.groupsRepository;
import com.acs_tr069.test_tr069.Repo.ssidRepository;
import com.acs_tr069.test_tr069.Repo.device_frontendRepository;

import com.acs_tr069.test_tr069.StoreRequestResult.GetResponseResult;
import com.acs_tr069.test_tr069.UDP.udp_sender;
import com.acs_tr069.test_tr069.ZabbixApi.ZabbixApiRPCCalls;
import com.google.common.base.Charsets;
import com.acs_tr069.test_tr069.CWMPResponses.RandomCodeGen;

@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/zeep/")
public class zeepController {

    @Autowired
    private httplogreqRepo httplogreqRepo;
    @Autowired
    private taskhandlerRepo taskhandlerRepo;
    @Autowired
    private zeepDevicesRepository zeepDevicesRepo; // separated devices repo for zeep
    @Autowired
    private webcli_response_logRepo webCliRepo;
    @Autowired
    private cpe_response_logRepository cpe_response_repo;
    @Autowired
    private ssidRepository ssidRepo;
    @Autowired
    private device_frontendRepository device_front;
    @Autowired
    private group_commandRepo GroupCommandRepo;
    @Autowired
    private auto_completeRepository auto_completeRepo;

    String cwmpheader = null;
    String Output = null;
    Integer stage = 0;
    Boolean SSIDAdded = false;

    private tr069Response tr069response;
    private GetSoapFromString getSoap;
    private RandomCodeGen randomGen;
    private ZabbixApiRPCCalls zabbixRPC;

    @PostMapping(value = "/")
    public DeferredResult<ResponseEntity<String>> TestDevice(@RequestBody(required = false) String xmlPayload,
            HttpServletRequest request, HttpServletResponse response) {
        System.out.println(xmlPayload);
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        String DeviceSerialNum = null;
        if (xmlPayload != null) {
            if (xmlPayload.contains("<cwmp:Inform>")) {

                SOAPBody convertB = null;
                try {
                    convertB = getSoap.StringToSAOP(xmlPayload).getSOAPBody();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
                DeviceSerialNum = convertB.getElementsByTagName("SerialNumber").item(0).getTextContent();
            } else {
                DeviceSerialNum = GetDeviceSerialNum(request);
            }
        } else {
            DeviceSerialNum = GetDeviceSerialNum(request);
        }
        //System.out.println("DeviceThatRequest" + DeviceSerialNum);
        new Thread(() -> {
            // //System.out.println("Execute method asynchronously - " +
            // Thread.currentThread().getName());
            String responsebody = null;
            String SN = null;
            String SNCookie = null;
            SOAPBody converteBody = null;
            String getResponsetype = null;

            if (xmlPayload != null) {
                try {
                    converteBody = getSoap.StringToSAOP(xmlPayload).getSOAPBody();
                    getResponsetype = converteBody.getChildNodes().item(0).getLocalName();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
                System.out.println("ResponseType: "+ getResponsetype);
                //getResponsetype = converteBody.getChildNodes().item(0).getLocalName();

                if (xmlPayload.contains("<cwmp:Inform>")) {

                    SNCookie = randomGen.CodeGenerator(18);
                    SN = converteBody.getElementsByTagName("SerialNumber").item(0).getTextContent();

                    SaveSNandCookie(SN, SNCookie);
                    response.addHeader("Set-Cookie", "session=" + SNCookie);
                    responsebody = tr069response.InformResponse();

                    //System.out.println("End: " + LocalTime.now());
                    device check_device = device_front.getBySerialNum(SN);
                    if(check_device == null){
                        try {
                            UpdateDevicesTable(xmlPayload);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML)
                                .body(null));
                    }
                    else {
                        CheckDeviceEventCode(xmlPayload);
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML)
                                .body(responsebody));
                    }
                }
                //System.out.println(GetResponseResult.getResult(converteBody, getResponsetype));

                if (xmlPayload.contains("<cwmp:X_RUIJIE_COM_CN_ExecuteCliCommandResponse>")) {
                    String DeviceSN = GetDeviceSerialNum(request);
                    String CommandUsed = converteBody.getElementsByTagName("Command").item(0).getTextContent();
                    String WebCliContent = GetResponseResult.getResult(converteBody, "X_RUIJIE_COM_CN_ExecuteCliCommandResponse");
                    System.out.println("Recieved CLI Response: "+ new Timestamp(System.currentTimeMillis()));
                    if (WebCliContent.matches("none") == false) {
                        SaveWebCLIOutput(WebCliContent, CommandUsed, DeviceSN);
                    }
                }

                if (xmlPayload.contains("<cwmp:GetParameterNamesResponse>")) {
                    //System.out.println(getResponsetype);
                    String DeviceSN = GetDeviceSerialNum(request);
                    LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
                if (xmlPayload.contains("<cwmp:RebootResponse>")) {
                    String DeviceSN = GetDeviceSerialNum(request);
                    UpdateDeviceStatus(DeviceSN, "offline");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
            }

            String DeviceSN = GetDeviceSerialNum(request);

            if (taskhandlerRepo.findBySerialNumEquals(DeviceSN).isEmpty() == false) {
                List<taskhandler> task = taskhandlerRepo.findBySerialNumEquals(DeviceSN);
                String Method = task.get(0).get_method().toString();
                String Parameters = task.get(0).get_parameters().toString();
                String Optional = task.get(0).get_optional();
                Long id = task.get(0).get_Id();

                if (Optional.contains("AddSSID")) {
                    if (getResponsetype.contains("GetParameterValuesResponse")) {
                        if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Delete")) {
                            responsebody = Tr069ResponseHandler(Method, Parameters, Optional);
                        } else {
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 1));
                            // taskhandlerRepo.delete(taskhandlerRepo.getByID(id+2));
                            // taskhandlerRepo.delete(taskhandlerRepo.getByID(id+3));

                            //System.out.println("End: " + LocalTime.now());

                            result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT)
                                    .contentType(MediaType.TEXT_XML).body(" "));
                        }
                    }
                } else if (Optional.contains("AddAuth")) {
                    if (getResponsetype.contains("GetParameterValuesResponse")) {
                        if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Add")) {
                            responsebody = Tr069ResponseHandler(Method, Parameters, Optional);
                        } else {
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 1));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 2));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 3));

                            //System.out.println("End: " + LocalTime.now());

                            result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(" "));
                        }
                    }
                } else {
                    responsebody = Tr069ResponseHandler(Method, Parameters, Optional);
                }

                taskhandlerRepo.delete(taskhandlerRepo.getByID(id));

                //System.out.println("End: " + LocalTime.now());

                result.setResult(
                        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
            }
            else{
                try {
                    SendUDPRequest(DeviceSN);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
            }

            //System.out.println("End: " + LocalTime.now());
            
        }, "MyThread for " + DeviceSerialNum).start();

        return result;
    }

    public void LogRequest(String Method, String Payload, String serial_num) {
        new Thread(() -> {
            //System.out.println(Payload);
            cpe_response_log newCPE_log = new cpe_response_log();
            newCPE_log.set_Method(Method);
            newCPE_log.set_Payload(Payload);
            newCPE_log.set_SN(serial_num);

            cpe_response_repo.save(newCPE_log);
        }, "logging " + serial_num).start();
    }

    public void CheckDeviceEventCode(String Payload) {
        //System.out.println(LocalTime.now() + "Current Thread: " + Thread.currentThread().getName());
        new Thread(() -> {
            SOAPBody soapBody = null;
            Integer NumEvent = 0;

            try {
                UpdateDeviceDetail(Payload);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                soapBody = getSoap.StringToSAOP(Payload).getSOAPBody();
            } catch (SOAPException e) {
                e.printStackTrace();
            }
            NumEvent = soapBody.getElementsByTagName("Event").item(0).getChildNodes().getLength();
            String serial_num = soapBody.getElementsByTagName("SerialNumber").item(0).getTextContent();
            
            /*Update device time*/
            httprequestlog logRequest = httplogreqRepo.getBySerialNumEquals(serial_num);
            logRequest.set_lastRequest(new Timestamp(System.currentTimeMillis()));
            httplogreqRepo.save(logRequest);

            for (int i = 0; i < NumEvent; i++) {
                String EventCode = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i)
                        .getChildNodes().item(0).getTextContent();
                //System.out.println("EventCode" + EventCode);
                if (EventCode.contains("BOOT")) {
                    String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
                    SaveTask(serial_num, "Command", ObjectName, "config");

                    device device = device_front.getBySerialNum(serial_num);
                    String deviceGroup = device.getparent();
                    if(!deviceGroup.matches("unassigned")){
                        String[] Devicesgroups = deviceGroup.split("/");
                        for (int k = 1; k < (Devicesgroups.length + 1); k++) {
                            StringBuilder sb = new StringBuilder();
                            for (int j = 1; j < k + 1; j++) {
                                if ((j - 1) > 0) {
                                    sb.append("/" + Devicesgroups[j - 1]);
                                }
                            }
                            //System.out.println("Group Device:" + sb.toString());
                            AddOldSSID(serial_num, sb.toString());
                            ApplyOldCommand(serial_num, sb.toString());
                        }
                    }
                }
                if (EventCode.contains("BOOTSTRAP")) {
                    if (device_front.findBySerialNum(serial_num).isEmpty()) {
                        try {
                            UpdateDevicesTable(Payload);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
                        SaveTask(serial_num, "Command", ObjectName, "config");
                    } else {
                        device device_to_bootstrap = device_front.getBySerialNum(serial_num);
                        if(!device_to_bootstrap.getparent().matches("unassigned")){
                            //System.out.println("bootstraping : " + device_to_bootstrap.getserial_number());
                            if (device_to_bootstrap.getstatus().contains("syncing") == false) {
                                device_to_bootstrap.setstatus("syncing");
                                device_front.save(device_to_bootstrap);
                                Bootstraping(serial_num);
                            }
                        }
                    }
                }
                if (EventCode.contains("PERIODIC") || EventCode.contains("CONNECTION REQUEST")
                        || EventCode.contains("VALUE CHANGE")) {
                    try {
                        UpdateDevicesTable(Payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*
                    httprequestlog logRequest = httplogreqRepo.getBySerialNumEquals(serial_num);
                    logRequest.set_lastRequest(new Timestamp(System.currentTimeMillis()));
                    httplogreqRepo.save(logRequest);
                    */
                }
            }
        }, "CheckEvent").start();
    }

    public void Bootstraping(String serial_num) { // search and destroy accesspoint objects
        new Thread(() -> {
            //System.out.println("Starting Delete AP process: " + serial_num);
            SaveTask(serial_num, "GetParameterNames", "Device.WiFi.AccessPoint.", "null");
            SaveTask(serial_num, "GetParameterValues", "Device.WiFi.AccessPoint.", "null");
            Integer num_ap = -1;
            String[] apTobeDelete = null;

            while (num_ap < 0) {
                String result = GetNumberOfParameters(serial_num, "GetParameterNames");
                if (result.matches("None") == false) {
                    if (result.matches("zero")) {
                        num_ap = 0;
                        break;
                    } else {
                        apTobeDelete = result.split(",", -1);
                        num_ap = apTobeDelete.length - 1;
                        break;
                    }
                }
                // if(num_ap>=0){ break; }
            }
            if (num_ap > 0) {
                DeleteMultipleObjects(serial_num, apTobeDelete, num_ap);
            }

            //System.out.println("Deleting AP: " + serial_num);

            //System.out.println("Starting Delet SSID process: " + serial_num);
            // search and destroy ssid objects
            SaveTask(serial_num, "GetParameterNames", "Device.WiFi.SSID.", "null");
            SaveTask(serial_num, "GetParameterValues", "Device.WiFi.SSID.", "null");

            Integer num_ssid = -1;
            String[] ssidTobeDelete = null;

            while (num_ssid < 0) {
                String result = GetNumberOfParameters(serial_num, "GetParameterNames");
                if (result.matches("None") == false) {
                    if (result.matches("zero")) {
                        num_ssid = 0;
                        break;
                    } else {
                        ssidTobeDelete = result.split(",", -1);
                        num_ssid = ssidTobeDelete.length - 1;
                        break;
                    }
                }
                /*
                 * if(num_ssid>=0){ break; }
                 */
            }
            if (num_ssid > 0) {
                DeleteMultipleObjects(serial_num, ssidTobeDelete, num_ssid);
            }
            // SaveTask(serial_num, "GetParameterValues", "Device.WiFi.SSID.", "null");

            //System.out.println("Deleting SSID: " + serial_num);

            // Create ssid and accesspoint based on the device's group
            //System.out.println("ADDOldSSID");

            device device = device_front.getBySerialNum(serial_num);
            String deviceGroup = device.getparent();
            String[] Devicesgroups = deviceGroup.split("/");
            for (int i = 1; i < (Devicesgroups.length + 1); i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < i + 1; j++) {
                    if ((j - 1) > 0) {
                        sb.append("/" + Devicesgroups[j - 1]);
                    }
                }
                //System.out.println("Group Device:" + sb.toString());
                AddOldSSID(serial_num, sb.toString());
                ApplyOldCommand(serial_num, sb.toString());
            }

            //System.out.println("Adding SSID: " + serial_num);
            // Add Nat
            String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
            SaveTask(serial_num, "Command", ObjectName, "config");
            // ConfigCWMP
            ObjectName = "{,Command:cwmp,Command:timer cpe-timeout 90,Command:cpe inform interval 180,Command:end,Command:write,}";
            SaveTask(serial_num, "Command", ObjectName, "config");
            // Set hostname
            List<device> deviceTobeset = device_front.findBySerialNum(serial_num);
            String deviceName = deviceTobeset.get(0).getdevice_name().replaceAll(" ", "_");
            if (deviceName == null) {
                deviceName = "DefaultAPName";
            }
            ObjectName = "{,Command:Set Hostname,Command:hostname " + deviceName
                    + ",Command:cpe inform interval 180,Command:end,Command:write,}";
            SaveTask(serial_num, "Command", ObjectName, "config");
            // Change device Status
            while (true) {
                List<taskhandler> remainingTask = taskhandlerRepo.findBySerialNumEquals(serial_num);
                Integer NumRemainingTask = remainingTask.size();
                if (NumRemainingTask < 1) {
                    device device_to_bootstrap = device_front.getBySerialNum(serial_num);
                    device_to_bootstrap.setstatus("synced");
                    device_front.save(device_to_bootstrap);
                    break;
                }
            }
        }, "newThread").start();
    }

    private String GetNumberOfParameters(String serial_num, String Method) {
        cpe_response_log cpe_response = null;
        try {
            cpe_response = cpe_response_repo.getBySerialNumEquals(serial_num);
        } catch (Exception e) {
            cpe_response = null;
        }
        //System.out.println(cpe_response);
        if(cpe_response == null){
            return "None";
        }else{
            if (cpe_response.get_method().contains(Method)) {
                SOAPBody soapBody = null;
                try {
                    soapBody = getSoap.StringToSAOP(cpe_response.get_payload()).getSOAPBody();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
                Integer numOfParam = soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
                if (numOfParam == 0) {
                    return "zero";
                } else {
                    //System.out.println(soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().item(0).getChildNodes().item(0).getTextContent());
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

    private void DeleteMultipleObjects(String serial_num, String[] ObjName, Integer NumOfObj) {
        for (int i = 0; i < NumOfObj; i++) {
            if (ObjName[0].matches(".*\\d+.*")) {
                SaveTask(serial_num, "DeleteObject", ObjName[i], "None");
            }
        }
    }

    private void AddOldSSID(String serial_num, String deviceGroup) {
        // getSSID
        List<group_ssid> ssids = ssidRepo.findByGroup(deviceGroup);
        Integer num_ssid = ssids.size();
        for (int i = 0; i < num_ssid; i++) {
            group_ssid currentSsid = ssids.get(i);
            Integer wlan_id = currentSsid.getwlan_id();
            StringBuilder SSIDSettings = new StringBuilder();
            String encryptionMode = null;
            String encrypModetoConvert = currentSsid.getencryption_mode();

            //System.out.println("EncryptionMode: " + encrypModetoConvert);

            if (encrypModetoConvert.contains("Open")) {
                encryptionMode = "None";
            }
            if (encrypModetoConvert.contains("WPA-PSK")) {
                encryptionMode = "WPA-Personal";
            }
            if (encrypModetoConvert.contains("WPA2-PSK")) {
                encryptionMode = "WPA2-Personal";
            }

            SSIDSettings.append("{,Device.WiFi.SSID." + wlan_id + ".SSID:" + currentSsid.getssid());
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".LowerLayers:1&2");
            if (currentSsid.getforward_mode().contains("Nat")) {
                SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:true");
            } else {
                SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:false");
            }
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_FowardType:"
                    + currentSsid.getforward_mode());

            if (currentSsid.getforward_mode().contains("Bridge")) {
                SSIDSettings.append(
                        ",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_VLANID:" + currentSsid.getvlan_id());
            }
            SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.ModeEnabled:" + encryptionMode);
            if (encryptionMode.contains("None") == false) {
                SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.KeyPassphrase:"
                        + currentSsid.getpassphrase() + ",}");
            } else {
                SSIDSettings.append(",}");
            }
            //System.out.println("SSID-Settings: " + SSIDSettings.toString());
            AddNewSSID(SSIDSettings.toString(), serial_num, wlan_id.toString());

            if (currentSsid.getauth()) {
                StringBuilder AuthSettings = new StringBuilder();
                AuthSettings.append("{,WiFiDog");
                AuthSettings.append("," + currentSsid.getportal_ip());
                AuthSettings.append("," + currentSsid.getportal_url());
                AuthSettings.append(",js");
                AuthSettings.append("," + currentSsid.getgateway_id());
                AuthSettings.append(",true");
                AuthSettings.append("," + currentSsid.getseamless() + ",}");

                AddNewAuth(AuthSettings.toString(), serial_num, wlan_id.toString());
            }
        }
    }

    private void ApplyOldCommand(String serial_num, String device_group) {
        List<group_command> CommandsInGroup = GroupCommandRepo.findByParent(device_group);
        zeep_devices currentDevice = zeepDevicesRepo.gEntityBySerialnum(serial_num);
        String deviceModel = currentDevice.get_device_Model();

        for (int i = 0; i < CommandsInGroup.size(); i++) {
            group_command current_command = CommandsInGroup.get(i);
            String[] command_in_line = current_command.getcommand().split("\n", -1);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (int j = 0; j < command_in_line.length; j++) {
                sb.append(",Command:" + command_in_line[j]);
            }
            sb.append(",}");
            /*
             * ObjectName = "{,Command:Set Hostname,Command:hostname "
             * +deviceName+",Command:cpe inform interval 180,Command:end,Command:write,}";
             * SaveTask(serial_num, "Command", ObjectName, "config");
             */
            if (current_command.getmodel().contains("ALL")) {
                SaveTask(serial_num, "Command", sb.toString(), "config");
            } else {
                if (current_command.getmodel().contains(deviceModel)) {
                    SaveTask(serial_num, "Command", sb.toString(), "config");
                }
            }
        }
    }

    private String GetDeviceSerialNum(HttpServletRequest request) {

        // String currentCookie = request.getHeader("Cookie").split(",")[0];
        String currentCookie = request.getHeader("Cookie").split(";")[0];
        //System.out.println("CurrentCookie --- " + currentCookie);
        String DeviceSN = null;
        if (httplogreqRepo.findByCookie(currentCookie).isEmpty() == false) {
            DeviceSN = httplogreqRepo.getByCookie(currentCookie).get_SN();
        }
        return DeviceSN;
    }

    @Scheduled(fixedRate = 60000)
    private void ZabbixAPI_Test() throws IOException, JSONException {
        /*
            item type 4 is text
            item type 0 is numeric(float)
        */
        new Thread(()->{
            String group_id = "213";
            URL zabbix_url = null;
            try {
                zabbix_url = new URL("http://zabbix.apolloglobal.net/zabbix/api_jsonrpc.php");
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            }
            String auth = null;
            try {
                auth = zabbixRPC.Authentication(zabbix_url);
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
    
            
            Iterable<device> device_list = device_front.findAll();
            for (device device : device_list) {
                String device_name = device.getdevice_name();
                String hostid = null;
                try {
                    hostid = zabbixRPC.GetSpecificHost(device_name, auth, zabbix_url);
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                System.out.println(hostid);
                if(device.getstatus()!=null){
                    if(device.getstatus().matches("offline")){
                        //System.out.println(hostid);
                        if(hostid != null){
                            JSONArray items = null;
                            try {
                                items = zabbixRPC.GetItems(hostid, auth, zabbix_url);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            StringBuilder ItemsInHost = new StringBuilder();
                            if(items != null){
                                for(int i=0;i<items.length();i++){
                                    JSONObject current_item = null;
                                    try {
                                        current_item = items.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String itemkey = "null";
                                    try {
                                        itemkey = current_item.get("key_").toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ItemsInHost.append( itemkey + ";");
                                }
                                if(ItemsInHost.toString().contains("device.status")){
                                    try {
                                        zabbixRPC.UpdateItem(device_name, "device.status", device.getstatus());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    try {
                                        zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                try {
                                    zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            try {
                                zabbixRPC.CreateZabbixHost(zabbix_url, device_name, "202.60.10.89", group_id, auth);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        httprequestlog currentlog = httplogreqRepo.getBySerialNumEquals(device.getserial_number());
                        currentlog.set_device_status(device.getstatus());
                        httplogreqRepo.save(currentlog);
                    }
                    if(device.getstatus().matches("online")){
                        httprequestlog currentlog = httplogreqRepo.getBySerialNumEquals(device.getserial_number());
        
                        if(currentlog.get_device_status() == null){
                            currentlog.set_device_status(device.getstatus());
                            httplogreqRepo.save(currentlog);
                            //System.out.println(hostid);
                            if(hostid != null){
                                JSONArray items = null;
                                try {
                                    items = zabbixRPC.GetItems(hostid, auth, zabbix_url);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                StringBuilder ItemsInHost = new StringBuilder();
                                if(items != null){
                                    for(int i=0;i<items.length();i++){
                                        JSONObject current_item = null;
                                        try {
                                            current_item = items.getJSONObject(i);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        String itemkey = "null";
                                        try {
                                            itemkey = current_item.get("key_").toString();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ItemsInHost.append( itemkey + ";");
                                    }
                                    if(ItemsInHost.toString().contains("device.status")){
                                        try {
                                            zabbixRPC.UpdateItem(device_name, "device.status", device.getstatus());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        try {
                                            zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    try {
                                        zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                try {
                                    zabbixRPC.CreateZabbixHost(zabbix_url, device_name, "202.60.10.89", group_id, auth);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
    
                        }else{
                            if(currentlog.get_device_status().matches(device.getstatus()) == false){
                                currentlog.set_device_status(device.getstatus());
                                httplogreqRepo.save(currentlog);
                                if(hostid != null){
                                    JSONArray items = null;
                                    try {
                                        items = zabbixRPC.GetItems(hostid, auth, zabbix_url);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                    StringBuilder ItemsInHost = new StringBuilder();
                                    if(items != null){
                                        for(int i=0;i<items.length();i++){
                                            JSONObject current_item = null;
                                            try {
                                                current_item = items.getJSONObject(i);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            String itemkey = "null";
                                            try {
                                                itemkey = current_item.get("key_").toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ItemsInHost.append( itemkey + ";");
                                        }
                                        if(ItemsInHost.toString().contains("device.status")){
                                            try {
                                                zabbixRPC.UpdateItem(device_name, "device.status", device.getstatus());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            try {
                                                zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }else{
                                        try {
                                            zabbixRPC.CreateItem(hostid, "DeviceStatus", "device.status", auth, zabbix_url);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else{
                                    try {
                                        zabbixRPC.CreateZabbixHost(zabbix_url, device_name, "202.60.10.89", group_id, auth);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        currentlog.set_device_status(device.getstatus());
                        httplogreqRepo.save(currentlog);
                    }
                }   
            }
        }).start();
    }

    @Scheduled(fixedRate = 60000)
    private void DeviceStatusUpdate(){
        /*
        List<group_command> CommandsInGroup = GroupCommandRepo.findByParent("/apollo");
        for(int i=0; i<CommandsInGroup.size();i++){
            group_command current_command = CommandsInGroup.get(i);
            //System.out.println(current_command.get_command().split("\n",-1)[0]);
        }
        */
        
        Iterable<httprequestlog> listOfDevices = httplogreqRepo.findAll();
        for (httprequestlog httprequestlog : listOfDevices) {
            Long interval;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Long timeInterval = (long) 0;
            try {
                timeInterval = currentTime.getTime() - httprequestlog.get_lastRequest().getTime();    
            } catch (Exception e) {
                timeInterval = (long) (60000*5);
            }
            
            interval = timeInterval/60000;
            device curent_device = null;
            while(true){
                if(httprequestlog.get_SN()!=null){
                    curent_device = device_front.getBySerialNum(httprequestlog.get_SN());
                    break;
                }
            } 
            
            if(curent_device.getstatus().contains("syncing")==false){
                if(interval>3){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                    LocalDateTime now = LocalDateTime.now();
                    curent_device.setdate_offline(dtf.format(now));
                    device_front.save(curent_device);
                    UpdateDeviceStatus(httprequestlog.get_SN(), "offline");
                    if(curent_device.getparent().matches("unassigned")){
                        device_front.delete(curent_device);
                    }
                }
                else{
                    UpdateDeviceStatus(httprequestlog.get_SN(), "online");
                }
            }
            
            /*else{
                while(true){
                    List<taskhandler> remainingTask = taskhandlerRepo.findBySerialNumEquals(httprequestlog.get_SN());
                    Integer NumRemainingTask = remainingTask.size();
                    if(NumRemainingTask<1){
                        device device_to_bootstrap = device_front.getBySerialNum(httprequestlog.get_SN());
                        device_to_bootstrap.setstatus("synced");
                        device_front.save(device_to_bootstrap);
                        break;
                    }
                }
            }
            */
        }
    }

    private void SaveSNandCookie(String SN, String Cookie){
        if(httplogreqRepo.findBySerialNumEquals(SN).isEmpty()){
            httprequestlog newHttpLog = new httprequestlog();
            newHttpLog.set_SN(SN);
            newHttpLog.set_cookie("session="+Cookie);
            httplogreqRepo.save(newHttpLog);
        }else{
            httprequestlog newHttpLog = httplogreqRepo.getBySerialNumEquals(SN);
            newHttpLog.set_SN(SN);
            newHttpLog.set_cookie("session="+Cookie);
            httplogreqRepo.save(newHttpLog);
        }
    }

    private void UpdateDeviceDetail(String Payload) throws JSONException{
        SOAPBody InformData = null;
        Integer NumData = 0;
        try {
            InformData = getSoap.StringToSAOP(Payload).getSOAPBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NumData = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<NumData; i++){
            sb.append('"'+InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(0).getTextContent()+'"'+':');
            sb.append('"'+InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(1).getTextContent()+'"');
            if(i<(NumData-1)){
                sb.append(",");
            }
        }
        JSONObject object = new JSONObject('{'+sb.toString()+'}');
        
        if(zeepDevicesRepo.findBySerialNumEquals(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()){
            zeep_devices newDevice = new zeep_devices();
            newDevice.set_device_SN(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.set_device_Manufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            newDevice.set_device_OUI(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            newDevice.set_device_Model(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            newDevice.set_device_MAC_ADD(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            newDevice.set_udp_con_req_url(object.get("Device.ManagementServer.UDPConnectionRequestAddress").toString());
            zeepDevicesRepo.save(newDevice);
        }else{
            zeep_devices deviceUpdate = zeepDevicesRepo.gEntityBySerialnum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.set_device_SN(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.set_device_Manufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            deviceUpdate.set_device_OUI(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            deviceUpdate.set_device_Model(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            deviceUpdate.set_device_MAC_ADD(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            deviceUpdate.set_udp_con_req_url(object.get("Device.ManagementServer.UDPConnectionRequestAddress").toString());
            zeepDevicesRepo.save(deviceUpdate); 
        }
    }

    public void UpdateDevicesTable(String Payload) throws JSONException {
        //DevicesGet
        /*
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        //System.out.println("current thread:" + threadSet.size());
        for(Thread t : threadSet){
            //System.out.println("Thread Number:"+t.getId()+"-- " + t.getName() + ", "+t.getState());
        }
        */

        SOAPBody InformData = null;
        Integer NumData = 0;
        try {
            InformData = getSoap.StringToSAOP(Payload).getSOAPBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NumData = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<NumData; i++){
            sb.append('"'+InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(0).getTextContent()+'"'+':');
            sb.append('"'+InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(1).getTextContent()+'"');
            if(i<(NumData-1)){
                sb.append(",");
            }
        }
        JSONObject object = new JSONObject('{'+sb.toString()+'}');

        //System.out.println("Json Length: " + object.length());
        //System.out.println("Try JsonFind: " +object.get("Device.DeviceInfo.SoftwareVersion").toString());

        if(device_front.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()){
            device unassigned_device = new device();
            unassigned_device.setserial_number(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            unassigned_device.setmac_address(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            unassigned_device.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            unassigned_device.setstatus("online");
            unassigned_device.setparent("unassigned");
            
            //newDevice.set_date_modified(LocalTime.now().toString());
            //if(newDevice.getstatus().contains("syncing")==false){
            //    newDevice.setstatus("online");
            //}
            unassigned_device.setactivated(false);
            device_front.save(unassigned_device);

        }else{
            device newDevice = device_front.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setserial_number(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setmac_address(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            newDevice.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            //newDevice.set_date_modified(LocalTime.now().toString());
            if(newDevice.getstatus().contains("syncing")==false){
                newDevice.setstatus("online");
            }
            newDevice.setactivated(true);
            device_front.save(newDevice);
        }
    }

    public void SaveWebCLIOutput(String WebCLIOutput,String CommandUsed, String SN){
        //System.out.println("SavingCLI");
        //System.out.println(WebCLIOutput.length());
        //System.out.println(WebCLIOutput);
        new Thread(()->{
            byte[] webcli_byte = WebCLIOutput.getBytes(Charsets.UTF_8);
            byte[] command_byte = CommandUsed.getBytes(Charsets.UTF_8);

            System.out.println("Saving CLI Response: "+ new Timestamp(System.currentTimeMillis()));
            webcli_response_log webCLIlog = new webcli_response_log();
            webCLIlog.set_CommandOutput(webcli_byte);
            webCLIlog.set_device_sn(SN);
            webCLIlog.set_CommandUsed(command_byte);
            webCliRepo.save(webCLIlog);
            System.out.println("Saved CLI Response: "+ new Timestamp(System.currentTimeMillis()));
        
        }).start();

    }

    public void SendUDPRequest(@PathVariable String SN) throws IOException{
        
        new Thread(()->{
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }

            Instant instant = Instant.now();
            long timeStampSeconds = instant.toEpochMilli();

            //String result = "";
            zeep_devices current_device = zeepDevicesRepo.gEntityBySerialnum(SN);
            String udp_url = current_device.get_udp_con_req_url();
            String[] device_udp_url = udp_url.split(":");
            String host = device_udp_url[0];
            Integer portnum = Integer.parseInt(device_udp_url[1]);

          
            StringBuilder sb = new StringBuilder();
            

            Random r = new Random();
            int id = 10000000 + r.nextInt(90000000);

            sb.append("GET http://"+udp_url+"?ts="+timeStampSeconds+"&id="+id+"&un=&cn=XTG&sig=DEFAULTSIGDEFAULTSIGDEFAULTSIGDEFAULTSIG HTTP/1.1\r\n");
            sb.append("Accept:*/*\r\n");
            sb.append("Accept-Language:zh-cn\r\n");
            sb.append("host:localhost\r\n");
            sb.append("Content-Length:0\r\n");
            
            
            String msg = sb.toString();
            for(int i=0;i<2;i++){
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
        //result = host+":"+portnum;
        //return result;
    }

    public void SaveTask(String SN, String Method,String Parameters,String Optional){ 
        taskhandler newTasK = new taskhandler();
        newTasK.set_SN(SN);
        newTasK.set_method(Method);
        newTasK.set_parameters(Parameters);
        newTasK.set_optional(Optional);;
        taskhandlerRepo.save(newTasK);
    }

    public void UpdateDeviceStatus(String SerialNum, String Status){
        device devicestat = device_front.getBySerialNum(SerialNum);
        //System.out.println(devicestat.getstatus());
        if(devicestat.getstatus().contains("syncing")==false){
            devicestat.setstatus(Status);
        }
        device_front.save(devicestat);
    }

    private String Tr069ResponseHandler(String Method, String Parameters, String Option){
        
        if(Method.contains("AddObject")){
            String body =  tr069response.AddObject(Parameters);
            return body;
        }
        if(Method.contains("GetParameterValues")){
            String body = tr069response.GetParameterValues(Parameters);
            return body;
        }
        if(Method.contains("GetParameterNames")){
            String body = tr069response.GetParameterNames(Parameters);
            return body;
        }
        if(Method.contains("SetParameterValues")){
            String body = tr069response.SetParameterValues(Parameters);
            return body;
        }
        if(Method.contains("Command")){
            String body = tr069response.Command(Parameters, "config");
            return body;
        }
        if(Method.contains("WebCli")){
            String body = tr069response.Command(Parameters, Option);
            return body;
        }
        if(Method.contains("GetRPCMethods")){
            String body = tr069response.GetRPCMethods();
            return body;
        }
        if(Method.contains("Reboot")){
            String body = tr069response.Reboot();
            return body;
        }
        if(Method.contains("DeleteObject")){
            String body = tr069response.DeleteObject(Parameters);
            return body;
        }
        if(Method.contains("Save")){
            String body = tr069response.SaveConfig();
            return body;
        }
        if(Method.contains("FactoryReset")){
            String body = tr069response.FactoryReset();
            return body;
        }
        return "Wrong RPC_Method";
    }

    public void AddNewSSID(String SSIDSettings,String  SerialNum,String ObjectName){
        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID."+ObjectName+".X_WWW-RUIJIE-COM-CN_ExistStatus","None");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.SSID.["+ObjectName+"].","AddSSID");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.AccessPoint.["+ObjectName+"].","None");
        SaveTask(SerialNum, "SetParameterValues", SSIDSettings,"None");
        SaveTask(SerialNum, "Save", "None","None");
    }

    public void AddNewAuth(String SSIDSettings,String SerialNum,String ObjectName){
        String[] ProcessedString = SSIDSettings.split(",",-1);
        
        StringBuilder authSetting = new StringBuilder();
        authSetting.append("'{,Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_ModeEnabled:"+ProcessedString[1]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalIP:"+ProcessedString[2]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalUrl:http1//"+ProcessedString[3]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayIP:1.2.3.4,");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_RedirectMode:"+ProcessedString[4]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayID:"+ProcessedString[5]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication."+ObjectName+".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_OffDetectEnable:"+ProcessedString[6]+",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_AuthenticationGlobal.X_WWW-RUIJIE-COM-CN_StaPerceptionEnable:"+ProcessedString[7]+",}");
        
        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID."+ObjectName+".X_WWW-RUIJIE-COM-CN_ExistStatus","None");
        SaveTask(SerialNum, "Command", "'{,Command:dot11 wlan "+ObjectName+",Command:no band-select enable,}'","AddAuth");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication.["+ObjectName+"].","None");
        SaveTask(SerialNum, "SetParameterValues", authSetting.toString(),"None");
        SaveTask(SerialNum, "Save", "None","None");
    }

    @RequestMapping(value="/CliAutoComplete/ {SerialNum}")
    public DeferredResult<ResponseEntity<String>> CliAutoComplete(@RequestBody String Modes,@PathVariable String SerialNum, HttpServletRequest request )
            throws JSONException {  
        //System.out.println("Modez: "+ Modes);
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        new Thread(() -> {
            String body = "";
            String[] modez = Modes.split(",",-1);
            String ObjectName = modez[7];
            zeep_devices current_device = zeepDevicesRepo.gEntityBySerialnum(SerialNum);
            String deviceModel = current_device.get_device_Model();
            List<auto_complete> suggestion_lists = auto_completeRepo.findByDeviceModel(deviceModel);
            boolean found = false;
            if(!suggestion_lists.isEmpty()){
                for (auto_complete auto_complete : suggestion_lists) {
                    System.out.println("from db"+auto_complete.get_command());
                    System.out.println("from ObjName" + ObjectName);
                    if(ObjectName.contains(auto_complete.get_command())){
                        body = new String(auto_complete.get_suggestion_list(),Charsets.UTF_8);
                        found = true;
                        result.setResult( ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                    }
                }
                System.out.println("found " + found);
                if(!found){
                    try {
                        AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
    
                    while(true){
                        body = GetCLIOutput(SerialNum, ObjectName);
                        if(body!=null){
                            break;
                        }
                    }
                    try {
                        auto_complete NewSuggestion = new auto_complete();
                        NewSuggestion.set_device_model(deviceModel);
                        NewSuggestion.set_command(ObjectName);
                        NewSuggestion.set_suggestion_list(body.getBytes(Charsets.UTF_8));
                        auto_completeRepo.save(NewSuggestion);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    
                    
                    //System.out.println("test--------" + body);
                    result.setResult( ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }
            }
            else{
                if(!found){
                    try {
                        AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    while(true){
                        body = GetCLIOutput(SerialNum, ObjectName);
                        if(body!=null){
                            break;
                        }
                    }
                    try {
                        auto_complete NewSuggestion = new auto_complete();
                        NewSuggestion.set_device_model(deviceModel);
                        NewSuggestion.set_command(ObjectName);
                        NewSuggestion.set_suggestion_list(body.getBytes(Charsets.UTF_8));
                        auto_completeRepo.save(NewSuggestion);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    //System.out.println("test--------" + body);
                    result.setResult( ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }
            }
        }, "MyThread for " ).start();
        return result;
    }

    private String GetCLIOutput(String SerialNum, String ObjectName)
    {
        //String Byte2String = new String(webcli_byte, Charsets.UTF_8);
        String Outputbody = null;
        String CommandUsed = null;
        List<webcli_response_log> cliOutput = webCliRepo.findBySerialNumEquals(SerialNum);
        if(cliOutput!=null){
            Integer NumOutput = cliOutput.size();
            for(int i=0; i<NumOutput; i++){
                webcli_response_log currentCheck = cliOutput.get(i);
                CommandUsed = new String(currentCheck.get_CommandUsed(), Charsets.UTF_8);
                if(CommandUsed.contains("\""+ObjectName+"\"")){
                    Outputbody = new String(currentCheck.get_CommandOutput(), Charsets.UTF_8);                       
                    webCliRepo.delete(webCliRepo.getByID(currentCheck.get_Id()));
                    System.out.println("OutputBody: "+ new Timestamp(System.currentTimeMillis()));
                    return Outputbody;
                }
            }
        }
        return Outputbody;
    }

    private void AddWebCLiTask(String Modes,String SerialNum,String ObjectName)throws JSONException
    {
        if(webCliRepo.findBySerialNumEquals(SerialNum).isEmpty()){
            String Head = "web_cli \"exec\" \"0\" \"0\" \"0\" \"\" \"\" ";
            SaveTask(SerialNum, "WebCli", "{,\"Command\":"+Head+'"'+ObjectName+'"'+",}", "shell");
        }else{
            String[] modez = Modes.split(",",-1);
            StringBuilder Head = new StringBuilder();
            Head.append("web_cli ");
            
            //Head.append('"'+modez[0].replaceAll("[^a-zA-Z0-9]", "")+'"'+" ");
            Head.append('"'+modez[1]+'"'+" ");
            Head.append('"'+modez[2]+'"'+" ");
            Head.append('"'+modez[3]+'"'+" ");
            Head.append("\""+modez[4]+"\" ");
            Head.append("\""+modez[5]+"\" ");
            Head.append("\""+modez[6]+"\" ");
            //webCliRepo.delete(webCliRepo.getByID(ResponseLog.get(ResponseLog.size()-1).get_Id()));
            //System.out.println("HeadCLI: "+Head.toString());
            //System.out.println("WebCLI: "+"{,\"Command\":"+Head.toString()+'"'+ObjectName+'"'+",}");
            SaveTask(SerialNum, "WebCli", "{,\"Command\":"+Head.toString()+'"'+ObjectName+'"'+",}", "shell");            
        }
        System.out.println("Commited CLI Request: "+ new Timestamp(System.currentTimeMillis()));
    }
}
