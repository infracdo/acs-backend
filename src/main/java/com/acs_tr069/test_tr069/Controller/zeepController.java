package com.acs_tr069.test_tr069.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;

import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.CWMPResponses.GetSoapFromString;
import com.acs_tr069.test_tr069.Entity.HttpRequestLog;
import com.acs_tr069.test_tr069.Entity.TaskHandler;
import com.acs_tr069.test_tr069.Entity.Devices;
import com.acs_tr069.test_tr069.Entity.Device;

import com.acs_tr069.test_tr069.Repo.HttpLogReqRepository;
import com.acs_tr069.test_tr069.Repo.TaskHandlerRepository;
import com.acs_tr069.test_tr069.Services.HelperService;
import com.acs_tr069.test_tr069.Repo.DevicesRepository;
import com.acs_tr069.test_tr069.Repo.DeviceFrontendRepository;

import com.acs_tr069.test_tr069.StoreRequestResult.GetResponseResult;
import com.acs_tr069.test_tr069.ZabbixApi.ZabbixApiRPCCalls;
import com.acs_tr069.test_tr069.CWMPResponses.RandomCodeGen;

@CrossOrigin(origins = "*")
@RequestMapping(path = "/zeep/")
public class ZeepController { 

    @Autowired
    private HttpLogReqRepository httplogreqRepo;
    @Autowired
    private TaskHandlerRepository taskhandlerRepo;
    @Autowired
    private DevicesRepository devicesRepo; 
    @Autowired
    private DeviceFrontendRepository deviceFront;
    @Autowired
    private HelperService helperService;

    String cwmpheader = null;
    String Output = null;
    Integer stage = 0;
    Boolean SSIDAdded = false;

    private tr069Response tr069response;
    private GetSoapFromString getSoap;
    private RandomCodeGen randomGen;
    private ZabbixApiRPCCalls zabbixRPC;

    @PostMapping(value = "/")
    private DeferredResult<ResponseEntity<String>> TestDevice(@RequestBody(required = false) String xmlPayload, HttpServletRequest request, HttpServletResponse response) {
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
                DeviceSerialNum = helperService.GetDeviceSerialNum(request);
            }
        } else {
            DeviceSerialNum = helperService.GetDeviceSerialNum(request);
        }
        //System.out.println("DeviceThatRequest" + DeviceSerialNum);
        new Thread(() -> {
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

                    helperService.SaveSNandCookie(SN, SNCookie);
                    response.addHeader("Set-Cookie", "session=" + SNCookie);
                    responsebody = tr069response.InformResponse();

                    Device checkDevice = deviceFront.getBySerialNum(SN);
                    if(checkDevice == null){
                        try {
                            UpdateDevicesTable(xmlPayload);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
                    }
                    else {
                        CheckDeviceEventCode(xmlPayload);
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
                    }
                }

                //System.out.println(GetResponseResult.getResult(converteBody, getResponsetype));
                if (xmlPayload.contains("<cwmp:X_RUIJIE_COM_CN_ExecuteCliCommandResponse>")) {
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    String CommandUsed = converteBody.getElementsByTagName("Command").item(0).getTextContent();
                    String WebCliContent = GetResponseResult.getResult(converteBody, "X_RUIJIE_COM_CN_ExecuteCliCommandResponse");
                    System.out.println("Recieved CLI Response: "+ new Timestamp(System.currentTimeMillis()));
                    if (WebCliContent.matches("none") == false) {
                        helperService.SaveWebCLIOutput(WebCliContent, CommandUsed, DeviceSN);
                    }
                }

                if (xmlPayload.contains("<cwmp:GetParameterNamesResponse>")) {
                    //System.out.println(getResponsetype);
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    helperService.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
                if (xmlPayload.contains("<cwmp:RebootResponse>")) {
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    helperService.UpdateDeviceStatus(DeviceSN, "offline");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
            }

            String DeviceSN = helperService.GetDeviceSerialNum(request);

            if (taskhandlerRepo.findBySerialNumEquals(DeviceSN).isEmpty() == false) {
                List<TaskHandler> task = taskhandlerRepo.findBySerialNumEquals(DeviceSN);
                String Method = task.get(0).getMethod().toString();
                String Parameters = task.get(0).getParameters().toString();
                String Optional = task.get(0).getOptional();
                Long id = task.get(0).getId();

                if (Optional.contains("AddSSID")) {
                    if (getResponsetype.contains("GetParameterValuesResponse")) {
                        if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Delete")) {
                            responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                        } else {
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 1));
                            // taskhandlerRepo.delete(taskhandlerRepo.getByID(id+2));
                            // taskhandlerRepo.delete(taskhandlerRepo.getByID(id+3));
                            result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(" "));
                        }
                    }
                } else if (Optional.contains("AddAuth")) {
                    if (getResponsetype.contains("GetParameterValuesResponse")) {
                        if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Add")) {
                            responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                        } else {
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 1));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 2));
                            taskhandlerRepo.delete(taskhandlerRepo.getByID(id + 3));

                            result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(" "));
                        }
                    }
                } else {
                    responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                }

                taskhandlerRepo.delete(taskhandlerRepo.getByID(id));

                result.setResult(
                        ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
            }
            else{
                try {
                    helperService.SendUDPRequest(DeviceSN);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
            }

        }, "MyThread for " + DeviceSerialNum).start();

        return result;
    }

    private void CheckDeviceEventCode(String Payload) {
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
            
            HttpRequestLog logRequest = httplogreqRepo.getBySerialNumEquals(serial_num);
            logRequest.setLastRequest(new Timestamp(System.currentTimeMillis()));
            httplogreqRepo.save(logRequest);

            for (int i = 0; i < NumEvent; i++) {
                String EventCode = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i).getChildNodes().item(0).getTextContent();
                //System.out.println("EventCode" + EventCode);
                if (EventCode.contains("BOOT")) {
                    String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
                    helperService.SaveTask(serial_num, "Command", ObjectName, "config");

                    Device device = deviceFront.getBySerialNum(serial_num);
                    String deviceGroup = device.getParent();
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
                            helperService.AddOldSSID(serial_num, sb.toString());
                            helperService.ApplyOldCommand(serial_num, sb.toString());
                        }
                    }
                }
                if (EventCode.contains("BOOTSTRAP")) {
                    if (deviceFront.findBySerialNum(serial_num).isEmpty()) {
                        try {
                            UpdateDevicesTable(Payload);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
                        helperService.SaveTask(serial_num, "Command", ObjectName, "config");
                    } else {
                        Device device_to_bootstrap = deviceFront.getBySerialNum(serial_num);
                        if(!device_to_bootstrap.getParent().matches("unassigned")){
                            if (device_to_bootstrap.getStatus().contains("syncing") == false) {
                                device_to_bootstrap.setStatus("syncing");
                                deviceFront.save(device_to_bootstrap);
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
                }
            }
        }, "CheckEvent").start();
    }

    private void Bootstraping(String serial_num) { 
        new Thread(() -> {
            helperService.SaveTask(serial_num, "GetParameterNames", "Device.WiFi.AccessPoint.", "null");
            helperService.SaveTask(serial_num, "GetParameterValues", "Device.WiFi.AccessPoint.", "null");
            Integer num_ap = -1;
            String[] apTobeDelete = null;

            while (num_ap < 0) {
                String result = helperService.GetNumberOfParameters(serial_num, "GetParameterNames");
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
                helperService.DeleteMultipleObjects(serial_num, apTobeDelete, num_ap);
            }

            helperService.SaveTask(serial_num, "GetParameterNames", "Device.WiFi.SSID.", "null");
            helperService.SaveTask(serial_num, "GetParameterValues", "Device.WiFi.SSID.", "null");

            Integer num_ssid = -1;
            String[] ssidTobeDelete = null;

            while (num_ssid < 0) {
                String result = helperService.GetNumberOfParameters(serial_num, "GetParameterNames");
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
                helperService.DeleteMultipleObjects(serial_num, ssidTobeDelete, num_ssid);
            }
            // helperService.SaveTask(serial_num, "GetParameterValues", "Device.WiFi.SSID.", "null");

            Device device = deviceFront.getBySerialNum(serial_num);
            String deviceGroup = device.getParent();
            String[] Devicesgroups = deviceGroup.split("/");
            for (int i = 1; i < (Devicesgroups.length + 1); i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < i + 1; j++) {
                    if ((j - 1) > 0) {
                        sb.append("/" + Devicesgroups[j - 1]);
                    }
                }
                helperService.AddOldSSID(serial_num, sb.toString());
                helperService.ApplyOldCommand(serial_num, sb.toString());
            }

            String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
            helperService.SaveTask(serial_num, "Command", ObjectName, "config");

            ObjectName = "{,Command:cwmp,Command:timer cpe-timeout 90,Command:cpe inform interval 180,Command:end,Command:write,}";
            helperService.SaveTask(serial_num, "Command", ObjectName, "config");

            List<Device> deviceTobeset = deviceFront.findBySerialNum(serial_num);
            String deviceName = deviceTobeset.get(0).getDeviceName().replaceAll(" ", "_");
            if (deviceName == null) {
                deviceName = "DefaultAPName";
            }
            ObjectName = "{,Command:Set Hostname,Command:hostname " + deviceName + ",Command:cpe inform interval 180,Command:end,Command:write,}";
            helperService.SaveTask(serial_num, "Command", ObjectName, "config");

            while (true) {
                List<TaskHandler> remainingTask = taskhandlerRepo.findBySerialNumEquals(serial_num);
                Integer NumRemainingTask = remainingTask.size();
                if (NumRemainingTask < 1) {
                    Device device_to_bootstrap = deviceFront.getBySerialNum(serial_num);
                    device_to_bootstrap.setStatus("synced");
                    deviceFront.save(device_to_bootstrap);
                    break;
                }
            }
        }, "newThread").start();
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
    
            Iterable<Device> device_list = deviceFront.findAll();
            for (Device device : device_list) {
                String device_name = device.getDeviceName();
                String hostid = null;
                try {
                    hostid = zabbixRPC.GetSpecificHost(device_name, auth, zabbix_url);
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                System.out.println(hostid);
                if(device.getStatus()!=null){
                    if(device.getStatus().matches("offline")){
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
                                        zabbixRPC.UpdateItem(device_name, "device.status", device.getStatus());
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
                        HttpRequestLog currentlog = httplogreqRepo.getBySerialNumEquals(device.getSerialNumber());
                        currentlog.setDeviceStatus(device.getStatus());
                        httplogreqRepo.save(currentlog);
                    }
                    if(device.getStatus().matches("online")){
                        HttpRequestLog currentlog = httplogreqRepo.getBySerialNumEquals(device.getSerialNumber());
        
                        if(currentlog.getDeviceStatus() == null){
                            currentlog.setDeviceStatus(device.getStatus());
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
                                            zabbixRPC.UpdateItem(device_name, "device.status", device.getStatus());
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
                            if(currentlog.getDeviceStatus().matches(device.getStatus()) == false){
                                currentlog.setDeviceStatus(device.getStatus());
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
                                                zabbixRPC.UpdateItem(device_name, "device.status", device.getStatus());
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
                        currentlog.setDeviceStatus(device.getStatus());
                        httplogreqRepo.save(currentlog);
                    }
                }   
            }
        }).start();
    }

    @Scheduled(fixedRate = 60000)
    private void DeviceStatusUpdate(){        
        Iterable<HttpRequestLog> listOfDevices = httplogreqRepo.findAll();
        for (HttpRequestLog httprequestlog : listOfDevices) {
            Long interval;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Long timeInterval = (long) 0;
            try {
                timeInterval = currentTime.getTime() - httprequestlog.getLastRequest().getTime();    
            } catch (Exception e) {
                timeInterval = (long) (60000*5);
            }
            interval = timeInterval/60000;
            Device curent_device = null;
            while(true){
                if(httprequestlog.getSerialNum()!=null){
                    curent_device = deviceFront.getBySerialNum(httprequestlog.getSerialNum());
                    break;
                }
            } 
            
            if(curent_device.getStatus().contains("syncing")==false){
                if(interval>3){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                    LocalDateTime now = LocalDateTime.now();
                    curent_device.setDateOffline(dtf.format(now));
                    deviceFront.save(curent_device);
                    helperService.UpdateDeviceStatus(httprequestlog.getSerialNum(), "offline");
                    if(curent_device.getParent().matches("unassigned")){
                        deviceFront.delete(curent_device);
                    }
                }
                else{
                    helperService.UpdateDeviceStatus(httprequestlog.getSerialNum(), "online");
                }
            }
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
        
        if(devicesRepo.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()){
            Devices newDevice = new Devices();
            newDevice.setSerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setManufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            newDevice.setOui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            newDevice.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            newDevice.setMacAddress(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            newDevice.setUdpConReqUrl(object.get("Device.ManagementServer.UDPConnectionRequestAddress").toString());
            devicesRepo.save(newDevice);
        }else{
            Devices deviceUpdate = devicesRepo.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setSerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setManufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            deviceUpdate.setOui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            deviceUpdate.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            deviceUpdate.setMacAddress(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            deviceUpdate.setUdpConReqUrl(object.get("Device.ManagementServer.UDPConnectionRequestAddress").toString());
            devicesRepo.save(deviceUpdate);
        }
    }

    private void UpdateDevicesTable(String Payload) throws JSONException {
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
        //System.out.println("Try JsonFind: " +object.get("Device.DeviceInfo.SoftwareVersion").toString());

        if(deviceFront.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()){
            Device unassigned_device = new Device();
            unassigned_device.setSerialNumber(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            unassigned_device.setMacAddress(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            unassigned_device.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            unassigned_device.setStatus("online");
            unassigned_device.setParent("unassigned");
            
            //newDevice.set_date_modified(LocalTime.now().toString());
            //if(newDevice.getstatus().contains("syncing")==false){
            //    newDevice.setstatus("online");
            //}
            unassigned_device.setActivated(false);
            deviceFront.save(unassigned_device);

        }else{
            Device newDevice = deviceFront.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setSerialNumber(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setMacAddress(object.get("Device.DeviceInfo.X_WWW-RUIJIE-COM-CN_MACAddress").toString());
            newDevice.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            //newDevice.set_date_modified(LocalTime.now().toString());
            if(newDevice.getStatus().contains("syncing")==false){
                newDevice.setStatus("online");
            }
            newDevice.setActivated(true);
            deviceFront.save(newDevice);
        }
    }
    
    @Async("asyncExecutor")
    @RequestMapping(value = "/MoveDeviceGroup/{SerialNum}") 
    private CompletableFuture<String> MoveDeviceGroup(@PathVariable String SerialNum) {
        Device device_to_bootstrap = deviceFront.getBySerialNum(SerialNum);
        if (device_to_bootstrap.getStatus().contains("syncing") == false) {
            device_to_bootstrap.setStatus("syncing");
            deviceFront.save(device_to_bootstrap);
            Bootstraping(SerialNum);
        }
        return CompletableFuture.completedFuture("MoveDeviceGroup Initiated");
    }
}
