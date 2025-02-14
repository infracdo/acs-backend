package com.acs_tr069.test_tr069.Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.tomcat.jni.File;
import org.opensaml.util.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.CWMPResponses.GetSoapFromString;
import com.acs_tr069.test_tr069.Entity.httprequestlog;
import com.acs_tr069.test_tr069.Entity.radio_info;
import com.acs_tr069.test_tr069.Entity.taskhandler;
import com.acs_tr069.test_tr069.Entity.webcli_response_log;
import com.acs_tr069.test_tr069.Entity.devices;
import com.acs_tr069.test_tr069.Entity.group_command;
import com.acs_tr069.test_tr069.Entity.auto_complete;
import com.acs_tr069.test_tr069.Entity.client_list;
import com.acs_tr069.test_tr069.Entity.cpe_response_log;
import com.acs_tr069.test_tr069.Entity.group_ssid;
import com.acs_tr069.test_tr069.Entity.groups;
import com.acs_tr069.test_tr069.Entity.device;
import com.acs_tr069.test_tr069.Entity.device_logs;
import com.acs_tr069.test_tr069.Entity.device_model_parameters;
import com.acs_tr069.test_tr069.Entity.device_traffic_24h;
import com.acs_tr069.test_tr069.Entity.device_traffic_daily;
import com.acs_tr069.test_tr069.Repo.httplogreqRepo;
import com.acs_tr069.test_tr069.Repo.radio_infoRepository;
import com.acs_tr069.test_tr069.Repo.taskhandlerRepo;
import com.acs_tr069.test_tr069.Repo.webcli_response_logRepo;
import com.acs_tr069.test_tr069.Services.rlDeviceService;
import com.acs_tr069.test_tr069.Repo.auto_completeRepository;
import com.acs_tr069.test_tr069.Repo.client_listRepository;
import com.acs_tr069.test_tr069.Repo.cpe_response_logRepository;
import com.acs_tr069.test_tr069.Repo.devicesRepository;
import com.acs_tr069.test_tr069.Repo.group_commandRepo;
import com.acs_tr069.test_tr069.Repo.groupsRepository;
import com.acs_tr069.test_tr069.Repo.ssidRepository;
import com.acs_tr069.test_tr069.Repo.device_frontendRepository;
import com.acs_tr069.test_tr069.Repo.device_logsRepository;
import com.acs_tr069.test_tr069.Repo.device_model_parametersRepository;
import com.acs_tr069.test_tr069.Repo.device_traffic_24hRepository;
import com.acs_tr069.test_tr069.Repo.device_traffic_dailyRepository;
import com.acs_tr069.test_tr069.StoreRequestResult.GetResponseResult;
import com.acs_tr069.test_tr069.UDP.udp_sender;
import com.acs_tr069.test_tr069.ZabbixApi.ZabbixApiRPCCalls;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo.None;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.google.common.base.Charsets;
import com.acs_tr069.test_tr069.CWMPResponses.RandomCodeGen;

@CrossOrigin(origins = "*")
@RestController
public class testController {

    @Autowired
    private httplogreqRepo httplogreqRepo;
    @Autowired
    private taskhandlerRepo taskhandlerRepo;
    @Autowired
    private devicesRepository devicesRepo;
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
    private groupsRepository group_repo;
    @Autowired
    private auto_completeRepository auto_completeRepo;
    @Autowired
    private radio_infoRepository radio_infoRepo;
    @Autowired
    private device_logsRepository device_logRepo;
    @Autowired
    private device_traffic_24hRepository dev_traff_24Repo;
    @Autowired
    private device_traffic_dailyRepository dev_traff_dailyRepo;
    @Autowired
    private client_listRepository client_listRepo;

    @Autowired
    private device_model_parametersRepository device_model_parameters_repo;

    @Autowired
    private rlDeviceService rl_devices_services;

    String cwmpheader = null;
    String Output = null;
    Integer stage = 0;
    Boolean SSIDAdded = false;

    private tr069Response tr069response;
    private GetSoapFromString getSoap;
    private RandomCodeGen randomGen;
    private ZabbixApiRPCCalls zabbixRPC;

    private int pendingTask = 0;
    private Boolean faultDetected = false;
    private StringBuilder faults = new StringBuilder();

    @Value("${CoreThreads}")
    private int corethreads;

    @Value("${MaxCoreThreads}")
    private int maxcorethreads;
    private RestTemplate restTemplate;

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

    /*
     * public void setup() throws SocketException{
     * System.out.println("UDP server start");
     * new udp_server().start();
     * }
     * 
     * @Scheduled(fixedDelay = 1000)
     * public void StartUDP() throws SocketException{
     * System.out.println("UDP server start");
     * new udp_server().start();
     * }
     */

    @Async("asyncExecutor")
    @PostMapping(value = "/")
    public CompletableFuture<DeferredResult<ResponseEntity<String>>> TestDevice(
            @RequestBody(required = false) String xmlPayload,
            HttpServletRequest request, HttpServletResponse response) throws InterruptedException {

        String ClientHost = request.getRemoteHost();
        String ClientPort = "" + request.getRemotePort();
        // System.out.println("Start: " + LocalTime.now());

        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        String DeviceSerialNum = null;
        if (xmlPayload != null) {
            if (xmlPayload.contains("<cwmp:Inform>")) {

                System.out.println("--------------------------------------------------");
                SOAPBody convertB = null;
                try {
                    convertB = getSoap.StringToSAOP(xmlPayload).getSOAPBody();
                } catch (SOAPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                DeviceSerialNum = convertB.getElementsByTagName("SerialNumber").item(0).getTextContent();

            } else {
                DeviceSerialNum = GetDeviceSerialNum(request);
            }
        } else {
            DeviceSerialNum = GetDeviceSerialNum(request);
        }
        // System.out.println("DeviceThatRequest" + DeviceSerialNum);

        new Thread(() -> {

            // //System.out.println("Execute method asynchronously - " +
            // Thread.currentThread().getName());
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // System.out.println("ResponseType: " + getResponsetype);
                // getResponsetype = converteBody.getChildNodes().item(0).getLocalName();

                if (xmlPayload.contains("<cwmp:Inform>")) {

                    SNCookie = randomGen.CodeGenerator(18);
                    SN = converteBody.getElementsByTagName("SerialNumber").item(0).getTextContent();

                    SaveSNandCookie(SN, SNCookie);
                    response.addHeader("Set-Cookie", "session=" + SNCookie);
                    responsebody = tr069response.InformResponse();

                    device check_device = device_front.getBySerialNum(SN);
                    if (check_device == null) {
                        try {
                            UpdateDevicesTable(xmlPayload);
                            UpdateDeviceDetail(xmlPayload, ClientHost, ClientPort);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML)
                                .body(responsebody));
                    } else {
                        CheckDeviceEventCode(xmlPayload, ClientHost, ClientPort);
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML)
                                .body(responsebody));
                    }
                }
                // System.out.println(GetResponseResult.getResult(converteBody,
                // getResponsetype));

                if (xmlPayload.contains("<cwmp:X_RUIJIE_COM_CN_ExecuteCliCommandResponse>")) {

                    String DeviceSN = GetDeviceSerialNum(request);
                    String CommandUsed = converteBody.getElementsByTagName("Command").item(0).getTextContent();
                    String WebCliContent = GetResponseResult.getResult(converteBody,
                            "X_RUIJIE_COM_CN_ExecuteCliCommandResponse");
                    System.out.println("Recieved CLI Response: " + new Timestamp(System.currentTimeMillis()));
                    if (WebCliContent.matches("none") == false) {
                        SaveWebCLIOutput(WebCliContent, CommandUsed, DeviceSN);
                    }
                    if (CommandUsed.contains("show dot1 associations debug all-client")) {
                        try {
                            SaveClients(DeviceSN, WebCliContent);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (CommandUsed.contains("show stamg sta all ip ipv4")) {
                        try {
                            update_client(DeviceSN, WebCliContent);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

                if (xmlPayload.contains("<cwmp:GetParameterNamesResponse>")) {
                    // System.out.println(getResponsetype);
                    System.out.println("--------------------- Received Parameter Names ---------------------");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }

                if (xmlPayload.contains("<cwmp:GetParameterValuesResponse>")) {
                    // System.out.println(getResponsetype);
                    String DeviceSN = GetDeviceSerialNum(request);
                    System.out.println("--------------------- Received Parameter Values from " + DeviceSN
                            + "---------------------");

                    NodeList nodeList = converteBody.getElementsByTagName("ParameterValueStruct");

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node parameterValueStructNode = nodeList.item(i);
                        NodeList childNodes = parameterValueStructNode.getChildNodes();

                        String name = null;
                        String mac = null;

                        // Iterate over child nodes to find Name and Value elements
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node childNode = childNodes.item(j);
                            if (childNode.getNodeName().equals("Name")) {
                                name = childNode.getTextContent().trim();
                            } else if (childNode.getNodeName().equals("Value")) {
                                mac = childNode.getTextContent().trim();
                            }
                        }

                        // Check if the Name matches the desired parameter
                        if (name != null && name.equals(
                                "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2.MACAddress")) {
                            System.out.println("MAC Address: " + mac);
                            devicesRepo.updateSecondMacAddressBySerialNum(mac, DeviceSN);
                            break; // No need to continue searching once found
                        }

                    }

                    LogRequest("GetParameterValues", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }

                if (xmlPayload.contains("<cwmp:Fault>")) {
                    // System.out.println(getResponsetype);
                    String DeviceSN = GetDeviceSerialNum(request);
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
                    System.out.println(faults.toString());
                    faults = new StringBuilder();

                }
                if (xmlPayload.contains("<cwmp:RebootResponse>")) {
                    String DeviceSN = GetDeviceSerialNum(request);
                    System.out.println(
                            "--------------------- Reboot Successful for " + DeviceSN + "---------------------");
                    UpdateDeviceStatus(DeviceSN, "offline");
                    // LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                    // async_method.LogRequest("GetParameterNames", xmlPayload, DeviceSN);
                }
            }

            String DeviceSN = GetDeviceSerialNum(request);

            if (!DeviceSN.contains("None")) {
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
                                taskhandlerRepo.delete(task.get(0));
                                taskhandlerRepo.delete(task.get(1));

                                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT)
                                        .contentType(MediaType.TEXT_XML).body(" "));
                            }
                        }
                    } else if (Optional.contains("AddAuth")) {
                        if (getResponsetype.contains("GetParameterValuesResponse")) {
                            if (GetResponseResult.getResult(converteBody, getResponsetype).contains("Add")) {
                                responsebody = Tr069ResponseHandler(Method, Parameters, Optional);
                            } else {
                                taskhandlerRepo.delete(task.get(0));
                                taskhandlerRepo.delete(task.get(1));
                                taskhandlerRepo.delete(task.get(2));
                                taskhandlerRepo.delete(task.get(3));

                                result.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT)
                                        .contentType(MediaType.TEXT_XML).body(" "));
                            }
                        }
                    } else {
                        responsebody = Tr069ResponseHandler(Method, Parameters, Optional);
                    }

                    taskhandlerRepo.delete(task.get(0));

                    // System.out.println("End: " + LocalTime.now());

                    result.setResult(
                            ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_XML).body(responsebody));
                } else {
                    try {
                        SendUDPRequest(DeviceSN);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        // System.out.println(e);
                    }
                    devices current_device = devicesRepo.gEntityBySerialnum(DeviceSN);
                    System.out.println(current_device.getcwmp_cycle_end());
                    current_device.setcwmp_cycle_end(true);
                    devicesRepo.save(current_device);
                    result.setResult(
                            ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
                }
            } else {
                devices current_device = devicesRepo.gEntityBySerialnum(DeviceSN);
                System.out.println(current_device);
                current_device.setcwmp_cycle_end(true);
                devicesRepo.save(current_device);
                result.setResult(
                        ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.TEXT_XML).body(null));
            }
            // System.out.println("End: " + LocalTime.now());

        }, "MyThread for " + DeviceSerialNum).start();

        return CompletableFuture.completedFuture(result);
    }

    public void SaveClients(String serial_num, String content) throws JSONException {
        System.out.println(content);
        JSONObject data = new JSONObject(content);
        new Thread(() -> {
            List<client_list> clients = client_listRepo.findBySerialNumEquals(serial_num);
            for (client_list client_list : clients) {
                client_listRepo.delete(client_list);
            }
            String string_data = null;
            try {
                string_data = data.getString("content");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String[] data_array = string_data.split("\r\n", -1);
            for (int i = 3; i < data_array.length; i++) {
                String[] datas = data_array[i].split("\\s+", -1);
                client_list client = new client_list();
                if (datas[0].contains("1")) {
                    client.setband("2.4G");
                } else {
                    client.setband("5G");
                }
                client.setmacc(datas[2]);
                client.setrssi(datas[7]);
                DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                client.setup(dt_date.format(now));
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
                client.settraffic(datas[5]);
                client.setssid(ssid.toString());
                client_listRepo.save(client);
            }
        }).start();
    }

    public void update_client(String serial_num, String content) throws JSONException {
        // System.out.println(content);
        JSONObject data = new JSONObject(content);
        String data_content = data.getString("content");
        new Thread(() -> {
            String[] lines = data_content.split("\r\n", -1);

            List<client_list> clients = client_listRepo.findBySerialNumEquals(serial_num);
            for (client_list client_list : clients) {
                String[] macc = client_list.getmacc().split(":", -1);
                int i = 0;
                for (String string_line : lines) {
                    String macc_address = macc[0] + macc[1] + "." + macc[2] + macc[3] + "." + macc[4] + macc[5];
                    if (string_line.contains(macc_address)) {
                        String[] ip = lines[i + 1].split("\\s+", -1);
                        client_list.setip(ip[1]);
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
            // System.out.println(Payload);
            cpe_response_log newCPE_log = new cpe_response_log();
            newCPE_log.set_Method(Method);
            newCPE_log.set_Payload(Payload);
            newCPE_log.set_SN(serial_num);

            cpe_response_repo.save(newCPE_log);
        }, "logging " + serial_num).start();
    }

    public void CheckDeviceEventCode(String Payload, String ClientHost, String ClientPort) {
        // System.out.println(LocalTime.now() + "Current Thread: " +
        // Thread.currentThread().getName());
        new Thread(() -> {
            SOAPBody soapBody = null;
            Integer NumEvent = 0;

            try {
                UpdateDeviceDetail(Payload, ClientHost, ClientPort);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                soapBody = getSoap.StringToSAOP(Payload).getSOAPBody();
            } catch (SOAPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            NumEvent = soapBody.getElementsByTagName("Event").item(0).getChildNodes().getLength();
            String serial_num = soapBody.getElementsByTagName("SerialNumber").item(0).getTextContent();

            /* Update device time */
            httprequestlog logRequest = httplogreqRepo.getBySerialNumEquals(serial_num);
            logRequest.set_lastRequest(new Timestamp(System.currentTimeMillis()));
            httplogreqRepo.save(logRequest);

            for (int i = 0; i < NumEvent; i++) {
                Integer num_nodes = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i)
                        .getChildNodes().getLength();
                for (int j = 0; j < num_nodes; j++) {
                    if (!soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i).getChildNodes().item(j)
                            .getTextContent().trim().isEmpty()) {
                        String EventCode = soapBody.getElementsByTagName("Event").item(0).getChildNodes().item(i)
                                .getChildNodes().item(j).getTextContent();
                        // System.out.println("EventCode" + EventCode);
                        if (EventCode.contains("BOOT")) {
                            device device = device_front.getBySerialNum(serial_num);
                            String deviceGroup = device.getparent();
                            if (!deviceGroup.matches("unassigned")) {
                                String[] Devicesgroups = deviceGroup.split("/");
                                for (int k = 1; k < (Devicesgroups.length + 1); k++) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int l = 1; j < k + 1; l++) {
                                        if ((l - 1) > 0) {
                                            sb.append("/" + Devicesgroups[l - 1]);
                                        }
                                    }
                                    // System.out.println("Group Device:" + sb.toString());
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
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                // String ObjectName = "{,Command:macc nat-config vlan 233 network 10.233.2.0
                                // 255.255.255.0,Command:interface BVI 233,Command:ip address 10.233.2.1
                                // 255.255.255.0,Command:ip nat inside,Command:end,Command:write,}";
                                // SaveTask(serial_num, "Command", ObjectName, "config");
                            } else {
                                device device_to_bootstrap = device_front.getBySerialNum(serial_num);
                                if (!device_to_bootstrap.getparent().matches("unassigned")) {
                                    // System.out.println("bootstraping : " +
                                    // device_to_bootstrap.getserial_number());
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
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            /*
                             * httprequestlog logRequest = httplogreqRepo.getBySerialNumEquals(serial_num);
                             * logRequest.set_lastRequest(new Timestamp(System.currentTimeMillis()));
                             * httplogreqRepo.save(logRequest);
                             */
                        }
                    }
                }

            }
        }, "CheckEvent").start();
    }

    public void Bootstraping(String serial_num) {
        new Thread(() -> {
            device device = device_front.getBySerialNum(serial_num);
            devices device_details = devicesRepo.gEntityBySerialnum(serial_num);
            if (device_details.getmanufacturer().equals("HGU")) {
                rl_devices_services.AddSSID(serial_num, "{ssid_id:2}");
                rl_devices_services.addWANConnectionDevice(serial_num, "{wlan_id:2,wlan_mode:2,wlan_vlan:2000}");
                rl_devices_services.AddWANIPConnection(serial_num, "{wlan_id:2,wlan_connection_id:1}");
            } else {
                // search and destroy accesspoint objects
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
                }
                if (num_ap > 0) {
                    DeleteMultipleObjects(serial_num, apTobeDelete, num_ap);
                }

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
                }
                if (num_ssid > 0) {
                    DeleteMultipleObjects(serial_num, ssidTobeDelete, num_ssid);
                }

                // Create ssid and accesspoint based on the device's group

                String deviceGroup = device.getparent();
                String[] Devicesgroups = deviceGroup.split("/");
                for (int i = 1; i < (Devicesgroups.length + 1); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < i + 1; j++) {
                        if ((j - 1) > 0) {
                            sb.append("/" + Devicesgroups[j - 1]);
                        }
                    }
                    // System.out.println("Group Device:" + sb.toString());
                    AddOldSSID(serial_num, sb.toString());
                    ApplyOldCommand(serial_num, sb.toString());
                }

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
                SaveTask(serial_num, "Command", ObjectName, "config");

                command = new StringBuilder();
                command.append("{,");
                command.append("Command:log_mng set uu http://192.168.90.11:7547/macclog/log/upload,");
                command.append("Command:log_mng set log-server http://192.168.90.11:7547,");
                command.append("Command:macc wis enable,");
                command.append("Command:end,");
                command.append("Command:write,}");
                ObjectName = command.toString();
                SaveTask(serial_num, "Command", ObjectName, "config");
            }

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
        // List<cpe_response_log> cpe_response =
        // cpe_response_repo.findBySerialNumEquals(serial_num);
        // Integer NumOfResponses = cpe_response.size();
        /*
         * for(int i=0; i<NumOfResponses;i++){ cpe_response_log current_response =
         * cpe_response.get(i); if(current_response.get_method().contains(Method)){
         * SOAPBody soapBody = null; try { soapBody =
         * getSoap.StringToSAOP(current_response.get_payload()).getSOAPBody(); } catch
         * (SOAPException e) { // TODO Auto-generated catch block e.printStackTrace(); }
         * Integer numOfParam =
         * soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().
         * getLength();
         * //System.out.println(soapBody.getElementsByTagName("ParameterList").item(0).
         * getChildNodes().item(i).getChildNodes().item(0).getTextContent());
         * StringBuilder result = new StringBuilder(); for(int j=0;j<numOfParam;j++){
         * result.append(soapBody.getElementsByTagName("ParameterList").item(0).
         * getChildNodes().item(j).getChildNodes().item(0).getTextContent()+","); }
         * cpe_response_repo.delete(current_response); return result.toString(); } }
         */
        cpe_response_log cpe_response = null;
        try {
            cpe_response = cpe_response_repo.getBySerialNumEquals(serial_num);
        } catch (Exception e) {
            // TODO: handle exception
            cpe_response = null;
        }
        // System.out.println(cpe_response);
        if (cpe_response == null) {
            return "None";
        } else {
            if (cpe_response.get_method().contains(Method)) {
                SOAPBody soapBody = null;
                try {
                    soapBody = getSoap.StringToSAOP(cpe_response.get_payload()).getSOAPBody();
                } catch (SOAPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Integer numOfParam = soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
                if (numOfParam == 0) {
                    return "zero";
                } else {
                    // System.out.println(soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().item(0).getChildNodes().item(0).getTextContent());
                    StringBuilder result = new StringBuilder();
                    for (int j = 0; j < numOfParam; j++) {
                        result.append(soapBody.getElementsByTagName("ParameterList").item(0).getChildNodes().item(j)
                                .getChildNodes().item(0).getTextContent() + ",");
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

            // System.out.println("EncryptionMode: " + encrypModetoConvert);

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
            // System.out.println("SSID-Settings: " + SSIDSettings.toString());
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
        devices currentDevice = devicesRepo.gEntityBySerialnum(serial_num);
        String deviceModel = currentDevice.getmodel();

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
        String DeviceSN = null;
        try {
            // String currentCookie = request.getHeader("Cookie").split(",")[0];
            String currentCookie = request.getHeader("Cookie").split(";")[0];
            // System.out.println("CurrentCookie --- " + currentCookie);

            if (httplogreqRepo.findByCookie(currentCookie).isEmpty() == false) {
                DeviceSN = httplogreqRepo.getByCookie(currentCookie).get_SN();
            }
            return DeviceSN;
        } catch (Exception e) {
            // TODO: handle exception
            // DeviceSN = GetDeviceSerialNum(request);
            return "None";
        }
    }

    @Scheduled(fixedRate = 60000)
    private void DeviceStatusUpdate() {
        /*
         * List<group_command> CommandsInGroup =
         * GroupCommandRepo.findByParent("/apollo");
         * for(int i=0; i<CommandsInGroup.size();i++){
         * group_command current_command = CommandsInGroup.get(i);
         * //System.out.println(current_command.get_command().split("\n",-1)[0]);
         * }
         */
        System.out.println("Deleteing Old cli logs");

        Iterable<webcli_response_log> webcli_logs = webCliRepo.findAll();
        for (webcli_response_log webcli_response_log : webcli_logs) {
            Long interv;
            Timestamp currTime = new Timestamp(System.currentTimeMillis());
            Long timeInterv = (long) 0;
            try {
                timeInterv = currTime.getTime() - webcli_response_log.get_time_saved().getTime();
            } catch (Exception e) {
                timeInterv = (long) (60000 * 3);
                // TODO: handle exception
            }
            interv = timeInterv / 60000;
            System.out.println("WebCli interval: " + interv);
            if (interv > 1) {
                webCliRepo.delete(webcli_response_log);
            }
        }

        Iterable<httprequestlog> listOfDevices = httplogreqRepo.findAll();
        for (httprequestlog httprequestlog : listOfDevices) {
            Long interval;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Long timeInterval = (long) 0;
            try {
                timeInterval = currentTime.getTime() - httprequestlog.get_lastRequest().getTime();
            } catch (Exception e) {
                timeInterval = (long) (60000 * 5);
                // TODO: handle exception
            }

            interval = timeInterval / 60000;
            device curent_device = null;
            while (true) {
                if (httprequestlog.get_SN() != null) {
                    curent_device = device_front.getBySerialNum(httprequestlog.get_SN());
                    break;
                }
            }

            if (curent_device.getstatus().contains("syncing") == false) {
                if (interval > 3) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    curent_device.setdate_offline(dtf.format(now));
                    device_front.save(curent_device);
                    UpdateDeviceStatus(httprequestlog.get_SN(), "offline");
                    if (curent_device.getparent().matches("unassigned")) {
                        device_front.delete(curent_device);
                    }
                } else {
                    UpdateDeviceStatus(httprequestlog.get_SN(), "online");
                }
            }

            /*
             * else{
             * while(true){
             * List<taskhandler> remainingTask =
             * taskhandlerRepo.findBySerialNumEquals(httprequestlog.get_SN());
             * Integer NumRemainingTask = remainingTask.size();
             * if(NumRemainingTask<1){
             * device device_to_bootstrap =
             * device_front.getBySerialNum(httprequestlog.get_SN());
             * device_to_bootstrap.setstatus("synced");
             * device_front.save(device_to_bootstrap);
             * break;
             * }
             * }
             * }
             */
        }
    }

    private void SaveSNandCookie(String SN, String Cookie) {
        if (httplogreqRepo.findBySerialNumEquals(SN).isEmpty()) {
            httprequestlog newHttpLog = new httprequestlog();
            newHttpLog.set_SN(SN);
            newHttpLog.set_cookie("session=" + Cookie);
            httplogreqRepo.save(newHttpLog);
        } else {
            httprequestlog newHttpLog = httplogreqRepo.getBySerialNumEquals(SN);
            newHttpLog.set_SN(SN);
            newHttpLog.set_cookie("session=" + Cookie);
            httplogreqRepo.save(newHttpLog);
        }
    }

    private void UpdateDeviceDetail(String Payload, String ClientHost, String ClientPort) throws JSONException {
        SOAPBody InformData = null;
        Integer NumData = 0;
        try {
            InformData = getSoap.StringToSAOP(Payload).getSOAPBody();
        } catch (Exception e) {
            // TODO: handle exception
        }
        NumData = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();
        JSONObject object = new JSONObject();

        try {
            for (int i = 0; i < NumData; i++) {
                if (InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes()
                        .getLength() == 5) {
                    Integer num_nodes = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                            .getChildNodes().getLength();
                    Integer num_child_nodes = 0;
                    String label = "";
                    String value = "";
                    for (int j = 0; j < num_nodes; j++) {
                        if (!InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                .getChildNodes().item(j).getTextContent().trim().isEmpty()) {
                            if (num_child_nodes == 0) {
                                label = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                        .getChildNodes().item(j).getTextContent();
                                num_child_nodes++;
                            } else {
                                value = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                        .getChildNodes().item(j).getTextContent();
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

        // System.out.println("Json: " + object.toString());

        device_model_parameters model_param = null;
        try {
            model_param = device_model_parameters_repo.searchByManufacturerAndModel(
                    InformData.getElementsByTagName("Manufacturer").item(0).getTextContent(),
                    InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
        } catch (Exception e) {
            model_param = null;
        }

        if (devicesRepo.findBySerialNumEquals(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent())
                .isEmpty()) {
            devices newDevice = new devices();

            newDevice.setserial_num(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setmanufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            newDevice.setoui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            newDevice.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            if (model_param != null) {
                try {
                    newDevice.setmac_address(object.get(model_param.getMac_address_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setudp_con_req_url(object.get(model_param.getUdp_con_req_url_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setmanagement_ip(object.get(model_param.getManagement_ip_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setpublic_ip(object.get(model_param.getPublic_ip_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setSecond_wan_mac(object.get(model_param.getSecond_wan_mac()).toString());
                    System.out.println("WAN2 Mac Added");
                } catch (Exception e) {
                }
                try {
                    newDevice.sethardware_ver(object.get(model_param.getHardware_ver_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setsoftware_ver(object.get(model_param.getSoftware_ver_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    newDevice.setcon_req_url(object.get(model_param.getCon_req_url_parameter()).toString());
                } catch (Exception e) {
                }
            }
            newDevice.setcwmp_cycle_end(false);
            devicesRepo.save(newDevice);
        } else {
            devices deviceUpdate = devicesRepo
                    .gEntityBySerialnum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setserial_num(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            deviceUpdate.setmanufacturer(InformData.getElementsByTagName("Manufacturer").item(0).getTextContent());
            deviceUpdate.setoui(InformData.getElementsByTagName("OUI").item(0).getTextContent());
            deviceUpdate.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());

            if (model_param != null) {
                try {
                    deviceUpdate.setmac_address(object.get(model_param.getMac_address_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setudp_con_req_url(object.get(model_param.getUdp_con_req_url_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setmanagement_ip(object.get(model_param.getManagement_ip_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setpublic_ip(object.get(model_param.getPublic_ip_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setSecond_wan_mac(object.get(model_param.getSecond_wan_mac()).toString());
                    System.out.println("WAN2 Mac Added");
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.sethardware_ver(object.get(model_param.getHardware_ver_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setsoftware_ver(object.get(model_param.getSoftware_ver_parameter()).toString());
                } catch (Exception e) {
                }
                try {
                    deviceUpdate.setcon_req_url(object.get(model_param.getCon_req_url_parameter()).toString());
                } catch (Exception e) {
                }
            }

            // Check on Rogue
            String DeviceSerialNum = InformData.getElementsByTagName("SerialNumber").item(0).getTextContent();
            String DeviceMacAddress = object.get(model_param.getMac_address_parameter()).toString();

            if (!device_front.findBySerialNumOnRogue(DeviceSerialNum).isEmpty()) {
                setDeviceInformInterval(DeviceSerialNum, 10);
                System.out.println(DeviceMacAddress);
                // TimeUnit.SECONDS.sleep(10);

                // device informedDevice = device_front.getBySerialNum(DeviceSerialNum);
                // informedDevice.setparent("Residential");
                // device_front.save(informedDevice);

                // setDeviceInformInterval(DeviceSerialNum, 1000);
                // requestAssociatedClientOfSerialNumber(DeviceSerialNum);
            }

            device dev = device_front
                    .getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            // device device =
            // device_front.setwan1_ip(object.get(model_param.getPublic_ip_parameter()).toString());
            deviceUpdate.setdevice_alias(dev.getdevice_name());
            List<group_ssid> ssid = ssidRepo.findByGroup(dev.getparent());
            StringBuilder strb = new StringBuilder();
            for (group_ssid group_ssid : ssid) {
                strb.append(group_ssid.getssid() + ",");
            }
            deviceUpdate.setssids(strb.toString());
            // deviceUpdate.set_udp_con_req_url(ClientHost+":"+ClientPort);

            deviceUpdate.setcwmp_cycle_end(false);
            devicesRepo.save(deviceUpdate);
        }
    }

    public void UpdateDevicesTable(String Payload) throws JSONException {
        SOAPBody InformData = null;
        Integer NumData = 0;
        try {
            InformData = getSoap.StringToSAOP(Payload).getSOAPBody();
        } catch (Exception e) {
            // TODO: handle exception
        }
        NumData = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().getLength();

        JSONObject object = new JSONObject();

        try {
            for (int i = 0; i < NumData; i++) {
                if (InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i).getChildNodes()
                        .getLength() == 5) {
                    Integer num_nodes = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                            .getChildNodes().getLength();
                    Integer num_child_nodes = 0;
                    String label = "";
                    String value = "";
                    for (int j = 0; j < num_nodes; j++) {
                        if (!InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                .getChildNodes().item(j).getTextContent().trim().isEmpty()) {
                            if (num_child_nodes == 0) {
                                label = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                        .getChildNodes().item(j).getTextContent();
                                num_child_nodes++;
                            } else {
                                value = InformData.getElementsByTagName("ParameterList").item(0).getChildNodes().item(i)
                                        .getChildNodes().item(j).getTextContent();
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

        // System.out.println("Json: " + object.toString());
        // System.out.println("Try JsonFind: "
        // +object.get("Device.DeviceInfo.SoftwareVersion").toString());
        device_model_parameters model_param = null;
        try {
            model_param = device_model_parameters_repo.searchByManufacturerAndModel(
                    InformData.getElementsByTagName("Manufacturer").item(0).getTextContent(),
                    InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
        } catch (Exception e) {
            model_param = null;
        }

        if (device_front.findBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent())
                .isEmpty()) {
            device unassigned_device = new device();
            unassigned_device
                    .setserial_number(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            if (model_param != null) {
                unassigned_device.setmac_address(object.get(model_param.getMac_address_parameter()).toString());
                try {
                    unassigned_device.setSecond_wan_mac(object.get(model_param.getSecond_wan_mac()).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            unassigned_device.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            unassigned_device.setstatus("online");
            unassigned_device.setparent("unassigned");

            // newDevice.set_date_modified(LocalTime.now().toString());
            // if(newDevice.getstatus().contains("syncing")==false){
            // newDevice.setstatus("online");
            // }
            unassigned_device.setactivated(false);
            device_front.save(unassigned_device);

        } else {
            device newDevice = device_front
                    .getBySerialNum(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            newDevice.setserial_number(InformData.getElementsByTagName("SerialNumber").item(0).getTextContent());
            if (model_param != null) {
                newDevice.setmac_address(object.get(model_param.getMac_address_parameter()).toString());
            }
            newDevice.setmodel(InformData.getElementsByTagName("ProductClass").item(0).getTextContent());
            // newDevice.set_date_modified(LocalTime.now().toString());
            if (newDevice.getstatus().contains("syncing") == false) {
                newDevice.setstatus("online");
            }
            newDevice.setactivated(true);
            device_front.save(newDevice);
        }
    }

    public void SaveWebCLIOutput(String WebCLIOutput, String CommandUsed, String SN) {
        // System.out.println("SavingCLI");
        // System.out.println(WebCLIOutput.length());
        // System.out.println(WebCLIOutput);
        new Thread(() -> {
            byte[] webcli_byte = WebCLIOutput.getBytes(Charsets.UTF_8);
            byte[] command_byte = CommandUsed.getBytes(Charsets.UTF_8);

            System.out.println("Saving CLI Response: " + new Timestamp(System.currentTimeMillis()));
            webcli_response_log webCLIlog = new webcli_response_log();
            webCLIlog.set_CommandOutput(webcli_byte);
            webCLIlog.set_device_sn(SN);
            webCLIlog.set_CommandUsed(command_byte);
            webCLIlog.set_time_saved(new Timestamp(System.currentTimeMillis()));
            webCliRepo.save(webCLIlog);
            System.out.println("Saved CLI Response: " + new Timestamp(System.currentTimeMillis()));

        }).start();

    }

    // ############################################################################
    // TestSendConnectionRequest
    // ############################################################################

    // @RequestMapping(value="/TestSendConnectionRequest/{SN}")
    public void SendUDPRequest(@PathVariable String SN) throws IOException {

        new Thread(() -> {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            Instant instant = Instant.now();
            long timeStampSeconds = instant.toEpochMilli();

            // String result = "";
            devices current_device = devicesRepo.gEntityBySerialnum(SN);
            String udp_url = current_device.getudp_con_req_url();
            String[] device_udp_url = udp_url.split(":");
            // System.out.println(udp_url);
            // System.out.println(device_udp_url);
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
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    udpclient.sendConnectionRequest(host, portnum, msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println(e);
                }
                udpclient.close();
            }

        }).start();
        // result = host+":"+portnum;
        // return result;
    }

    // ############################################################################
    // SaveTask
    // ############################################################################

    public void SaveTask(String SN, String Method, String Parameters, String Optional) {
        taskhandler newTasK = new taskhandler();
        newTasK.set_SN(SN);
        newTasK.set_method(Method);
        newTasK.set_parameters(Parameters);
        newTasK.set_optional(Optional);
        taskhandlerRepo.save(newTasK);

        try {
            devices current_device = devicesRepo.gEntityBySerialnum(SN);
            if (current_device.getcwmp_cycle_end()) {
                SendUDPRequest(SN);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            faults.append("SerialNumber Not Found");
            e.printStackTrace();
        }
    }

    public void UpdateDeviceStatus(String SerialNum, String Status) {
        device devicestat = device_front.getBySerialNum(SerialNum);
        // System.out.println(devicestat.getstatus());
        if (devicestat.getstatus().contains("syncing") == false) {
            devicestat.setstatus(Status);
        }
        device_front.save(devicestat);
    }

    private String Tr069ResponseHandler(String Method, String Parameters, String Option) {

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

    // '{"test":"1","test2":"2","test3":"3"}'
    /*
     * @RequestMapping(value="/AddSSID/{SerialNum}, {ObjectName}")
     * public String AddSSID(@RequestBody String SSIDSettings,@PathVariable String
     * SerialNum, @PathVariable String ObjectName) {
     * 
     * //System.out.println(SSIDSettings);
     * 
     * AddNewSSID(SSIDSettings, SerialNum, ObjectName);
     * 
     * return SSIDSettings;
     * }
     */

    // @Async("asyncExecutor")
    @RequestMapping(value = "/macclog/api/upload/stream/staLinkQuality_info")
    public ResponseEntity<String> staLinkQuality_info(@RequestBody(required = false) String Payload,
            @RequestParam("uploadTime") String uploadTime,
            @RequestParam("logType") String logType, @RequestParam("mac") String mac,
            @RequestParam("sn") String sn, @RequestParam("isCompressed") Integer isCompressed,
            @RequestParam("isEachCompressed") Integer isEachCompressed,
            @RequestParam("isEncrypted") Integer isEncrypted,
            HttpServletRequest request) {
        System.out.println(uploadTime);
        System.out.println(logType);
        System.out.println(mac);
        System.out.println(sn);
        System.out.println(isCompressed);
        System.out.println(isEachCompressed);
        System.out.println(isEncrypted);

        System.out.println(Payload);

        return ResponseEntity.status(HttpStatus.CONTINUE).contentType(MediaType.TEXT_XML).body(null);

    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/macclog/api/upload/jsondata/radio_info")
    public CompletableFuture<ResponseEntity<String>> DeviceRadio_info(@RequestBody(required = false) String Payload)
            throws JSONException {
        JSONObject json_payload = new JSONObject(Payload);

        String sn = json_payload.getString("sn").toString();
        System.out.println(sn);
        String uploadTime = json_payload.getString("uploadTime").toString();
        System.out.println(uploadTime);
        String data = json_payload.get("data").toString();
        System.out.println(data);

        SaveRadioInfo(sn, uploadTime, data);

        return CompletableFuture
                .completedFuture(ResponseEntity.status(HttpStatus.CONTINUE).contentType(MediaType.TEXT_XML).body(null));
    }

    // List<group_ssid> customers = new ArrayList<>();
    // ssidRepo.findAll().forEach(customers::add);
    @Async("asyncExecutor")
    @RequestMapping(value = "/getradioinfo")
    public CompletableFuture<List<radio_info>> getradioinfo(@RequestParam("sn") String sn) throws JSONException {
        System.out.println(sn);

        try {
            List<radio_info> data = radio_infoRepo.findBySerialNumEquals(sn);
            return CompletableFuture.completedFuture(data);
        } catch (Exception e) {
            // TODO: handle exception
            return CompletableFuture.completedFuture(null);
        }
    }

    public void SaveRadioInfo(String sn, String uploadTime, String data) throws JSONException {
        System.out.println(sn);
        System.out.println(data);

        JSONArray jsonarray_data = new JSONArray(data);
        System.out.println(jsonarray_data.length());
        int data_count = jsonarray_data.length();
        new Thread(() -> {
            for (int i = 0; i < data_count; i++) {
                JSONObject current_data = null;
                try {
                    current_data = jsonarray_data.getJSONObject(i);
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                List<radio_info> device;
                try {
                    device = radio_infoRepo.findBySerialNumEquals(sn);
                } catch (Exception e) {
                    // TODO: handle exception
                    device = null;
                }

                if (device.isEmpty()) {
                    try {
                        radio_info newRadioInfo = new radio_info();
                        newRadioInfo.setsn(sn);
                        newRadioInfo.setuploadTime(uploadTime);
                        newRadioInfo.setradioIndex(current_data.getString("radioIndex").toString());
                        newRadioInfo.setchannel(current_data.getString("channel").toString());
                        newRadioInfo.setgatherTime(current_data.getString("gatherTime").toString());
                        newRadioInfo.setutilization(current_data.getString("utilization").toString());
                        newRadioInfo.setpower(current_data.getString("power").toString());
                        newRadioInfo.setbandWidth(current_data.getString("bandWidth").toString());
                        radio_infoRepo.save(newRadioInfo);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                } else {
                    Boolean already_exist = false;
                    radio_info tobe_saved = null;
                    try {
                        for (radio_info radio_info : device) {

                            if (radio_info.getradioIndex()
                                    .contentEquals(current_data.getString("radioIndex").toString())) {
                                tobe_saved = radio_info;
                                already_exist = true;
                                break;
                            } else {
                                already_exist = false;
                            }
                        }
                        System.out.println(already_exist);
                        if (already_exist) {
                            tobe_saved.setsn(sn);
                            tobe_saved.setuploadTime(uploadTime);
                            tobe_saved.setradioIndex(current_data.getString("radioIndex").toString());
                            tobe_saved.setchannel(current_data.getString("channel").toString());
                            tobe_saved.setgatherTime(current_data.getString("gatherTime").toString());
                            tobe_saved.setutilization(current_data.getString("utilization").toString());
                            tobe_saved.setpower(current_data.getString("power").toString());
                            tobe_saved.setbandWidth(current_data.getString("bandWidth").toString());
                            radio_infoRepo.save(tobe_saved);
                        } else {
                            radio_info newRadioInfo = new radio_info();
                            newRadioInfo.setsn(sn);
                            newRadioInfo.setuploadTime(uploadTime);
                            newRadioInfo.setradioIndex(current_data.getString("radioIndex").toString());
                            newRadioInfo.setchannel(current_data.getString("channel").toString());
                            newRadioInfo.setgatherTime(current_data.getString("gatherTime").toString());
                            newRadioInfo.setutilization(current_data.getString("utilization").toString());
                            newRadioInfo.setpower(current_data.getString("power").toString());
                            newRadioInfo.setbandWidth(current_data.getString("bandWidth").toString());
                            radio_infoRepo.save(newRadioInfo);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    // @RequestMapping(value="/macclog/log/upload?file={filename:.+}&type={type}}")
    @Async("asyncExecutor")
    @RequestMapping(value = "/macclog/log/upload", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<String>> uploadfile(@RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) throws IllegalStateException, IOException {
        System.out.println(file.getOriginalFilename());
        String path = new java.io.File("home/apollo/uploads/").getAbsolutePath();
        System.out.println(path);
        try {
            if (!new java.io.File(path).exists()) {
                new java.io.File(path).mkdir();
            }

            String orgName = file.getOriginalFilename();
            String filePath = path + "/" + orgName;
            java.io.File dest = new java.io.File(filePath);
            file.transferTo(dest);
            process_file(filePath, path, orgName);
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.CONTINUE).contentType(MediaType.TEXT_XML).body(null));
        } catch (Exception e) {
            // TODO: handle exception
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_XML).body(null));
        }
    }

    public void process_file(String sourcefile, String dest_location, String orig_file_name)
            throws FileNotFoundException, IOException, InterruptedException, JSONException {
        Path source = Paths.get(sourcefile);

        new Thread(() -> {
            // ###############Extracting tar.gz###############
            String file_name = orig_file_name.replace(".tar.gz", "");
            System.out.println(file_name);
            Path target = Paths.get(dest_location + "/" + file_name);
            try {
                Files.createDirectories(target);
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command("sh", "-c", String.format("tar xfz %s -C %s", source, target));
                builder.directory(new java.io.File("/tmp"));
                Process process = builder.start();
                int exitCode = process.waitFor();
                System.out.println(exitCode);
            } catch (Exception e) {
                // TODO: handle exception
            }

            // ###############Deleting tar.gz###############
            java.io.File source_file = new java.io.File(sourcefile);
            boolean deleted = source_file.delete();
            System.out.println(deleted);

            // ###############Read files###############
            String[] sub_falder;
            java.io.File current_folder = new java.io.File(dest_location + "/" + file_name + "/all");
            sub_falder = current_folder.list();

            String[] filenameArray = file_name.split("-", -1);
            String serial_num = filenameArray[6];
            System.out.println(filenameArray[6]);
            for (String string : sub_falder) {
                java.io.File subfolder = new java.io.File(dest_location + "/" + file_name + "/all/" + string);
                String[] files = subfolder.list();
                String filename = files[0];
                System.out.println(filename);
                // SaveData
                StringBuilder data = new StringBuilder();

                try {
                    String filelocation = new java.io.File(
                            dest_location + "/" + file_name + "/all/" + string + "/" + filename).getAbsolutePath();
                    ProcessBuilder procBuilder = new ProcessBuilder();
                    procBuilder.command("chmod", "777", filelocation);
                    procBuilder.directory(new java.io.File("/tmp"));
                    Process proc = procBuilder.start();
                    int exitCode = proc.waitFor();
                    System.out.println(exitCode);

                    java.io.File filedata = new java.io.File(filelocation);
                    Scanner reader = new Scanner(filedata);
                    while (reader.hasNextLine()) {
                        data.append(reader.nextLine());
                    }
                    reader.close();
                } catch (Exception e) {
                    System.out.println("Can't Read file: " + e);
                    // TODO: handle exception
                }
                System.out.println("data:" + data.toString());
                JSONObject jsondata = null;
                try {
                    jsondata = new JSONObject(data.toString());
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (string.contains("system")) {
                    try {
                        devices current_device = devicesRepo.gEntityBySerialnum(serial_num);
                        current_device.setcpu_usage(jsondata.getString("cpu_rate"));
                        current_device.setmemory_usage(jsondata.getString("memory_rate"));
                        devicesRepo.save(current_device);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                if (string.contains("offdrop")) {
                    try {
                        List<device_logs> logs = device_logRepo.findBySerialNumEquals(serial_num);
                        if (logs.isEmpty()) {
                            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            device_logs dev_log = new device_logs();
                            dev_log.setserial_num(serial_num);
                            dev_log.setupdate_time(dt_date.format(now));
                            dev_log.setontime(jsondata.getString("on_time"));
                            dev_log.setofftime(jsondata.getString("off_time"));
                            dev_log.setreason(jsondata.getString("off_reason"));

                            if (jsondata.getString("off_reason").contains("reload")) {
                                dev_log.settype("reload");
                            }
                            if (jsondata.getString("off_reason").contains("online")) {
                                dev_log.settype("online");
                            }
                            if (jsondata.getString("off_reason").contains("offline")) {
                                dev_log.settype("offline");
                            }
                            if (jsondata.getString("off_reason").contains("restart")) {
                                dev_log.settype("restart");
                            }
                            System.out.println(dev_log);
                            device_logRepo.save(dev_log);
                        }
                        if (!logs.get(0).getreason().contains(jsondata.getString("off_reason"))) {
                            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            device_logs dev_log = new device_logs();
                            dev_log.setserial_num(serial_num);
                            dev_log.setupdate_time(dt_date.format(now));
                            dev_log.setontime(jsondata.getString("on_time"));
                            dev_log.setofftime(jsondata.getString("off_time"));
                            dev_log.setreason(jsondata.getString("off_reason"));

                            if (jsondata.getString("off_reason").contains("reload")) {
                                dev_log.settype("reload");
                            }
                            if (jsondata.getString("off_reason").contains("online")) {
                                dev_log.settype("online");
                            }
                            if (jsondata.getString("off_reason").contains("offline")) {
                                dev_log.settype("offline");
                            }
                            if (jsondata.getString("off_reason").contains("restart")) {
                                dev_log.settype("restart");
                            }
                            System.out.println(dev_log);
                            device_logRepo.save(dev_log);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                if (string.contains("flow")) {
                    try {
                        String new_data = data.toString().replace("}{", "},{");
                        new_data = "[" + new_data + "]";
                        System.out.println(new_data);
                        JSONArray data_arr = new JSONArray(new_data);
                        int rx = 0;
                        int tx = 0;
                        for (int i = 0; i < data_arr.length(); i++) {
                            JSONObject data_obj = data_arr.getJSONObject(i);
                            if (data_obj.getString("intf_name").contains("Dot11radio")) {
                                rx = rx + data_obj.getInt("rx_bytes");
                                tx = tx + data_obj.getInt("tx_bytes");
                            }
                        }
                        device_traffic_24h flow = new device_traffic_24h();
                        flow.setserial_num(serial_num);
                        DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        DateTimeFormatter dt_time = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        flow.setdate(dt_date.format(now));
                        flow.settime(dt_time.format(now));
                        flow.setrx((rx / 1024) / 1024);
                        flow.settx((tx / 1024) / 1024);
                        dev_traff_24Repo.save(flow);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
            try {
                java.io.File delete_log = new java.io.File(dest_location + "/" + file_name);
                ProcessBuilder procBuilder = new ProcessBuilder();
                procBuilder.command("rm", "-r", delete_log.getAbsolutePath());
                procBuilder.directory(new java.io.File("/tmp"));
                Process delete_process = procBuilder.start();
                int exitCode = delete_process.waitFor();

                System.out.println(delete_log.getAbsolutePath() + ": deleted:" + exitCode);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }).start();

        /*
         * try (GZIPInputStream gis = new GZIPInputStream(new
         * FileInputStream(source.toFile())); FileOutputStream fos = new
         * FileOutputStream(target.toFile()))
         * {
         * // copy GZIPInputStream to FileOutputStream
         * byte[] buffer = new byte[2048];
         * int len;
         * while ((len = gis.read(buffer)) > 0) {
         * fos.write(buffer, 0, len);
         * }
         * }
         */
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevicelogs")
    public CompletableFuture<List<device_logs>> get_dev_log(@RequestParam("sn") String sn) {
        List<device_logs> logs = device_logRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(logs);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevicedetails")
    public CompletableFuture<devices> get_dev_details(@RequestParam("sn") String sn) {
        devices device_details = devicesRepo.gEntityBySerialnum(sn);
        return CompletableFuture.completedFuture(device_details);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevice24htraffic")
    public CompletableFuture<List<device_traffic_24h>> get_dev_traffic(@RequestParam("sn") String sn) {
        List<device_traffic_24h> device_traffic_24hs = dev_traff_24Repo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(device_traffic_24hs);
    }

    @Scheduled(cron = "00 23 * * * ?")
    private void save_daily_traffic() {
        new Thread(() -> {
            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String current_date = dt_date.format(now);
            Iterable<device> divices = device_front.findAll();
            for (device device : divices) {
                int rx = 0;
                int tx = 0;
                List<device_traffic_24h> dev_traffic_data = dev_traff_24Repo
                        .findBySerialNumEquals(device.getserial_number());
                for (device_traffic_24h device_traffic : dev_traffic_data) {
                    if (device_traffic.getdate().contains(current_date)) {
                        rx = rx + device_traffic.getrx();
                        tx = tx + device_traffic.gettx();
                        dev_traff_24Repo.delete(device_traffic);
                    }
                }
                device_traffic_daily traffic_daily = new device_traffic_daily();
                traffic_daily.setserial_num(device.getserial_number());
                traffic_daily.setdate(current_date);
                traffic_daily.setrx((rx / 1024) / 1024);
                traffic_daily.settx((tx / 1024) / 1024);
                dev_traff_dailyRepo.save(traffic_daily);
            }
        }).start();
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "getdevice-daily-traffic")
    public CompletableFuture<List<device_traffic_daily>> get_daily_traffic(@RequestParam("sn") String sn,
            @RequestParam("days") Integer days) {
        DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        List<device_traffic_daily> traffic = dev_traff_dailyRepo.findBySerialNumEquals(sn);
        List<device_traffic_daily> result = null;
        /*
         * for (int i = 0; i < days; i++) {
         * if(traffic.size()<days){
         * return traffic;
         * }
         * LocalDateTime date = now.minusDays((days-1)-i);
         * for (device_traffic_daily device_traffic_daily : traffic) {
         * if(device_traffic_daily.getdate().contains(dt_date.format(date))){
         * result.add(device_traffic_daily);
         * }
         * }
         * }
         */
        return CompletableFuture.completedFuture(traffic);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/setdevicedailytraffic")
    public CompletableFuture<device_traffic_daily> set_daily_traffic(@RequestParam("sn") String sn,
            @RequestParam("date") String date, @RequestParam("rx") Integer rx, @RequestParam("tx") Integer tx) {
        device_traffic_daily traffic = new device_traffic_daily();
        traffic.setserial_num(sn);
        traffic.setdate(date);
        traffic.setrx(rx);
        traffic.settx(tx);
        dev_traff_dailyRepo.save(traffic);
        return CompletableFuture.completedFuture(traffic);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getclient_list")
    public CompletableFuture<List<client_list>> getclient_list(@RequestParam("sn") String sn) {
        List<client_list> client_list = client_listRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(client_list);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/setclient_list")
    public CompletableFuture<client_list> setclient_list(
            @RequestParam("sn") String sn,
            @RequestParam("ip") String ip,
            @RequestParam("macc") String macc,
            @RequestParam("ssid") String ssid,
            @RequestParam("rssi") String rssi,
            @RequestParam("band") String band,
            @RequestParam("traffic") String traffic,
            @RequestParam("os") String os,
            @RequestParam("manufacturer") String manufacturer,
            @RequestParam("up") String up,
            @RequestParam("down") String down) {
        client_list client = new client_list();
        client.setserial_num(sn);
        client.setip(ip);
        client.setmacc(macc);
        client.setssid(ssid);
        client.setrssi(rssi);
        client.setband(band);
        client.settraffic(traffic);
        client.setos(os);
        client.setmanufacturer(manufacturer);
        client.setup(up);
        client.setdown(down);
        client_listRepo.save(client);

        return CompletableFuture.completedFuture(client);
    }

    /*
     * @Scheduled(fixedRate = 60000)
     * private void request_client_list(){
     * Iterable<device> devices = device_front.findAll();
     * for (device device : devices) {
     * String Head = "web_cli \"exec\" \"0\" \"0\" \"0\" \"\" \"\" ";
     * String sn = device.getserial_number();
     * System.out.println("ClientReq: " + sn);
     * SaveTask(sn, "WebCli", "{,\"Command\":"+Head+'"'+"show dot1 associations
     * debug all-client"+'"'+",}", "shell");
     * SaveTask(sn, "WebCli", "{,\"Command\":"+Head+'"'+"show stamg sta all ip
     * ipv4"+'"'+",}", "shell");
     * }
     * }
     */

    @Async("asyncExecutor")
    @RequestMapping(value = "/ExecuteGroupCommand/{SerialNum}, {ID}")
    public CompletableFuture<String> ExecuteGroupCommand(@PathVariable String SerialNum, @PathVariable String ID) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        Long id = Long.parseLong(ID);
        group_command current_command = GroupCommandRepo.getByID(id);
        String DeviceGroup = current_command.getparent();

        device DevicesInGroup = device_front.getBySerialNum(SerialNum);

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

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            DevicesInGroup.setdate_modified(dtf.format(now));
            device_front.save(DevicesInGroup);

            SaveTask(DevicesInGroup.getserial_number(), "Command", sb.toString(), "config");
        } else {
            String deviceModel = DevicesInGroup.getmodel();
            if (current_command.getmodel().contains(deviceModel)) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                DevicesInGroup.setdate_modified(dtf.format(now));
                device_front.save(DevicesInGroup);

                SaveTask(DevicesInGroup.getserial_number(), "Command", sb.toString(), "config");
            }
        }
        return CompletableFuture.completedFuture("ExecuteCommand");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddSSID/{SerialNum}, {ID}")
    public CompletableFuture<String> AddSSID(@PathVariable String SerialNum, @PathVariable String ID) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        device current_device = device_front.getBySerialNum(SerialNum);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        current_device.setdate_modified(dtf.format(now));
        device_front.save(current_device);

        Long id = Long.parseLong(ID);
        group_ssid ssid_to_add = ssidRepo.getByID(id);
        Integer wlan_id = ssid_to_add.getwlan_id();

        StringBuilder SSIDSettings = new StringBuilder();
        String encryptionMode = null;
        String encrypModetoConvert = ssid_to_add.getencryption_mode();

        if (encrypModetoConvert.contains("Open")) {
            encryptionMode = "None";
        }
        if (encrypModetoConvert.contains("WPA-PSK")) {
            encryptionMode = "WPA-Personal";
        }
        if (encrypModetoConvert.contains("WPA2-PSK")) {
            encryptionMode = "WPA2-Personal";
        }

        SSIDSettings.append("{,Device.WiFi.SSID." + wlan_id + ".SSID:" + ssid_to_add.getssid());
        SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".LowerLayers:1&2");
        if (ssid_to_add.getforward_mode().contains("Nat")) {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:true");
        } else {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:false");
        }
        SSIDSettings.append(
                ",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_FowardType:" + ssid_to_add.getforward_mode());

        if (ssid_to_add.getforward_mode().contains("Bridge")) {
            SSIDSettings
                    .append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_VLANID:" + ssid_to_add.getvlan_id());
        }
        SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.ModeEnabled:" + encryptionMode);
        if (encryptionMode.contains("None") == false) {
            SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.KeyPassphrase:"
                    + ssid_to_add.getpassphrase() + ",}");
        } else {
            SSIDSettings.append(",}");
        }
        // System.out.println("SSID-Settings: " + SSIDSettings.toString());
        AddNewSSID(SSIDSettings.toString(), SerialNum, wlan_id.toString());

        if (ssid_to_add.getauth()) {
            StringBuilder AuthSettings = new StringBuilder();
            AuthSettings.append("{,WiFiDog");
            AuthSettings.append("," + ssid_to_add.getportal_ip());
            AuthSettings.append("," + ssid_to_add.getportal_url());
            AuthSettings.append(",js");
            AuthSettings.append("," + ssid_to_add.getgateway_id());
            AuthSettings.append(",true");
            AuthSettings.append("," + ssid_to_add.getseamless() + ",}");

            AddNewAuth(AuthSettings.toString(), SerialNum, wlan_id.toString());
        }

        return CompletableFuture.completedFuture("Adding SSID");
    }

    public void AddNewSSID(String SSIDSettings, String SerialNum, String ObjectName) {
        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID." + ObjectName + ".X_WWW-RUIJIE-COM-CN_ExistStatus",
                "None");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.SSID.[" + ObjectName + "].", "AddSSID");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.AccessPoint.[" + ObjectName + "].", "None");
        SaveTask(SerialNum, "SetParameterValues", SSIDSettings, "None");
        SaveTask(SerialNum, "Save", "None", "None");
    }

    /*
     * @RequestMapping(value="/AddAuth/{SerialNum}, {ObjectName}")
     * public String AddAuth(@RequestBody String SSIDSettings, @PathVariable String
     * SerialNum, @PathVariable String ObjectName) {
     * AddNewAuth(SSIDSettings, SerialNum, ObjectName);
     * return "TaskAdded";
     * }
     */

    public void AddNewAuth(String SSIDSettings, String SerialNum, String ObjectName) {
        String[] ProcessedString = SSIDSettings.split(",", -1);

        StringBuilder authSetting = new StringBuilder();
        authSetting.append("'{,Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_ModeEnabled:" + ProcessedString[1] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalIP:" + ProcessedString[2] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_PortalUrl:http1//" + ProcessedString[3] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayIP:1.2.3.4,");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_RedirectMode:" + ProcessedString[4] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_GatewayID:" + ProcessedString[5] + ",");
        authSetting.append("Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication." + ObjectName
                + ".X_WWW-RUIJIE-COM-CN_WiFiDog.X_WWW-RUIJIE-COM-CN_OffDetectEnable:" + ProcessedString[6] + ",");
        authSetting
                .append("Device.WiFi.X_WWW-RUIJIE-COM-CN_AuthenticationGlobal.X_WWW-RUIJIE-COM-CN_StaPerceptionEnable:"
                        + ProcessedString[7] + ",}");

        SaveTask(SerialNum, "GetParameterValues", "Device.WiFi.SSID." + ObjectName + ".X_WWW-RUIJIE-COM-CN_ExistStatus",
                "None");
        SaveTask(SerialNum, "Command", "'{,Command:dot11 wlan " + ObjectName + ",Command:no band-select enable,}'",
                "AddAuth");
        SaveTask(SerialNum, "AddObject", "Device.WiFi.X_WWW-RUIJIE-COM-CN_Authentication.[" + ObjectName + "].",
                "None");
        SaveTask(SerialNum, "SetParameterValues", authSetting.toString(), "None");
        SaveTask(SerialNum, "Save", "None", "None");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddObject/{SerialNum}, {ObjectName}")
    public CompletableFuture<String> AddNewObject(@PathVariable String SerialNum, @PathVariable String ObjectName) {
        SaveTask(SerialNum, "AddObject", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddObjectXMPP/{SerialNum}")
    public CompletableFuture<String> AddObjectXMPP(@PathVariable String SerialNum) {
        SaveTask(SerialNum, "AddObject", "Device.XMPP.Connection.", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetParameterValues/{SerialNum}, {ObjectName}")
    public CompletableFuture<String> GetParameterValues(@PathVariable String SerialNum,
            @PathVariable String ObjectName) {
        SaveTask(SerialNum, "GetParameterValues", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/SetParameterValues/{SerialNum}")
    public CompletableFuture<String> SetParameterValues(@RequestBody String ParameterList,
            @PathVariable String SerialNum) {
        SaveTask(SerialNum, "SetParameterValues", ParameterList, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetRPCMethods/{SerialNum}")
    public CompletableFuture<String> GetRPCMethods(@PathVariable String SerialNum) {
        SaveTask(SerialNum, "GetRPCMethods", "None", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/DeleteObject/{SerialNum}, {ObjectName}")
    public CompletableFuture<String> DeleteObject(@PathVariable String SerialNum, @PathVariable String ObjectName) {
        SaveTask(SerialNum, "DeleteObject", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/MoveDeviceGroup/{SerialNum}")
    public CompletableFuture<String> MoveDeviceGroup(@PathVariable String SerialNum) {
        device device_to_bootstrap = device_front.getBySerialNum(SerialNum);
        if (device_to_bootstrap.getstatus().contains("syncing") == false) {
            device_to_bootstrap.setstatus("syncing");
            device_front.save(device_to_bootstrap);
            Bootstraping(SerialNum);
        }
        return CompletableFuture.completedFuture("MoveDeviceGroup Initiated");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "   /{SerialNum}")
    public CompletableFuture<String> FactoryReset(@PathVariable String SerialNum) {
        SaveTask(SerialNum, "FactoryReset", "None", "None");
        return CompletableFuture.completedFuture("Reseting Device");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/Command/{SerialNum}")
    public CompletableFuture<String> Command(@RequestBody String ObjectName, @PathVariable String SerialNum) {

        SaveTask(SerialNum, "Command", ObjectName, "config");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetRogueDevices")
    public CompletableFuture<List<device>> GetRougeDevices() {
        List<device> roguedevices = device_front.findByGroup("unassigned");
        /*
         * StringBuilder sb = new StringBuilder();
         * sb.append("{,");
         * for (device device : roguedevices) {
         * sb.append(device.getserial_number()+":"+device.getId()+",");
         * }
         * sb.append("}");
         */
        return CompletableFuture.completedFuture(roguedevices);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/CheckParentGroup")
    public CompletableFuture<String> CheckParentGroup(@RequestBody String parent) {
        List<groups> groups = group_repo.findByParent(parent);
        if (groups.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("{,");
            for (groups groups2 : groups) {
                sb.append(groups2.getgroup_name() + ",");
            }
            sb.append("}");
            return CompletableFuture.completedFuture(sb.toString());
        } else {
            return CompletableFuture.completedFuture("parent not exist");
        }
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/WebCli/ {SerialNum}")
    public CompletableFuture<DeferredResult<ResponseEntity<String>>> WebCli(@RequestBody String Modes,
            @PathVariable String SerialNum, HttpServletRequest request)
            throws JSONException {
        String[] modez = Modes.split(",", -1);
        String ObjectName = modez[7];
        System.out.println("COmmand:############" + Modes);
        System.out.println("COmmand:############" + ObjectName);
        // System.out.println("Modez: "+ Modes);
        AddWebCLiTask(Modes, SerialNum, ObjectName);
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        new Thread(() -> {
            String body = "";
            while (true) {
                body = GetCLIOutput(SerialNum, ObjectName);
                if (body != null) {
                    break;
                }

            }
            // System.out.println("test--------" + body);
            result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
        }, "MyThread for ").start();
        return CompletableFuture.completedFuture(result);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/CliAutoComplete/ {SerialNum}")
    public CompletableFuture<DeferredResult<ResponseEntity<String>>> CliAutoComplete(@RequestBody String Modes,
            @PathVariable String SerialNum, HttpServletRequest request)
            throws JSONException {
        // System.out.println("Modez: "+ Modes);

        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        new Thread(() -> {
            String body = "";
            String[] modez = Modes.split(",", -1);
            String ObjectName = modez[7];
            devices current_device = devicesRepo.gEntityBySerialnum(SerialNum);
            String deviceModel = current_device.getmodel();
            List<auto_complete> suggestion_lists = auto_completeRepo.findByDeviceModel(deviceModel);
            boolean found = false;
            if (!suggestion_lists.isEmpty()) {

                for (auto_complete auto_complete : suggestion_lists) {
                    System.out.println("from db" + auto_complete.get_command());
                    System.out.println("from ObjName" + ObjectName);
                    if (ObjectName.contains(auto_complete.get_command())) {
                        body = new String(auto_complete.get_suggestion_list(), Charsets.UTF_8);
                        found = true;
                        result.setResult(
                                ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                    }
                }
                System.out.println("found " + found);
                if (!found) {
                    try {
                        AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    while (true) {
                        body = GetCLIOutput(SerialNum, ObjectName);
                        if (body != null) {
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
                        // TODO: handle exception
                    }

                    // System.out.println("test--------" + body);
                    result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }
            } else {
                if (!found) {
                    try {
                        AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    while (true) {
                        body = GetCLIOutput(SerialNum, ObjectName);
                        if (body != null) {
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
                        // TODO: handle exception
                    }

                    // System.out.println("test--------" + body);
                    result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }

            }

        }, "MyThread for ").start();
        return CompletableFuture.completedFuture(result);
    }

    private String GetCLIOutput(String SerialNum, String ObjectName) {
        // String Byte2String = new String(webcli_byte, Charsets.UTF_8);
        String Outputbody = null;
        String CommandUsed = null;
        List<webcli_response_log> cliOutput = webCliRepo.findBySerialNumEquals(SerialNum);
        if (cliOutput != null) {
            Integer NumOutput = cliOutput.size();
            for (int i = 0; i < NumOutput; i++) {
                webcli_response_log currentCheck = cliOutput.get(i);
                CommandUsed = new String(currentCheck.get_CommandUsed(), Charsets.UTF_8);
                if (CommandUsed.contains("\"" + ObjectName + "\"")) {
                    Outputbody = new String(currentCheck.get_CommandOutput(), Charsets.UTF_8);
                    webCliRepo.delete(webCliRepo.getByID(currentCheck.get_Id()));
                    System.out.println("OutputBody: " + new Timestamp(System.currentTimeMillis()));
                    return Outputbody;
                }
            }
        }
        return Outputbody;
    }

    private void AddWebCLiTask(String Modes, String SerialNum, String ObjectName) throws JSONException {
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
            // webCliRepo.delete(webCliRepo.getByID(ResponseLog.get(ResponseLog.size()-1).get_Id()));
            // System.out.println("HeadCLI: "+Head.toString());
            // System.out.println("WebCLI:
            // "+"{,\"Command\":"+Head.toString()+'"'+ObjectName+'"'+",}");
            SaveTask(SerialNum, "WebCli", "{,\"Command\":" + Head.toString() + '"' + ObjectName + '"' + ",}", "shell");
        }
        System.out.println("Commited CLI Request: " + new Timestamp(System.currentTimeMillis()));
    }

    // ################################################################################################
    // Backend MVC Endpoints
    // ##########################################################################
    // ################################################################################################

    @Async("asyncExecutor")
    @GetMapping("/getssid")
    public CompletableFuture<Iterable<group_ssid>> getAllCustomers() {

        List<group_ssid> customers = new ArrayList<>();
        ssidRepo.findAll().forEach(customers::add);
        // System.out.println("customers: " + customers);
        return CompletableFuture.completedFuture(customers);
    }

    @Async("asyncExecutor")
    @GetMapping("/getgroup")
    public CompletableFuture<List<groups>> getAllGroups() {

        List<groups> customers = new ArrayList<>();
        group_repo.findAll().forEach(customers::add);
        return CompletableFuture.completedFuture(customers);
    }

    @Async("asyncExecutor")
    @GetMapping("/getdevice")
    public CompletableFuture<List<device>> getAllDevice() {
        List<device> Device = new ArrayList<>();
        device_front.findAll().forEach(Device::add);

        return CompletableFuture.completedFuture(Device);
    }

    @Async("asyncExecutor")
    @GetMapping("/getcommand")
    public CompletableFuture<List<group_command>> getAllCommands() {

        List<group_command> commands = new ArrayList<>();
        // group_commandRepo.findAll().forEach(commands::add);
        GroupCommandRepo.findAll().forEach(commands::add);
        return CompletableFuture.completedFuture(commands);
    }

    @Async("asyncExecutor")
    @PostMapping("/adddevice")
    public CompletableFuture<device> postGroup(@RequestBody device DEVICE) {

        device Device = device_front
                .save(new device(DEVICE.getdevice_name(), DEVICE.getmac_address(), DEVICE.getserial_number(),
                        DEVICE.getlocation(), DEVICE.getparent(), DEVICE.getdate_created(), DEVICE.getdate_modified(),
                        DEVICE.getdate_offline(), DEVICE.getstatus(), DEVICE.getmodel(), DEVICE.getdevice_type()));
        return CompletableFuture.completedFuture(Device);
    }

    @Async("asyncExecutor")
    @PostMapping("/addgroup")
    public CompletableFuture<groups> postGroup(@RequestBody groups GROUP) {

        groups GroupS = group_repo
                .save(new groups(GROUP.getgroup_name(), GROUP.getlocation(), GROUP.getparent(), GROUP.getchild(),
                        GROUP.getdate_created(), GROUP.getdate_modified()));
        return CompletableFuture.completedFuture(GroupS);

    }

    @Async("asyncExecutor")
    @PostMapping("/addcommand")
    public CompletableFuture<group_command> postCommand(@RequestBody group_command COMMAND) {

        group_command Commands = GroupCommandRepo.save(new group_command(COMMAND.getmodel(), COMMAND.getdescription(),
                COMMAND.getparent(), COMMAND.getcommand()));
        return CompletableFuture.completedFuture(Commands);

    }

    @Async("asyncExecutor")
    @PostMapping("/addssid")
    public CompletableFuture<group_ssid> postSSID(@RequestBody group_ssid ssID) {
        group_ssid _ssid = ssidRepo.save(new group_ssid(ssID.getssid(),
                ssID.getforward_mode(),
                ssID.getvlan_id(),
                ssID.getwlan_id(),
                ssID.getencryption_mode(),
                ssID.getpassphrase(),
                ssID.getlimitless(),
                ssID.getuplink(),
                ssID.getdownlink(),
                ssID.getauth(),
                ssID.getportal_url(),
                ssID.getportal_ip(),
                ssID.getparent(),
                ssID.getgateway_id(),
                ssID.getseamless()));
        return CompletableFuture.completedFuture(_ssid);
    }

    @Async("asyncExecutor")
    @PutMapping("/updatessid/{id}")
    public CompletableFuture<ResponseEntity<group_ssid>> updateCustomer(@PathVariable("id") long id,
            @RequestBody group_ssid ssID) {
        // System.out.println("Update Customer with ID = " + id + "...");

        Optional<group_ssid> customerData = ssidRepo.findById(id);

        if (customerData.isPresent()) {
            group_ssid _ssid = customerData.get();
            _ssid.setssid(ssID.getssid());
            _ssid.setforward_mode(ssID.getforward_mode());
            _ssid.setvlan_id(ssID.getvlan_id());
            _ssid.setwlan_id(ssID.getwlan_id());
            _ssid.setencryption_mode(ssID.getencryption_mode());
            _ssid.setpassphrase(ssID.getpassphrase());
            _ssid.setlimitless(ssID.getlimitless());
            _ssid.setuplink(ssID.getuplink());
            _ssid.setdownlink(ssID.getdownlink());
            _ssid.setauth(ssID.getauth());
            _ssid.setportal_url(ssID.getportal_url());
            _ssid.setportal_ip(ssID.getportal_ip());
            _ssid.setparent(ssID.getparent());
            _ssid.setgateway_id(ssID.getgateway_id());
            _ssid.setseamless(ssID.getseamless());
            return CompletableFuture.completedFuture(new ResponseEntity<>(ssidRepo.save(_ssid), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updatecommand/{id}")
    public CompletableFuture<ResponseEntity<group_command>> updateCommand(@PathVariable("id") long id,
            @RequestBody group_command Command) {
        // System.out.println("Update Customer with ID = " + id + "...");

        Optional<group_command> commandData = GroupCommandRepo.findById(id);

        if (commandData.isPresent()) {
            group_command _command = commandData.get();
            _command.setmodel(Command.getmodel());
            _command.setparent(Command.getparent());
            _command.setdescription(Command.getdescription());
            _command.setcommand(Command.getcommand());
            return CompletableFuture
                    .completedFuture(new ResponseEntity<>(GroupCommandRepo.save(_command), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updategroup/{id}")
    public CompletableFuture<ResponseEntity<groups>> updateGroup(@PathVariable("id") long id,
            @RequestBody groups Group) {
        // System.out.println("Update Customer with ID = " + id + "...");

        Optional<groups> groupData = group_repo.findById(id);

        if (groupData.isPresent()) {
            groups _groups = groupData.get();
            _groups.setgroup_name(Group.getgroup_name());
            _groups.setparent(Group.getparent());
            _groups.setlocation(Group.getlocation());
            _groups.setchild(Group.getchild());
            _groups.setdate_created(Group.getdate_created());
            _groups.setdate_modified(Group.getdate_modified());
            return CompletableFuture.completedFuture(new ResponseEntity<>(group_repo.save(_groups), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updatedevice/{id}")
    public CompletableFuture<ResponseEntity<device>> updateDevice(@PathVariable("id") long id,
            @RequestBody device Device) {
        // System.out.println("Update Customer with ID = " + id + "...");

        Optional<device> deviceData = device_front.findById(id);

        if (deviceData.isPresent()) {
            device _device = deviceData.get();
            _device.setdevice_name(Device.getdevice_name());
            _device.setparent(Device.getparent());
            _device.setlocation(Device.getlocation());
            _device.setmac_address(Device.getmac_address());
            _device.setserial_number(Device.getserial_number());
            _device.setdate_created(Device.getdate_created());
            _device.setdate_modified(Device.getdate_modified());
            _device.setdevice_type(Device.getdevice_type());
            return CompletableFuture.completedFuture(new ResponseEntity<>(device_front.save(_device), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletessid/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteCustomer(@PathVariable("id") long id) {
        // System.out.println("Delete Customer with ID = " + id + "...");

        ssidRepo.deleteById(id);

        return CompletableFuture.completedFuture(new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletecommand/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteCommand(@PathVariable("id") long id) {
        // System.out.println("Delete Customer with ID = " + id + "...");

        GroupCommandRepo.deleteById(id);

        return CompletableFuture.completedFuture(new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletegroup/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteGroup(@PathVariable("id") long id) {
        // System.out.println("Delete Customer with ID = " + id + "...");

        group_repo.deleteById(id);

        return CompletableFuture.completedFuture(new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletedevice/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteDevice(@PathVariable("id") long id) {
        // System.out.println("Delete Customer with ID = " + id + "...");

        device_front.deleteById(id);

        return CompletableFuture.completedFuture(new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @PostMapping("/addDeviceModel")
    public CompletableFuture<ResponseEntity<Object>> addDeviceModel(@RequestBody Map<String, String> params) {

        try {

            if (device_model_parameters_repo.searchByManufacturerAndModel(params.get("manufacturer").toString(),
                    params.get("model").toString()) == null) {
                device_model_parameters new_model = new device_model_parameters();
                new_model.setModel(params.get("model").toString());
                new_model.setManufacturer(params.get("manufacturer").toString());

                if (!params.get("mac_address").isEmpty() || (params.get("mac_address") != null)) {
                    new_model.setMac_address_parameter(params.get("mac_address").toString());
                }
                if (!params.get("udp_con_req_url").isEmpty() || (params.get("udp_con_req_url") != null)) {
                    new_model.setUdp_con_req_url_parameter(params.get("udp_con_req_url").toString());
                }
                if (!params.get("con_req_url").isEmpty() || (params.get("con_req_url") != null)) {
                    new_model.setCon_req_url_parameter(params.get("con_req_url").toString());
                }
                if (!params.get("management_ip").isEmpty() || (params.get("management_ip") != null)) {
                    new_model.setManagement_ip_parameter(params.get("management_ip").toString());
                }
                if (!params.get("public_ip").isEmpty() || (params.get("public_ip") != null)) {
                    new_model.setPublic_ip_parameter(params.get("public_ip").toString());
                }
                if (!params.get("hardware_ver").isEmpty() || (params.get("hardware_ver") != null)) {
                    new_model.setHardware_ver_parameter(params.get("hardware_ver").toString());
                }
                if (!params.get("software_ver").isEmpty() || (params.get("software_ver") != null)) {
                    new_model.setSoftware_ver_parameter(params.get("software_ver").toString());
                }

                device_model_parameters_repo.save(new_model);
                return CompletableFuture.completedFuture(new ResponseEntity<>(new_model, HttpStatus.OK));
            } else {
                device_model_parameters new_model = device_model_parameters_repo.searchByManufacturerAndModel(
                        params.get("manufacturer").toString(), params.get("model").toString());
                new_model.setModel(params.get("model").toString());
                new_model.setManufacturer(params.get("manufacturer").toString());

                if (!params.get("mac_address").isEmpty() || (params.get("mac_address") != null)) {
                    new_model.setMac_address_parameter(params.get("mac_address").toString());
                }
                if (!params.get("udp_con_req_url").isEmpty() || (params.get("udp_con_req_url") != null)) {
                    new_model.setUdp_con_req_url_parameter(params.get("udp_con_req_url").toString());
                }
                if (!params.get("con_req_url").isEmpty() || (params.get("con_req_url") != null)) {
                    new_model.setCon_req_url_parameter(params.get("con_req_url").toString());
                }
                if (!params.get("management_ip").isEmpty() || (params.get("management_ip") != null)) {
                    new_model.setManagement_ip_parameter(params.get("management_ip").toString());
                }
                if (!params.get("public_ip").isEmpty() || (params.get("public_ip") != null)) {
                    new_model.setPublic_ip_parameter(params.get("public_ip").toString());
                }
                if (!params.get("hardware_ver").isEmpty() || (params.get("hardware_ver") != null)) {
                    new_model.setHardware_ver_parameter(params.get("hardware_ver").toString());
                }
                if (!params.get("software_ver").isEmpty() || (params.get("software_ver") != null)) {
                    new_model.setSoftware_ver_parameter(params.get("software_ver").toString());
                }

                device_model_parameters_repo.save(new_model);
                return CompletableFuture.completedFuture(new ResponseEntity<>(new_model, HttpStatus.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture
                    .completedFuture(new ResponseEntity<>("Error adding model", HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }

    // ---------------- FUNCTIONS ---------------

    public void requestAssociatedClientOfSerialNumber(String SerialNumber) {

        // Set the API endpoint
        System.out.println("Call to AutoProv Server for SN Association");
        String apiUrl = "http://192.168.32.16:7549/getClientBySerialNumber/" + SerialNumber;

        // Make the HTTP GET request
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // Assuming "ip_assigned" is a String field, if not, you might need to adjust
            // accordingly
            String ipAssigned = jsonNode.get("ip_assigned").asText();

            System.out.println("IP Assigned: " + ipAssigned);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(response);
    }

    public void AddNewRLSSID(String SerialNum) {
        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetewayDevice.LANDevice.1.WLANConfiguration.1.SSID: ACSTest,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:50rangePips,}",
                "None");

    }

    // Editable SSID
    public void ConfigureSSID(String SerialNum, String NewSSID) {
        NewSSID = NewSSID.replace(" ", "_");
        String Password = "" + NewSSID + "1234";
        System.out.println(Password);

        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID: " + NewSSID
                        + "-2.4G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:"
                        + Password + ",}",
                "None");
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.SSID: " + NewSSID
                        + "-5G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.PreSharedKey.1.PreSharedKey:"
                        + Password + ",}",
                "None");

    }

    // Editable SSID
    public void ConfigureDefaultSSID(String SerialNum) {

        String ssid = "SSID";
        String password = "12345678";

        SaveTask(SerialNum, "GetParameterValues", "InternetGatewayDevice.LANDevice.1", "None");
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID: " + ssid
                        + "-2.4G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.1.PreSharedKey:"
                        + password + ",}",
                "None");
        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.LANDevice.1.WLANConfiguration.5.SSID: " + ssid
                        + "-5G,InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.5.PreSharedKey:"
                        + password + ",}",
                "None");

    }

    public void toggleService(String serialNum) {
        System.out.println("ACS: Pushed task to revert WAN1 to TR069");
        SaveTask(serialNum, "SetParameterValues",
                "{,InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.X_CMCC_ServiceList:TR069,}",
                "None");
    }

    public void AddRoutedWANConfiguration(String SerialNum, String VlanId, String ExternalIPAdd, String DefaultGateway,
            String SubnetMask,
            String DNSServers) {
        if (ExternalIPAdd == null || ExternalIPAdd == " ")
            ExternalIPAdd = "100.10.0.20";
        if (DefaultGateway == null || DefaultGateway == " ")
            DefaultGateway = "192.168.0.1";
        if (SubnetMask == null || SubnetMask == " ")
            SubnetMask = "255.255.255.0";
        if (DNSServers == null || DNSServers == " ")
            DNSServers = "8.8.8.8";

        System.out.println(ExternalIPAdd + DefaultGateway + SubnetMask + DNSServers);

        // WANIP
        SaveTask(SerialNum, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection",
                "None");
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
        sb_config.append(parent_object_WAN_config
                + ".X_CMCC_LanInterface:InternetGatewayDevice.LANDevice.1.WLANConfiguration.2&InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1,");
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
            SaveTask(SerialNum, "SetParameterValues",
                    "{,InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection." + Instance + ".Enable:"
                            + Toggle + ",}",
                    "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Error on Configuration. Fault: Serial Number Not Found";
        }

        return "WAN Instance Toggled";

    }

    public void AddBridgedWANConfiguration(String SerialNum, String VlanId) {

        // Bridged WANIP
        SaveTask(SerialNum, "AddObject", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection",
                "None");
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
            SaveTask(SerialNum, "DeleteObject",
                    "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection." + Instance,
                    "None");

            return "WAN Instance Deleted";
        }

    }

    public void SetPPPUserCredentials(String SerialNum, String Username, String Password) {
        // TODO: Implement PPP username and password
        // Parameters
    }

    public void SetONUUserAdminCredentials(String SerialNum, String Username, String Password) {
        // Parameters - User Admin
        // InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetUserName
        // InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetPassword

        if (Username == null || Username == " ")
            Username = "useradmin";
        if (Password == null || Password == " ")
            Password = "RL87654321";

        SaveTask(SerialNum, "SetParameterValues",
                "{,InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetUserName:" + Username
                        + ",InternetGatewayDevice.DeviceInfo.X_CMCC_ServiceManage.TelnetPassword:" + Password + ",}",
                "None");
    }

    public void UnrougeDevice(String SerialNum) {
        device device = device_front.getBySerialNum(SerialNum);
        device.setparent("Residential");
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

    @Async("asyncExecutor")
    @GetMapping("/getRogueDevices")
    public CompletableFuture<List<device>> getRogueDevices() {
        List<device> Device = new ArrayList<>();
        device_front.findByGroup("unassigned").forEach(Device::add);

        return CompletableFuture.completedFuture(Device);
    }

    // InternetGatewayDevice.ManagementServer.PeriodicInformInterval

    // ---------------- ENDPOINTS ---------------

    @Async("asyncExecutor")
    @RequestMapping(value = "/Reboot/{SerialNum}")
    public CompletableFuture<String> Reboot(@PathVariable String SerialNum) {
        SaveTask(SerialNum, "Reboot", "None", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @PostMapping("/resetssid")
    public CompletableFuture<String> resetSSID(@RequestBody Map<String, String> params) {
        AddNewRLSSID(params.get("SN"));
        return CompletableFuture.completedFuture("Pushed SSID change");
    }

    // Routed WAN Configuration
    @Async("asyncExecutor")
    @PostMapping("/routedWanConfig")
    public CompletableFuture<String> routedWanConfig(@RequestBody Map<String, String> params) {
        AddRoutedWANConfiguration(params.get("SN"), params.get("VlanId"), params.get("ExternalIPAdd"),
                params.get("DefaultGateway"),
                params.get("SubnetMask"), params.get("DNSServers"));
        return CompletableFuture.completedFuture("Pushed Task for WAN2 Configuration");

    }

    // Bridged WAN Configuration
    @Async("asyncExecutor")
    @PostMapping("/bridgedWanConfig")
    public CompletableFuture<String> bridgedWanConfig(@RequestBody Map<String, String> params) {
        AddBridgedWANConfiguration(params.get("SN"), params.get("VlanId"));
        return CompletableFuture.completedFuture("Pushed Task for Bridged WAN2 Configuration");
    }

    // Delete WAN Instance
    @Async("asyncExecutor")
    @PostMapping("/deleteWanInstance")
    public CompletableFuture<String> deleteWanInstance(@RequestBody Map<String, String> params) {

        try {
            SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }
        DeleteWANInstance(params.get("serialNumber"), params.get("instance"));
        return CompletableFuture.completedFuture("Pushed Task for WAN Instance Delete");
    }

    @Async("asyncExecutor")
    @GetMapping("/getWanInstances")
    public CompletableFuture<String> getWanInstance(@RequestBody Map<String, String> params) {
        SaveTask(params.get("SN"), "GetParameterValues", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1",
                "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");

    }

    @Async("asyncExecutor")
    @GetMapping("/getParameterValues")
    public CompletableFuture<String> getParameterValues(@RequestBody Map<String, String> params) {
        SaveTask(params.get("SN"), "GetParameterValues", "InternetGatewayDevice", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");

    }

    @Async("asyncExecutor")
    @GetMapping("/getAllParameterValues")
    public CompletableFuture<String> getAllParameterValues(@RequestBody Map<String, String> params) {
        SaveTask(params.get("SN"), "GetParameterValues", "Device", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");

    }

    @Async("asyncExecutor")
    @PostMapping("/setUserAdmin")
    public CompletableFuture<String> setUserAdminDefault(@RequestBody Map<String, String> params) {
        SetONUUserAdminCredentials(params.get("SN"), params.get("Username"), params.get("Password"));
        return CompletableFuture.completedFuture("User Admin credential change pushed");
    }

    @Async("asyncExecutor")
    @PostMapping("/setInformInterval")
    public CompletableFuture<String> setInformInterval(@RequestBody Map<String, String> params) {
        setDeviceInformInterval(params.get("serialNumber"), Integer.parseInt(params.get("time")));
        return CompletableFuture.completedFuture("Inform timeout pushed for change");
    }

    @Async("asyncExecutor")
    @PostMapping("/toggleWan")
    public CompletableFuture<String> toggleWanInstance(@RequestBody Map<String, String> params)
            throws InterruptedException {

        try {
            SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }

        String taskFault;

        ToggleWAN(params.get("serialNumber"), params.get("Instance"), params.get("Toggle"));

        pendingTask += 1;

        TimeUnit.SECONDS.sleep(11);

        while (pendingTask == 1) {
            System.out.println("Waiting for ONU Inform Response");

            if (faultDetected) {
                taskFault = faults.toString();
                faults = new StringBuilder();
                pendingTask = 0;
                faultDetected = false;
                return CompletableFuture.completedFuture("Error on Toggling. Faults: " + taskFault);
            }

            else {
                pendingTask = 0;
                return CompletableFuture.completedFuture("WAN2 Toggling Pushed");
            }

        }

        return CompletableFuture.completedFuture("No Response");

    }

    @Async("asyncExecutor")
    @PostMapping("/pushSetParameterValueTask")
    public CompletableFuture<String> pushSetParameterValueTask(@RequestBody Map<String, String> params) {

        SaveTask(params.get("serialNumber"), "SetParameterValues", "{," + params.get("parameter") + ",}", "None");
        return CompletableFuture.completedFuture("Task Pushed");

    }

    @Async("asyncExecutor")
    @PostMapping("/getWan2MacAddress")
    public CompletableFuture<String> getSecondWanAddress(@RequestBody Map<String, String> params) {
        SaveTask(params.get("serialNumber"), "GetParameterValues",
                "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2.MACAddress", "None");

        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");
    }

    @Async("asyncExecutor")
    @PostMapping("/resetSsid")
    public CompletableFuture<String> resetSsid(@RequestBody Map<String, String> params) {
        try {
            SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
            ConfigureSSID(params.get("serialNumber"), params.get("clientName"));
            return CompletableFuture.completedFuture("SSID and Password Reset Task Pushed");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }
    }

    @Async("asyncExecutor")
    @PostMapping("/executeAutoConfig")
    public CompletableFuture<String> executeAutoConfig(@RequestBody Map<String, String> params)
            throws InterruptedException {

        System.out.println("----------- Invoked executeAutoConfig from HiveConnect -----------");
        System.out.println(
                params.get("serialNumber") + params.get("clientName") + params.get("vlanId") + params.get("ipAddress"));

        try {
            SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }

        ConfigureSSID(params.get("serialNumber"), params.get("clientName"));
        AddRoutedWANConfiguration(params.get("serialNumber"), params.get("vlanId"), params.get("ipAddress"),
                params.get("defaultGateway"), null, null);
        toggleService(params.get("serialNumber"));

        // TimeUnit.SECONDS.sleep(20);
        // SaveTask(params.get("serialNumber"), "GetParameterValues",
        // "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2.MACAddress",
        // "None");

        // Task Monitoring
        String taskFault;
        pendingTask += 1;

        TimeUnit.SECONDS.sleep(11);

        while (pendingTask == 1) {
            System.out.println("Waiting for ONU Inform Response");

            if (faultDetected) {
                taskFault = faults.toString();
                pendingTask = 0;
                faultDetected = false;
                return CompletableFuture.completedFuture("Error on Configuration. Faults: " + taskFault);
            }

            else {
                pendingTask = 0;
                return CompletableFuture.completedFuture("Auto Configuration Successful with No Faults");
            }

        }

        return CompletableFuture.completedFuture("No Response");

    }

    @Async("asyncExecutor")
    @PostMapping("/onuOnboarded")
    public CompletableFuture<String> onuOnboarded(@RequestBody Map<String, String> params) {
        UnrougeDevice(params.get("serialNumber"));
        return CompletableFuture.completedFuture("Device " + params.get("serialNumber") + " removed from Rogue");
    }

    @Async("asyncExecutor")
    @PostMapping("/rollbackSsid")
    public CompletableFuture<String> rollbackSsid(@RequestBody Map<String, String> params) {
        ConfigureDefaultSSID(params.get("serialNumber"));
        return CompletableFuture
                .completedFuture("Device " + params.get("serialNumber") + "'s SSID has been rolled back to default.");
    }

}
