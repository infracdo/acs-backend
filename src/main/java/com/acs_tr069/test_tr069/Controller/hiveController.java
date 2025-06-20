package com.acs_tr069.test_tr069.Controller;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Bean;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.CWMPResponses.GetSoapFromString;
import com.acs_tr069.test_tr069.Entity.HttpRequestLog;
import com.acs_tr069.test_tr069.Entity.TaskHandler;
import com.acs_tr069.test_tr069.Entity.WebcliResponseLog;
import com.acs_tr069.test_tr069.Entity.GroupSsid;
import com.acs_tr069.test_tr069.Entity.Device;
import com.acs_tr069.test_tr069.Entity.DeviceModelParameters;
import com.acs_tr069.test_tr069.Entity.Devices;
import com.acs_tr069.test_tr069.Repo.HttpLogReqRepository;
import com.acs_tr069.test_tr069.Repo.TaskHandlerRepository;
import com.acs_tr069.test_tr069.Repo.WebcliResponseLogRepository;
import com.acs_tr069.test_tr069.Repo.DevicesRepository;
import com.acs_tr069.test_tr069.Services.HelperService;
import com.acs_tr069.test_tr069.Services.RlDeviceService;
import com.acs_tr069.test_tr069.Repo.SsidRepository;
import com.acs_tr069.test_tr069.Repo.DeviceFrontendRepository;
import com.acs_tr069.test_tr069.Repo.DeviceModelParametersRepository;
import com.acs_tr069.test_tr069.StoreRequestResult.GetResponseResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.acs_tr069.test_tr069.CWMPResponses.RandomCodeGen;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/hive/")
public class HiveController {

    @Autowired
    private HttpLogReqRepository httplogreqRepo; 
    @Autowired
    private TaskHandlerRepository taskhandlerRepo;
    @Autowired
    private DevicesRepository devicesRepo; 
    @Autowired
    private WebcliResponseLogRepository webCliRepo; 
    @Autowired
    private SsidRepository ssidRepo; 
    @Autowired
    private DeviceFrontendRepository device_front; 
    @Autowired
    private DeviceModelParametersRepository device_model_parameters_repo; 
    @Autowired
    private RlDeviceService rl_devices_services; 
    @Autowired
    private HelperService helperService;

    String cwmpheader = null;
    String Output = null;
    Integer stage = 0;
    Boolean SSIDAdded = false;

    private tr069Response tr069response;
    private GetSoapFromString getSoap;
    private RandomCodeGen randomGen;

    private Boolean faultDetected = false;
    private StringBuilder faults = new StringBuilder();

    @Value("${CoreThreads}")
    private int corethreads;

    @Value("${MaxCoreThreads}")
    private int maxcorethreads;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ApiResponse {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    // TANAN TR069 NA GINA SEND SA ROUTER DANI MO AGI
    @Async("asyncExecutor")
    @PostMapping(value = "/")
    private CompletableFuture<DeferredResult<ResponseEntity<String>>> TestDevice( @RequestBody(required = false) String xmlPayload, HttpServletRequest request, HttpServletResponse response) throws InterruptedException { 
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        String DeviceSerialNum = null;
        if (xmlPayload != null) {
            if (xmlPayload.contains("<cwmp:Inform>")) {
                System.out.println("--------------------------------------------------");
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

        new Thread(() -> {
            String responsebody = null;
            String SN = null;
            String parameterFault = null; 
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

                // System.out.println("ResponseType: " + getResponsetype);
                // getResponsetype = converteBody.getChildNodes().item(0).getLocalName();
                if (xmlPayload.contains("<cwmp:Inform>")) {
                    SNCookie = randomGen.CodeGenerator(18);
                    SN = converteBody.getElementsByTagName("SerialNumber").item(0).getTextContent();

                    helperService.SaveSNandCookie(SN, SNCookie);
                    response.addHeader("Set-Cookie", "session=" + SNCookie);
                    responsebody = tr069response.InformResponse();

                    Device check_device = device_front.getBySerialNum(SN);
                    if (check_device == null) {
                        try {
                            UpdateDevicesTable(xmlPayload);
                            UpdateDeviceDetail(xmlPayload); 
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
                    } else {
                        CheckDeviceEventCode(xmlPayload);
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
                    }
                }

                if (xmlPayload.contains("<cwmp:X_RUIJIE_COM_CN_ExecuteCliCommandResponse>")) {
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    String CommandUsed = converteBody.getElementsByTagName("Command").item(0).getTextContent();
                    String WebCliContent = GetResponseResult.getResult(converteBody,
                    "X_RUIJIE_COM_CN_ExecuteCliCommandResponse");
                    System.out.println("Received CLI Response: " + new Timestamp(System.currentTimeMillis()));
                    if (WebCliContent.matches("none") == false) {
                        helperService.SaveWebCLIOutput(WebCliContent, CommandUsed, DeviceSN);
                    }
                    if (CommandUsed.contains("show dot1 associations debug all-client")) { 
                        try {
                            helperService.SaveClients(DeviceSN, WebCliContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (CommandUsed.contains("show stamg sta all ip ipv4")) { 
                        try {
                            helperService.update_client(DeviceSN, WebCliContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (xmlPayload.contains("<cwmp:GetParameterNamesResponse>")) {
                    System.out.println("--------------------- Received Parameter Names ---------------------");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN); // uncommented in zeep ver
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }

                if (xmlPayload.contains("<cwmp:GetParameterValuesResponse>")) { 
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    System.out.println("--------------------- Received Parameter Values from " + DeviceSN + "---------------------");

                    NodeList nodeList = converteBody.getElementsByTagName("ParameterValueStruct");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node parameterValueStructNode = nodeList.item(i);
                        NodeList childNodes = parameterValueStructNode.getChildNodes();

                        String name = null;
                        String mac = null;

                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node childNode = childNodes.item(j);
                            if (childNode.getNodeName().equals("Name")) {
                                name = childNode.getTextContent().trim();
                            } else if (childNode.getNodeName().equals("Value")) {
                                mac = childNode.getTextContent().trim();
                            }
                        }

                        if (name != null && name.equals("InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2.MACAddress")) {
                            System.out.println("MAC Address: " + mac);
                            devicesRepo.updateSecondMacAddressBySerialNum(mac, DeviceSN);
                            break; 
                        }
                    }
                    helperService.LogRequest("GetParameterValues", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }

                if (xmlPayload.contains("<cwmp:Fault>")) { 
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    System.out.println("!!!!!!!!! Fault Response from " + DeviceSN + "!!!!!!!!!");
                    parameterFault = converteBody.getElementsByTagName("ParameterName").item(0).getTextContent();
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    if (xmlPayload.contains("9005"))
                        faults.append("SetParameterValuesFault: Invalid parameter name: " + parameterFault);
                    if (xmlPayload.contains("9007"))
                        faults.append("SetParameterValuesFault: Invalid parameter value for " + parameterFault);
                    if (xmlPayload.contains("9008"))
                        faults.append("SetParameterValuesFault: Non writeable parameter: " + parameterFault);
                    faultDetected = true;
                    if (faultDetected) {
                        System.out.println(faults.toString());
                    }
                    faults = new StringBuilder();
                }
                if (xmlPayload.contains("<cwmp:RebootResponse>")) {
                    String DeviceSN = helperService.GetDeviceSerialNum(request);
                    System.out.println("--------------------- Reboot Successful for " + DeviceSN + "---------------------");
                    helperService.UpdateDeviceStatus(DeviceSN, "offline");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
            }

            String DeviceSN = helperService.GetDeviceSerialNum(request);
            if (!DeviceSN.contains("None")) { 
                if (taskhandlerRepo.findBySerialNumEquals(DeviceSN).isEmpty() == false) {
                    List<TaskHandler> task = taskhandlerRepo.findBySerialNumEquals(DeviceSN);
                    String Method = task.get(0).getMethod().toString();
                    String Parameters = task.get(0).getParameters().toString();
                    String Optional = task.get(0).getOptional();

                    if (Optional.contains("AddSSID")) {
                        if (getResponsetype.contains("GetParameterValuesResponse")) {
                            if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Delete")) {
                                responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                            } else {
                                taskhandlerRepo.delete(task.get(0));
                                taskhandlerRepo.delete(task.get(1));
                                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(" "));
                            }
                        }
                    } else if (Optional.contains("AddAuth")) {
                        if (getResponsetype.contains("GetParameterValuesResponse")) {
                            if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Add")) {
                                responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                            } else {
                                taskhandlerRepo.delete(task.get(0));
                                taskhandlerRepo.delete(task.get(1));
                                taskhandlerRepo.delete(task.get(2));
                                taskhandlerRepo.delete(task.get(3));
                                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(" "));
                            }
                        }
                    } else {
                        responsebody = helperService.Tr069ResponseHandler(Method, Parameters, Optional);
                    }

                    taskhandlerRepo.delete(task.get(0));
                    result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
                } else {
                    try {
                        helperService.SendUDPRequest(DeviceSN);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Devices current_device = devicesRepo.getBySerialNum(DeviceSN); 
                    System.out.println(current_device.getCwmpCycleEnd());
                    current_device.setCwmpCycleEnd(true); 
                    devicesRepo.save(current_device);
                    result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
                }
            } else { 
                Devices current_device = devicesRepo.getBySerialNum(DeviceSN);
                System.out.println(current_device);
                current_device.setCwmpCycleEnd(true);
                devicesRepo.save(current_device);
                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
            }
        }, "MyThread for " + DeviceSerialNum).start();
        return CompletableFuture.completedFuture(result);
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
                Integer num_nodes = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i).getChildNodes().getLength(); 
                for (int j = 0; j < num_nodes; j++) {
                    if (!soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent().trim().isEmpty()) {
                        String EventCode = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent();
                        if (EventCode.contains("BOOT")) {
                            Device device = device_front.getBySerialNum(serial_num);
                            String deviceGroup = device.getParent();
                            if (!deviceGroup.matches("unassigned")) {
                                String[] Devicesgroups = deviceGroup.split("/");
                                for (int k = 1; k < (Devicesgroups.length + 1); k++) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int l = 1; j < k + 1; l++) {
                                        if ((l - 1) > 0) {
                                            sb.append("/" + Devicesgroups[l - 1]);
                                        }
                                    }
                                    helperService.AddOldSSID(serial_num, sb.toString());
                                    helperService.ApplyOldCommand(serial_num, sb.toString());
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
                            } else {
                                Device device_to_bootstrap = device_front.getBySerialNum(serial_num);
                                if (!device_to_bootstrap.getParent().matches("unassigned")) {
                                    if (device_to_bootstrap.getStatus().contains("syncing") == false) {
                                        device_to_bootstrap.setStatus("syncing");
                                        device_front.save(device_to_bootstrap);
                                        Bootstraping(serial_num);
                                    }
                                }
                            }
                        }
                        if (EventCode.contains("PERIODIC") || EventCode.contains("CONNECTION REQUEST") || EventCode.contains("VALUE CHANGE")) {
                            try {
                                UpdateDevicesTable(Payload);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, "CheckEvent").start();
    }

    private void Bootstraping(String serial_num) { 
        new Thread(() -> {
            Device device = device_front.getBySerialNum(serial_num);
            Devices device_details = devicesRepo.getBySerialNum(serial_num);
            if (device_details.getManufacturer().equals("HGU")) {
                rl_devices_services.AddSSID(serial_num, "{ssid_id:2}");
                rl_devices_services.addWANConnectionDevice(serial_num, "{wlan_id:2,wlan_mode:2,wlan_vlan:2000}");
                rl_devices_services.AddWANIPConnection(serial_num, "{wlan_id:2,wlan_connection_id:1}");
            } else {
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
                }
                if (num_ssid > 0) {
                    helperService.DeleteMultipleObjects(serial_num, ssidTobeDelete, num_ssid);
                }

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

                List<Device> deviceTobeset = device_front.findBySerialNum(serial_num);
                String deviceName = deviceTobeset.get(0).getDeviceName().replaceAll(" ", "_");
                if (deviceName == null) {
                    deviceName = "DefaultAPName";
                }
                ObjectName = "{,Command:Set Hostname,Command:hostname " + deviceName
                        + ",Command:cpe inform interval 180,Command:end,Command:write,}";
                helperService.SaveTask(serial_num, "Command", ObjectName, "config");

                StringBuilder command = new StringBuilder();
                command.append("{,");
                command.append("Command:enable service web-server http,");
                command.append("Command:enable service web-server https,");
                command.append("Command:log_mng set up HTTP,");
                command.append("Command:log_mng set upd 300,");
                command.append("Command:macc wis enable,");
                command.append("Command:no service password-encryption,");
                command.append("Command:end,");
                command.append("Command:write,}");
                ObjectName = command.toString();
                helperService.SaveTask(serial_num, "Command", ObjectName, "config");

                command = new StringBuilder();
                command.append("{,");
                command.append("Command:log_mng set uu http://192.168.90.11:7547/macclog/log/upload,");
                command.append("Command:log_mng set log-server http://192.168.90.11:7547,");
                command.append("Command:macc wis enable,");
                command.append("Command:end,");
                command.append("Command:write,}");
                ObjectName = command.toString();
                helperService.SaveTask(serial_num, "Command", ObjectName, "config");
            }

            while (true) {
                List<TaskHandler> remainingTask = taskhandlerRepo.findBySerialNumEquals(serial_num);
                Integer NumRemainingTask = remainingTask.size();
                if (NumRemainingTask < 1) {
                    Device device_to_bootstrap = device_front.getBySerialNum(serial_num);
                    device_to_bootstrap.setStatus("synced");
                    device_front.save(device_to_bootstrap);
                    break;
                }
            }
        }, "newThread").start();
    }

    // TODO; add zabbixapi_test 

    @Scheduled(fixedRate = 60000)
    private void DeviceStatusUpdate() { 
        Iterable<WebcliResponseLog> webcli_logs = webCliRepo.findAll(); 
        for (WebcliResponseLog webcli_response_log : webcli_logs) {
            Long interv;
            Timestamp currTime = new Timestamp(System.currentTimeMillis());
            Long timeInterv = (long) 0;
            try {
                timeInterv = currTime.getTime() - webcli_response_log.getTimeSaved().getTime();
            } catch (Exception e) {
                timeInterv = (long) (60000 * 3);
            }
            interv = timeInterv / 60000;
            System.out.println("WebCli interval: " + interv);
            if (interv > 1) {
                webCliRepo.delete(webcli_response_log);
            }
        } 

        Iterable<HttpRequestLog> listOfDevices = httplogreqRepo.findAll();
        for (HttpRequestLog httprequestlog : listOfDevices) {
            Long interval;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Long timeInterval = (long) 0;
            try {
                timeInterval = currentTime.getTime() - httprequestlog.getLastRequest().getTime();
            } catch (Exception e) {
                timeInterval = (long) (60000 * 5);
            }

            interval = timeInterval / 60000;
            Device curent_device = null;
            while (true) {
                if (httprequestlog.getSerialNum() != null) {
                    curent_device = device_front.getBySerialNum(httprequestlog.getSerialNum());
                    break;
                }
            }

            if (curent_device.getStatus().contains("syncing") == false) {
                if (interval > 3) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    curent_device.setDateOffline(dtf.format(now));
                    device_front.save(curent_device);
                    helperService.UpdateDeviceStatus(httprequestlog.getSerialNum(), "offline");
                    if (curent_device.getParent().matches("unassigned")) {
                        device_front.delete(curent_device);
                    }
                } else {
                    helperService.UpdateDeviceStatus(httprequestlog.getSerialNum(), "online");
                }
            }
        }
    }

    private void UpdateDeviceDetail(String Payload) throws JSONException { 
        SOAPBody InformData = null;
        Integer NumData = 0;
        try {
            InformData = getSoap.StringToSAOP(Payload).getSOAPBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NumData = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
        JSONObject object = new JSONObject(); 

        try {
            for (int i = 0; i < NumData; i++) {
                if (InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().getLength() == 5) {
                    Integer num_nodes = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().getLength();
                    Integer num_child_nodes = 0;
                    String label = "";
                    String value = "";
                    for (int j = 0; j < num_nodes; j++) {
                        if (!InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent().trim().isEmpty()) {
                            if (num_child_nodes == 0) {
                                label = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent();
                                num_child_nodes++;
                            } else {
                                value = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent();
                                object.put(label, value);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DeviceModelParameters model_param = null;
        try {
            model_param = device_model_parameters_repo.searchByManufacturerAndModel(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent(), InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
        } catch (Exception e) {
            model_param = null;
        }

        if (devicesRepo.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()) {
            Devices newDevice = new Devices();
            newDevice.setSerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setManufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            newDevice.setOui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            newDevice.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            if (model_param != null) {
                try {
                    newDevice.setMacAddress(object.get(model_param.getMacAddressParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setUdpConReqUrl(object.get(model_param.getUdpConReqUrlParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setManagementIp(object.get(model_param.getManagementIpParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setPublicIp(object.get(model_param.getPublicIpParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setSecondWanMac(object.get(model_param.getSecondWanMac()).toString());
                    System.out.println("WAN2 Mac Added");
                } catch (Exception e) {
                }
                try {
                    newDevice.setHardwareVer(object.get(model_param.getHardwareVerParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setSoftwareVer(object.get(model_param.getSoftwareVerParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setConReqUrl(object.get(model_param.getConReqUrlParameter()).toString());
                } catch (Exception e) {
                }
            }
            newDevice.setCwmpCycleEnd(false);
            devicesRepo.save(newDevice);
        } else {
            Devices deviceUpdate = devicesRepo.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setSerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setManufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            deviceUpdate.setOui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            deviceUpdate.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());

            if (model_param != null) {
                try {
                    deviceUpdate.setMacAddress(object.get(model_param.getMacAddressParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setUdpConReqUrl(object.get(model_param.getUdpConReqUrlParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setManagementIp(object.get(model_param.getManagementIpParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setPublicIp(object.get(model_param.getPublicIpParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setSecondWanMac(object.get(model_param.getSecondWanMac()).toString());
                    System.out.println("WAN2 Mac Added");
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setHardwareVer(object.get(model_param.getHardwareVerParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setSoftwareVer(object.get(model_param.getSoftwareVerParameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setConReqUrl(object.get(model_param.getConReqUrlParameter()).toString());
                } catch (Exception e) {
                }
            }

            String DeviceSerialNum = InformData.getElementsByTagName("SerialNumber").item(0).getTextContent();
            String DeviceMacAddress = object.get(model_param.getMacAddressParameter()).toString();

            if (!device_front.findBySerialNumOnRogue(DeviceSerialNum).isEmpty()) {
                helperService.setDeviceInformInterval(DeviceSerialNum, 10);
                System.out.println(DeviceMacAddress);
            }

            Device dev = device_front.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setDeviceAlias(dev.getDeviceName());
            List<GroupSsid> ssid = ssidRepo.findByGroup(dev.getParent());
            StringBuilder strb = new StringBuilder();

            for (GroupSsid group_ssid : ssid) {
                strb.append(group_ssid.getSsid() + ",");
            }

            deviceUpdate.setSsids(strb.toString());
            // deviceUpdate.setUdpConReqUrl(ClientHost+":"+ClientPort);
            deviceUpdate.setCwmpCycleEnd(false);
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
        JSONObject object = new JSONObject(); 

        try {
            for (int i = 0; i < NumData; i++) {
                if (InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().getLength() == 5) {
                    Integer num_nodes = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().getLength();
                    Integer num_child_nodes = 0;
                    String label = "";
                    String value = "";

                    for (int j = 0; j < num_nodes; j++) {
                        if (!InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent().trim().isEmpty()) {
                            if (num_child_nodes == 0) {
                                label = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent();
                                num_child_nodes++;
                            } else {
                                value = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes().item(j).getTextContent();
                                object.put(label, value);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DeviceModelParameters model_param = null;
        try {
            model_param = device_model_parameters_repo.searchByManufacturerAndModel(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent(), InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
        } catch (Exception e) {
            model_param = null;
        }

        if (device_front.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent()).isEmpty()) {
            Device unassigned_device = new Device();
            unassigned_device.setSerialNumber(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            if (model_param != null) {
                unassigned_device.setMacAddress(object.get(model_param.getMacAddressParameter()).toString());
                try {
                    unassigned_device.setSecondWanMac(object.get(model_param.getSecondWanMac()).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            unassigned_device.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            unassigned_device.setStatus("online");
            unassigned_device.setParent("unassigned");
            unassigned_device.setActivated(false);
            device_front.save(unassigned_device);
        } else {
            Device newDevice = device_front.getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setSerialNumber(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            if (model_param != null) {
                newDevice.setMacAddress(object.get(model_param.getMacAddressParameter()).toString());
            }
            
            newDevice.setModel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            // newDevice.set_date_modified(LocalTime.now().toString());
            if (newDevice.getStatus().contains("syncing") == false) {
                newDevice.setStatus("online");
            }
            newDevice.setActivated(true);
            device_front.save(newDevice);
        }
    }
    
    @Async("asyncExecutor")
    @RequestMapping(value = "/MoveDeviceGroup/{SerialNum}") 
    private CompletableFuture<String> MoveDeviceGroup(@PathVariable String SerialNum) {
        Device device_to_bootstrap = device_front.getBySerialNum(SerialNum);
        if (device_to_bootstrap.getStatus().contains("syncing") == false) {
            device_to_bootstrap.setStatus("syncing");
            device_front.save(device_to_bootstrap);
            Bootstraping(SerialNum);
        }
        return CompletableFuture.completedFuture("MoveDeviceGroup Initiated");
    }
}
