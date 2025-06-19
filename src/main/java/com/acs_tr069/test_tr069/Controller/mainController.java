package com.acs_tr069.test_tr069.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.acs_tr069.test_tr069.Entity.AutoComplete;
import com.acs_tr069.test_tr069.Entity.ClientList;
import com.acs_tr069.test_tr069.Entity.Device;
import com.acs_tr069.test_tr069.Entity.DeviceLogs;
import com.acs_tr069.test_tr069.Entity.DeviceModelParameters;
import com.acs_tr069.test_tr069.Entity.DeviceTraffic24h;
import com.acs_tr069.test_tr069.Entity.DeviceTrafficDaily;
import com.acs_tr069.test_tr069.Entity.Devices;
import com.acs_tr069.test_tr069.Entity.GroupCommand;
import com.acs_tr069.test_tr069.Entity.GroupSsid;
import com.acs_tr069.test_tr069.Entity.Groups;
import com.acs_tr069.test_tr069.Entity.RadioInfo;
import com.acs_tr069.test_tr069.Repo.AutoCompleteRepository;
import com.acs_tr069.test_tr069.Repo.ClientListRepository;
import com.acs_tr069.test_tr069.Repo.DeviceFrontendRepository;
import com.acs_tr069.test_tr069.Repo.DeviceLogsRepository;
import com.acs_tr069.test_tr069.Repo.DeviceModelParametersRepository;
import com.acs_tr069.test_tr069.Repo.DeviceTraffic24hRepository;
import com.acs_tr069.test_tr069.Repo.DeviceTrafficDailyRepository;
import com.acs_tr069.test_tr069.Repo.DevicesRepository;
import com.acs_tr069.test_tr069.Repo.GroupCommandRepository;
import com.acs_tr069.test_tr069.Repo.GroupsRepository;
import com.acs_tr069.test_tr069.Repo.RadioInfoRepository;
import com.acs_tr069.test_tr069.Repo.SsidRepository;
import com.acs_tr069.test_tr069.Services.HelperService;
import com.google.common.base.Charsets;

@CrossOrigin(origins = "*")
@RestController
public class MainController { // place all hive and zeep apis that arent in conflict with each other here
    
    @Autowired
    private DevicesRepository devicesRepo; 
    @Autowired
    private SsidRepository ssidRepo; 
    @Autowired
    private DeviceFrontendRepository device_front; 
    @Autowired
    private GroupCommandRepository GroupCommandRepo; 
    @Autowired
    private GroupsRepository group_repo; 
    @Autowired
    private AutoCompleteRepository auto_completeRepo; 
    @Autowired
    private RadioInfoRepository radio_infoRepo; 
    @Autowired
    private DeviceLogsRepository device_logRepo; 
    @Autowired
    private DeviceTraffic24hRepository dev_traff_24Repo; 
    @Autowired
    private DeviceTrafficDailyRepository dev_traff_dailyRepo; 
    @Autowired
    private ClientListRepository client_listRepo; 
    @Autowired
    private DeviceModelParametersRepository device_model_parameters_repo; 
    @Autowired
    private HelperService helperService;

    private int pendingTask = 0;
    private Boolean faultDetected = false;
    private StringBuilder faults = new StringBuilder();
    
    // @Async("asyncExecutor")
    @RequestMapping(value = "/macclog/api/upload/stream/staLinkQuality_info") 
    public ResponseEntity<String> staLinkQuality_info(@RequestBody(required = false) String Payload, @RequestParam("uploadTime") String uploadTime, @RequestParam("logType") String logType, @RequestParam("mac") String mac, @RequestParam("sn") String sn, @RequestParam("isCompressed") Integer isCompressed, @RequestParam("isEachCompressed") Integer isEachCompressed, @RequestParam("isEncrypted") Integer isEncrypted, HttpServletRequest request) {
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
    public CompletableFuture<ResponseEntity<String>> DeviceRadio_info(@RequestBody(required = false) String Payload) throws JSONException {
        JSONObject json_payload = new JSONObject(Payload);
        String sn = json_payload.getString("sn").toString();
        System.out.println(sn);
        String uploadTime = json_payload.getString("uploadTime").toString();
        System.out.println(uploadTime);
        String data = json_payload.get("data").toString();
        System.out.println(data);

        SaveRadioInfo(sn, uploadTime, data);
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CONTINUE).contentType(MediaType.TEXT_XML).body(null));
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getradioinfo") 
    public CompletableFuture<List<RadioInfo>> getradioinfo(@RequestParam("sn") String sn) throws JSONException {
        try {
            List<RadioInfo> data = radio_infoRepo.findBySerialNumEquals(sn);
            return CompletableFuture.completedFuture(data);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public void SaveRadioInfo(String sn, String uploadTime, String data) throws JSONException { 
        JSONArray jsonarray_data = new JSONArray(data);
        System.out.println(jsonarray_data.length());
        int data_count = jsonarray_data.length();

        new Thread(() -> {
            for (int i = 0; i < data_count; i++) {
                JSONObject current_data = null;
                try {
                    current_data = jsonarray_data.getJSONObject(i);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                List<RadioInfo> device;
                try {
                    device = radio_infoRepo.findBySerialNumEquals(sn);
                } catch (Exception e) {
                    device = null;
                }

                if (device.isEmpty()) {
                    try {
                        RadioInfo newRadioInfo = new RadioInfo();
                        newRadioInfo.setSn(sn);
                        newRadioInfo.setUploadTime(uploadTime);
                        newRadioInfo.setRadioIndex(current_data.getString("radioIndex").toString());
                        newRadioInfo.setChannel(current_data.getString("channel").toString());
                        newRadioInfo.setGatherTime(current_data.getString("gatherTime").toString());
                        newRadioInfo.setUtilization(current_data.getString("utilization").toString());
                        newRadioInfo.setPower(current_data.getString("power").toString());
                        newRadioInfo.setBandWidth(current_data.getString("bandWidth").toString());
                        radio_infoRepo.save(newRadioInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Boolean already_exist = false;
                    RadioInfo tobe_saved = null;
                    try {
                        for (RadioInfo radio_info : device) {
                            if (radio_info.getRadioIndex().contentEquals(current_data.getString("radioIndex").toString())) {
                                tobe_saved = radio_info;
                                already_exist = true;
                                break;
                            } else {
                                already_exist = false;
                            }
                        }
                        System.out.println(already_exist);
                        if (already_exist) {
                            tobe_saved.setSn(sn);
                            tobe_saved.setUploadTime(uploadTime);
                            tobe_saved.setRadioIndex(current_data.getString("radioIndex").toString());
                            tobe_saved.setChannel(current_data.getString("channel").toString());
                            tobe_saved.setGatherTime(current_data.getString("gatherTime").toString());
                            tobe_saved.setUtilization(current_data.getString("utilization").toString());
                            tobe_saved.setPower(current_data.getString("power").toString());
                            tobe_saved.setBandWidth(current_data.getString("bandWidth").toString());
                            radio_infoRepo.save(tobe_saved);
                        } else {
                            RadioInfo newRadioInfo = new RadioInfo();
                            newRadioInfo.setSn(sn);
                            newRadioInfo.setUploadTime(uploadTime);
                            newRadioInfo.setRadioIndex(current_data.getString("radioIndex").toString());
                            newRadioInfo.setChannel(current_data.getString("channel").toString());
                            newRadioInfo.setGatherTime(current_data.getString("gatherTime").toString());
                            newRadioInfo.setUtilization(current_data.getString("utilization").toString());
                            newRadioInfo.setPower(current_data.getString("power").toString());
                            newRadioInfo.setBandWidth(current_data.getString("bandWidth").toString());
                            radio_infoRepo.save(newRadioInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // @RequestMapping(value="/macclog/log/upload?file={filename:.+}&type={type}}")
    @Async("asyncExecutor")
    @RequestMapping(value = "/macclog/log/upload", method = RequestMethod.POST) 
    public CompletableFuture<ResponseEntity<String>> uploadfile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) throws IllegalStateException, IOException {
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

            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CONTINUE).contentType(MediaType.TEXT_XML).body(null));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_XML).body(null));
        }
    }

    public void process_file(String sourcefile, String dest_location, String orig_file_name) throws FileNotFoundException, IOException, InterruptedException, JSONException {
        Path source = Paths.get(sourcefile);

        new Thread(() -> {
            String file_name = orig_file_name.replace(".tar.gz", "");
            Path target = Paths.get(dest_location + "/" + file_name);
            try {
                Files.createDirectories(target);
            } catch (IOException e2) {
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
                e.printStackTrace();
            }

            java.io.File source_file = new java.io.File(sourcefile);
            boolean deleted = source_file.delete();
            System.out.println(deleted);

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
                StringBuilder data = new StringBuilder();

                try {
                    String filelocation = new java.io.File(dest_location + "/" + file_name + "/all/" + string + "/" + filename).getAbsolutePath();
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
                }
                System.out.println("data:" + data.toString());
                JSONObject jsondata = null;
                try {
                    jsondata = new JSONObject(data.toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (string.contains("system")) {
                    try {
                        Devices current_device = devicesRepo.getBySerialNum(serial_num);
                        current_device.setCpuUsage(jsondata.getString("cpu_rate"));
                        current_device.setMemoryUsage(jsondata.getString("memory_rate"));
                        devicesRepo.save(current_device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (string.contains("offdrop")) {
                    try {
                        List<DeviceLogs> logs = device_logRepo.findBySerialNumEquals(serial_num);
                        if (logs.isEmpty()) {
                            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            DeviceLogs dev_log = new DeviceLogs();
                            dev_log.setSerialNum(serial_num);
                            dev_log.setUpdateTime(dt_date.format(now));
                            dev_log.setOntime(jsondata.getString("on_time"));
                            dev_log.setOfftime(jsondata.getString("off_time"));
                            dev_log.setReason(jsondata.getString("off_reason"));

                            if (jsondata.getString("off_reason").contains("reload")) {
                                dev_log.setType("reload");
                            }
                            if (jsondata.getString("off_reason").contains("online")) {
                                dev_log.setType("online");
                            }
                            if (jsondata.getString("off_reason").contains("offline")) {
                                dev_log.setType("offline");
                            }
                            if (jsondata.getString("off_reason").contains("restart")) {
                                dev_log.setType("restart");
                            }
                            System.out.println(dev_log);
                            device_logRepo.save(dev_log);
                        }
                        if (!logs.get(0).getReason().contains(jsondata.getString("off_reason"))) {
                            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            DeviceLogs dev_log = new DeviceLogs();
                            dev_log.setSerialNum(serial_num);
                            dev_log.setUpdateTime(dt_date.format(now));
                            dev_log.setOntime(jsondata.getString("on_time"));
                            dev_log.setOfftime(jsondata.getString("off_time"));
                            dev_log.setReason(jsondata.getString("off_reason"));

                            if (jsondata.getString("off_reason").contains("reload")) {
                                dev_log.setType("reload");
                            }
                            if (jsondata.getString("off_reason").contains("online")) {
                                dev_log.setType("online");
                            }
                            if (jsondata.getString("off_reason").contains("offline")) {
                                dev_log.setType("offline");
                            }
                            if (jsondata.getString("off_reason").contains("restart")) {
                                dev_log.setType("restart");
                            }
                            System.out.println(dev_log);
                            device_logRepo.save(dev_log);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        DeviceTraffic24h flow = new DeviceTraffic24h();
                        flow.setSerialNum(serial_num);
                        DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        DateTimeFormatter dt_time = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        flow.setDate(dt_date.format(now));
                        flow.setTime(dt_time.format(now));
                        flow.setRx((rx / 1024) / 1024);
                        flow.setTx((tx / 1024) / 1024);
                        dev_traff_24Repo.save(flow);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                e.printStackTrace();
            }
        }).start();
    }
    
    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevicelogs") 
    public CompletableFuture<List<DeviceLogs>> get_dev_log(@RequestParam("sn") String sn) {
        List<DeviceLogs> logs = device_logRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(logs);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevicedetails") 
    public CompletableFuture<Devices> get_dev_details(@RequestParam("sn") String sn) {
        Devices device_details = devicesRepo.getBySerialNum(sn);
        return CompletableFuture.completedFuture(device_details);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevice24htraffic") 
    public CompletableFuture<List<DeviceTraffic24h>> get_dev_traffic(@RequestParam("sn") String sn) {
        List<DeviceTraffic24h> device_traffic_24hs = dev_traff_24Repo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(device_traffic_24hs);
    }

    @Scheduled(cron = "00 23 * * * ?") 
    private void save_daily_traffic() {
        new Thread(() -> {
            DateTimeFormatter dt_date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String current_date = dt_date.format(now);
            Iterable<Device> divices = device_front.findAll();
            for (Device device : divices) {
                int rx = 0;
                int tx = 0;
                List<DeviceTraffic24h> dev_traffic_data = dev_traff_24Repo.findBySerialNumEquals(device.getSerialNumber());
                for (DeviceTraffic24h device_traffic : dev_traffic_data) {
                    if (device_traffic.getDate().contains(current_date)) {
                        rx = rx + device_traffic.getRx();
                        tx = tx + device_traffic.getTx();
                        dev_traff_24Repo.delete(device_traffic);
                    }
                }
                DeviceTrafficDaily traffic_daily = new DeviceTrafficDaily();
                traffic_daily.setSerialNum(device.getSerialNumber());
                traffic_daily.setDate(current_date);
                traffic_daily.setRx((rx / 1024) / 1024);
                traffic_daily.setTx((tx / 1024) / 1024);
                dev_traff_dailyRepo.save(traffic_daily);
            }
        }).start();
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "getdevice-daily-traffic") 
    public CompletableFuture<List<DeviceTrafficDaily>> get_daily_traffic(@RequestParam("sn") String sn, @RequestParam("days") Integer days) {
        List<DeviceTrafficDaily> traffic = dev_traff_dailyRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(traffic);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/setdevicedailytraffic") 
    public CompletableFuture<DeviceTrafficDaily> set_daily_traffic(@RequestParam("sn") String sn, @RequestParam("date") String date, @RequestParam("rx") Integer rx, @RequestParam("tx") Integer tx) {
        DeviceTrafficDaily traffic = new DeviceTrafficDaily();
        traffic.setSerialNum(sn);
        traffic.setDate(date);
        traffic.setRx(rx);
        traffic.setTx(tx);
        dev_traff_dailyRepo.save(traffic);
        return CompletableFuture.completedFuture(traffic);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getclient_list") 
    public CompletableFuture<List<ClientList>> getclient_list(@RequestParam("sn") String sn) {
        List<ClientList> client_list = client_listRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(client_list);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/setclient_list") 
    public CompletableFuture<ClientList> setclient_list(
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
        ClientList client = new ClientList();
        client.setSerialNum(sn);
        client.setIp(ip);
        client.setMacc(macc);
        client.setSsid(ssid);
        client.setRssi(rssi);
        client.setBand(band);
        client.setTraffic(traffic);
        client.setOs(os);
        client.setManufacturer(manufacturer);
        client.setUp(up);
        client.setDown(down);
        client_listRepo.save(client);

        return CompletableFuture.completedFuture(client);
    }

    
    @Async("asyncExecutor")
    @RequestMapping(value = "/ExecuteGroupCommand/{SerialNum}, {ID}") 
    public CompletableFuture<String> ExecuteGroupCommand(@PathVariable String SerialNum, @PathVariable String ID) {
        Long id = Long.parseLong(ID);
        GroupCommand current_command = GroupCommandRepo.getByID(id);
        Device DevicesInGroup = device_front.getBySerialNum(SerialNum);

        String[] command_in_line = current_command.getCommand().split("\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int j = 0; j < command_in_line.length; j++) {
            sb.append(",Command:" + command_in_line[j]);
        }
        sb.append(",}");
        if (current_command.getModel().contains("ALL")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            DevicesInGroup.setDateModified(dtf.format(now));
            device_front.save(DevicesInGroup);

            helperService.SaveTask(DevicesInGroup.getSerialNumber(), "Command", sb.toString(), "config");
        } else {
            String deviceModel = DevicesInGroup.getModel();
            if (current_command.getModel().contains(deviceModel)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                DevicesInGroup.setDateModified(dtf.format(now));
                device_front.save(DevicesInGroup);

                helperService.SaveTask(DevicesInGroup.getSerialNumber(), "Command", sb.toString(), "config");
            }
        }
        return CompletableFuture.completedFuture("ExecuteCommand");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddSSID/{SerialNum}, {ID}") 
    public CompletableFuture<String> AddSSID(@PathVariable String SerialNum, @PathVariable String ID) {
        Device current_device = device_front.getBySerialNum(SerialNum);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        current_device.setDateModified(dtf.format(now));
        device_front.save(current_device);

        Long id = Long.parseLong(ID);
        GroupSsid ssid_to_add = ssidRepo.getByID(id);
        Integer wlan_id = ssid_to_add.getWlanId();

        StringBuilder SSIDSettings = new StringBuilder();
        String encryptionMode = null;
        String encrypModetoConvert = ssid_to_add.getEncryptionMode();

        if (encrypModetoConvert.contains("Open")) {
            encryptionMode = "None";
        }
        if (encrypModetoConvert.contains("WPA-PSK")) {
            encryptionMode = "WPA-Personal";
        }
        if (encrypModetoConvert.contains("WPA2-PSK")) {
            encryptionMode = "WPA2-Personal";
        }

        SSIDSettings.append("{,Device.WiFi.SSID." + wlan_id + ".SSID:" + ssid_to_add.getSsid());
        SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".LowerLayers:1&2");
        if (ssid_to_add.getForwardMode().contains("Nat")) {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:true");
        } else {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_IsHidden:false");
        }
        SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_FowardType:" + ssid_to_add.getForwardMode());

        if (ssid_to_add.getForwardMode().contains("Bridge")) {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_VLANID:" + ssid_to_add.getVlanId());
        }
        SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.ModeEnabled:" + encryptionMode);
        if (encryptionMode.contains("None") == false) {
            SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.KeyPassphrase:" + ssid_to_add.getPassphrase() + ",}");
        } else {
            SSIDSettings.append(",}");
        }
        
        helperService.AddNewSSID(SSIDSettings.toString(), SerialNum, wlan_id.toString());

        if (ssid_to_add.isAuth()) {
            StringBuilder AuthSettings = new StringBuilder();
            AuthSettings.append("{,WiFiDog");
            AuthSettings.append("," + ssid_to_add.getPortalIp());
            AuthSettings.append("," + ssid_to_add.getPortalUrl());
            AuthSettings.append(",js");
            AuthSettings.append("," + ssid_to_add.getGatewayId());
            AuthSettings.append(",true");
            AuthSettings.append("," + ssid_to_add.isSeamless() + ",}");

            helperService.AddNewAuth(AuthSettings.toString(), SerialNum, wlan_id.toString());
        }
        return CompletableFuture.completedFuture("Adding SSID");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddObject/{SerialNum}, {ObjectName}") 
    public CompletableFuture<String> AddNewObject(@PathVariable String SerialNum, @PathVariable String ObjectName) {
        helperService.SaveTask(SerialNum, "AddObject", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/AddObjectXMPP/{SerialNum}") 
    public CompletableFuture<String> AddObjectXMPP(@PathVariable String SerialNum) {
        helperService.SaveTask(SerialNum, "AddObject", "Device.XMPP.Connection.", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetParameterValues/{SerialNum}, {ObjectName}") 
    public CompletableFuture<String> GetParameterValues(@PathVariable String SerialNum, @PathVariable String ObjectName) {
        helperService.SaveTask(SerialNum, "GetParameterValues", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/SetParameterValues/{SerialNum}") 
    public CompletableFuture<String> SetParameterValues(@RequestBody String ParameterList, @PathVariable String SerialNum) {
        helperService.SaveTask(SerialNum, "SetParameterValues", ParameterList, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetRPCMethods/{SerialNum}") 
    public CompletableFuture<String> GetRPCMethods(@PathVariable String SerialNum) {
        helperService.SaveTask(SerialNum, "GetRPCMethods", "None", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    // TODO; add reboot with endpoint /Reboot/{SerialNum}

    @Async("asyncExecutor")
    @RequestMapping(value = "/DeleteObject/{SerialNum}, {ObjectName}") 
    public CompletableFuture<String> DeleteObject(@PathVariable String SerialNum, @PathVariable String ObjectName) {
        helperService.SaveTask(SerialNum, "DeleteObject", ObjectName, "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/FactoryReset/{SerialNum}") 
    public CompletableFuture<String> FactoryReset(@PathVariable String SerialNum) { 
        helperService.SaveTask(SerialNum, "FactoryReset", "None", "None");
        return CompletableFuture.completedFuture("Reseting Device");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/Command/{SerialNum}") 
    public CompletableFuture<String> Command(@RequestBody String ObjectName, @PathVariable String SerialNum) {
        helperService.SaveTask(SerialNum, "Command", ObjectName, "config");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/GetRogueDevices")
    public CompletableFuture<List<Device>> GetRougeDevices() { 
        List<Device> roguedevices = device_front.findByGroup("unassigned");
        return CompletableFuture.completedFuture(roguedevices);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/CheckParentGroup")
    public CompletableFuture<String> CheckParentGroup(@RequestBody String parent) { 
        List<Groups> groups = group_repo.findByParent(parent);
        if (groups.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("{,");
            for (Groups groups2 : groups) {
                sb.append(groups2.getGroupName() + ",");
            }
            sb.append("}");
            return CompletableFuture.completedFuture(sb.toString());
        } else {
            return CompletableFuture.completedFuture("parent not exist");
        }
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/WebCli/ {SerialNum}")
    public CompletableFuture<DeferredResult<ResponseEntity<String>>> WebCli(@RequestBody String Modes, @PathVariable String SerialNum, HttpServletRequest request) throws JSONException { 
        String[] modez = Modes.split(",", -1);
        String ObjectName = modez[7];
        System.out.println("Command:############" + Modes);
        System.out.println("Command:############" + ObjectName);
        helperService.AddWebCLiTask(Modes, SerialNum, ObjectName);
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        new Thread(() -> {
            String body = "";
            while (true) {
                body = helperService.GetCLIOutput(SerialNum, ObjectName);
                if (body != null) {
                    break;
                }
            }
            result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
        }, "MyThread for ").start();
        return CompletableFuture.completedFuture(result);
    }
    
    @Async("asyncExecutor")
    @RequestMapping(value = "/CliAutoComplete/ {SerialNum}") 
    public CompletableFuture<DeferredResult<ResponseEntity<String>>> CliAutoComplete(@RequestBody String Modes, @PathVariable String SerialNum, HttpServletRequest request) throws JSONException { 
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        new Thread(() -> {
            String body = "";
            String[] modez = Modes.split(",", -1);
            String ObjectName = modez[7];
            Devices current_device = devicesRepo.getBySerialNum(SerialNum);
            String deviceModel = current_device.getModel();
            List<AutoComplete> suggestion_lists = auto_completeRepo.findByDeviceModel(deviceModel);
            boolean found = false;
            if (!suggestion_lists.isEmpty()) {
                for (AutoComplete auto_complete : suggestion_lists) {
                    System.out.println("from db" + auto_complete.getCommand());
                    System.out.println("from ObjName" + ObjectName);
                    if (ObjectName.contains(auto_complete.getCommand())) {
                        body = new String(auto_complete.getSuggestionList(), Charsets.UTF_8);
                        found = true;
                        result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                    }
                }
                System.out.println("found " + found);
                if (!found) {
                    try {
                        helperService.AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    while (true) {
                        body = helperService.GetCLIOutput(SerialNum, ObjectName);
                        if (body != null) {
                            break;
                        }
                    }
                    try {
                        AutoComplete NewSuggestion = new AutoComplete();
                        NewSuggestion.setDeviceModel(deviceModel);
                        NewSuggestion.setCommand(ObjectName);
                        NewSuggestion.setSuggestionList(body.getBytes(Charsets.UTF_8));
                        auto_completeRepo.save(NewSuggestion);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }
            } else {
                if (!found) {
                    try {
                        helperService.AddWebCLiTask(Modes, SerialNum, ObjectName);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    while (true) {
                        body = helperService.GetCLIOutput(SerialNum, ObjectName);
                        if (body != null) {
                            break;
                        }
                    }
                    try {
                        AutoComplete NewSuggestion = new AutoComplete();
                        NewSuggestion.setDeviceModel(deviceModel);
                        NewSuggestion.setCommand(ObjectName);
                        NewSuggestion.setSuggestionList(body.getBytes(Charsets.UTF_8));
                        auto_completeRepo.save(NewSuggestion);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    result.setResult(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(body));
                }
            }
        }, "MyThread for ").start();
        return CompletableFuture.completedFuture(result);
    }
    
    @Async("asyncExecutor")
    @GetMapping("/getssid")
    public CompletableFuture<Iterable<GroupSsid>> getAllCustomers() { 
        List<GroupSsid> customers = new ArrayList<>();
        ssidRepo.findAll().forEach(customers::add);
        return CompletableFuture.completedFuture(customers);
    }

    @Async("asyncExecutor")
    @GetMapping("/getgroup")
    public CompletableFuture<List<Groups>> getAllGroups() { 
        List<Groups> customers = new ArrayList<>();
        group_repo.findAll().forEach(customers::add);
        return CompletableFuture.completedFuture(customers);
    }

    @Async("asyncExecutor")
    @GetMapping("/getdevice")
    public CompletableFuture<List<Device>> getAllDevice() { 
        List<Device> Device = new ArrayList<>();
        device_front.findAll().forEach(Device::add);
        return CompletableFuture.completedFuture(Device);
    }

    @Async("asyncExecutor")
    @GetMapping("/getcommand")
    public CompletableFuture<List<GroupCommand>> getAllCommands() { 
        List<GroupCommand> commands = new ArrayList<>();
        GroupCommandRepo.findAll().forEach(commands::add);
        return CompletableFuture.completedFuture(commands);
    }

    @Async("asyncExecutor")
    @PostMapping("/adddevice")
    public CompletableFuture<Device> postGroup(@RequestBody Device DEVICE) { 
        Device Device = device_front.save(new Device(DEVICE.getDeviceName(), DEVICE.getMacAddress(), DEVICE.getSerialNumber(), DEVICE.getLocation(), DEVICE.getParent(), DEVICE.getDateCreated(), DEVICE.getDateModified(), DEVICE.getDateOffline(), DEVICE.getStatus(), DEVICE.getModel(), DEVICE.getDeviceType()));
        return CompletableFuture.completedFuture(Device);
    }

    @Async("asyncExecutor")
    @PostMapping("/addgroup")
    public CompletableFuture<Groups> postGroup(@RequestBody Groups GROUP) { 
        Groups GroupS = group_repo.save(new Groups(GROUP.getGroupName(), GROUP.getLocation(), GROUP.getParent(), GROUP.getChild(), GROUP.getDateCreated(), GROUP.getDateModified()));
        return CompletableFuture.completedFuture(GroupS);
    }

    @Async("asyncExecutor")
    @PostMapping("/addcommand")
    public CompletableFuture<GroupCommand> postCommand(@RequestBody GroupCommand COMMAND) { 
        GroupCommand Commands = GroupCommandRepo.save(new GroupCommand(COMMAND.getModel(), COMMAND.getDescription(), COMMAND.getParent(), COMMAND.getCommand()));
        return CompletableFuture.completedFuture(Commands);
    }

    @Async("asyncExecutor")
    @PostMapping("/addssid")
    public CompletableFuture<GroupSsid> postSSID(@RequestBody GroupSsid ssID) {  
        GroupSsid _ssid = ssidRepo.save(new GroupSsid(
                ssID.getSsid(),
                ssID.getForwardMode(),
                ssID.getVlanId(),
                ssID.getWlanId(),
                ssID.getEncryptionMode(),
                ssID.getPassphrase(),
                ssID.isLimitless(),
                ssID.getUplink(),
                ssID.getDownlink(),
                ssID.isAuth(),
                ssID.getPortalUrl(),
                ssID.getPortalIp(),
                ssID.getParent(),
                ssID.getGatewayId(),
                ssID.isSeamless()));
        return CompletableFuture.completedFuture(_ssid);
    }

    @Async("asyncExecutor")
    @PutMapping("/updatessid/{id}")
    public CompletableFuture<ResponseEntity<GroupSsid>> updateCustomer(@PathVariable("id") long id, @RequestBody GroupSsid ssID) { 
        Optional<GroupSsid> customerData = ssidRepo.findById(id);
        if (customerData.isPresent()) {
            GroupSsid _ssid = customerData.get();
            _ssid.setSsid(ssID.getSsid());
            _ssid.setForwardMode(ssID.getForwardMode());
            _ssid.setVlanId(ssID.getVlanId());
            _ssid.setWlanId(ssID.getWlanId());
            _ssid.setEncryptionMode(ssID.getEncryptionMode());
            _ssid.setPassphrase(ssID.getPassphrase());
            _ssid.setLimitless(ssID.isLimitless());
            _ssid.setUplink(ssID.getUplink());
            _ssid.setDownlink(ssID.getDownlink());
            _ssid.setAuth(ssID.isAuth());
            _ssid.setPortalUrl(ssID.getPortalUrl());
            _ssid.setPortalIp(ssID.getPortalIp());
            _ssid.setParent(ssID.getParent());
            _ssid.setGatewayId(ssID.getGatewayId());
            _ssid.setSeamless(ssID.isSeamless());
            return CompletableFuture.completedFuture(new ResponseEntity<>(ssidRepo.save(_ssid), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updatecommand/{id}")
    public CompletableFuture<ResponseEntity<GroupCommand>> updateCommand(@PathVariable("id") long id, @RequestBody GroupCommand Command) { 
        Optional<GroupCommand> commandData = GroupCommandRepo.findById(id);
        if (commandData.isPresent()) {
            GroupCommand _command = commandData.get();
            _command.setModel(Command.getModel());
            _command.setParent(Command.getParent());
            _command.setDescription(Command.getDescription());
            _command.setCommand(Command.getCommand());
            return CompletableFuture.completedFuture(new ResponseEntity<>(GroupCommandRepo.save(_command), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updategroup/{id}")
    public CompletableFuture<ResponseEntity<Groups>> updateGroup(@PathVariable("id") long id, @RequestBody Groups Group) { 
        Optional<Groups> groupData = group_repo.findById(id);
        if (groupData.isPresent()) {
            Groups _groups = groupData.get();
            _groups.setGroupName(Group.getGroupName());
            _groups.setParent(Group.getParent());
            _groups.setLocation(Group.getLocation());
            _groups.setChild(Group.getChild());
            _groups.setDateCreated(Group.getDateCreated());
            _groups.setDateModified(Group.getDateModified());
            return CompletableFuture.completedFuture(new ResponseEntity<>(group_repo.save(_groups), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updatedevice/{id}")
    public CompletableFuture<ResponseEntity<Device>> updateDevice(@PathVariable("id") long id, @RequestBody Device Device) { 
        Optional<Device> deviceData = device_front.findById(id);
        if (deviceData.isPresent()) {
            Device _device = deviceData.get();
            _device.setDeviceName(Device.getDeviceName());
            _device.setParent(Device.getParent());
            _device.setLocation(Device.getLocation());
            _device.setMacAddress(Device.getMacAddress());
            _device.setSerialNumber(Device.getSerialNumber());
            _device.setDateCreated(Device.getDateCreated());
            _device.setDateModified(Device.getDateModified());
            _device.setDeviceType(Device.getDeviceType());
            return CompletableFuture.completedFuture(new ResponseEntity<>(device_front.save(_device), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletessid/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteCustomer(@PathVariable("id") long id) { 
        ssidRepo.deleteById(id);
        return CompletableFuture.completedFuture(new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletecommand/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteCommand(@PathVariable("id") long id) { 
        GroupCommandRepo.deleteById(id);
        return CompletableFuture.completedFuture(new ResponseEntity<>("Command has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletegroup/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteGroup(@PathVariable("id") long id) { 
        group_repo.deleteById(id);
        return CompletableFuture.completedFuture(new ResponseEntity<>("Group has been deleted!", HttpStatus.OK));
    }

    @Async("asyncExecutor")
    @DeleteMapping("/deletedevice/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteDevice(@PathVariable("id") long id) { 
        device_front.deleteById(id);
        return CompletableFuture.completedFuture(new ResponseEntity<>("Device has been deleted!", HttpStatus.OK));
    }
    
    @Async("asyncExecutor")
    @PostMapping("/addDeviceModel")
    public CompletableFuture<ResponseEntity<Object>> addDeviceModel(@RequestBody Map<String, String> params) {
        try {
            if (device_model_parameters_repo.searchByManufacturerAndModel(params.get("manufacturer").toString(), params.get("model").toString()) == null) {
                DeviceModelParameters new_model = new DeviceModelParameters();
                new_model.setModel(params.get("model").toString());
                new_model.setManufacturer(params.get("manufacturer").toString());

                if (!params.get("mac_address").isEmpty() || (params.get("mac_address") != null)) {
                    new_model.setMacAddressParameter(params.get("mac_address").toString());
                }
                if (!params.get("udp_con_req_url").isEmpty() || (params.get("udp_con_req_url") != null)) {
                    new_model.setUdpConReqUrlParameter(params.get("udp_con_req_url").toString());
                }
                if (!params.get("con_req_url").isEmpty() || (params.get("con_req_url") != null)) {
                    new_model.setConReqUrlParameter(params.get("con_req_url").toString());
                }
                if (!params.get("management_ip").isEmpty() || (params.get("management_ip") != null)) {
                    new_model.setManagementIpParameter(params.get("management_ip").toString());
                }
                if (!params.get("public_ip").isEmpty() || (params.get("public_ip") != null)) {
                    new_model.setPublicIpParameter(params.get("public_ip").toString());
                }
                if (!params.get("hardware_ver").isEmpty() || (params.get("hardware_ver") != null)) {
                    new_model.setHardwareVerParameter(params.get("hardware_ver").toString());
                }
                if (!params.get("software_ver").isEmpty() || (params.get("software_ver") != null)) {
                    new_model.setSoftwareVerParameter(params.get("software_ver").toString());
                }

                device_model_parameters_repo.save(new_model);
                return CompletableFuture.completedFuture(new ResponseEntity<>(new_model, HttpStatus.OK));
            } else {
                DeviceModelParameters new_model = device_model_parameters_repo.searchByManufacturerAndModel(params.get("manufacturer").toString(), params.get("model").toString());
                new_model.setModel(params.get("model").toString());
                new_model.setManufacturer(params.get("manufacturer").toString());

                if (!params.get("mac_address").isEmpty() || (params.get("mac_address") != null)) {
                    new_model.setMacAddressParameter(params.get("mac_address").toString());
                }
                if (!params.get("udp_con_req_url").isEmpty() || (params.get("udp_con_req_url") != null)) {
                    new_model.setUdpConReqUrlParameter(params.get("udp_con_req_url").toString());
                }
                if (!params.get("con_req_url").isEmpty() || (params.get("con_req_url") != null)) {
                    new_model.setConReqUrlParameter(params.get("con_req_url").toString());
                }
                if (!params.get("management_ip").isEmpty() || (params.get("management_ip") != null)) {
                    new_model.setManagementIpParameter(params.get("management_ip").toString());
                }
                if (!params.get("public_ip").isEmpty() || (params.get("public_ip") != null)) {
                    new_model.setPublicIpParameter(params.get("public_ip").toString());
                }
                if (!params.get("hardware_ver").isEmpty() || (params.get("hardware_ver") != null)) {
                    new_model.setHardwareVerParameter(params.get("hardware_ver").toString());
                }
                if (!params.get("software_ver").isEmpty() || (params.get("software_ver") != null)) {
                    new_model.setSoftwareVerParameter(params.get("software_ver").toString());
                }

                device_model_parameters_repo.save(new_model);
                return CompletableFuture.completedFuture(new ResponseEntity<>(new_model, HttpStatus.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(new ResponseEntity<>("Error adding model", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @Async("asyncExecutor")
    @GetMapping("/getRogueDevices")
    public CompletableFuture<List<Device>> getRogueDevices() {
        List<Device> Device = new ArrayList<>();
        device_front.findByGroup("unassigned").forEach(Device::add);
        return CompletableFuture.completedFuture(Device);
    }
    
    @Async("asyncExecutor")
    @RequestMapping(value = "/Reboot/{SerialNum}")
    public CompletableFuture<String> Reboot(@PathVariable String SerialNum) {
        helperService.SaveTask(SerialNum, "Reboot", "None", "None");
        return CompletableFuture.completedFuture("Task Added");
    }

    @Async("asyncExecutor")
    @PostMapping("/resetssid")
    public CompletableFuture<String> resetSSID(@RequestBody Map<String, String> params) {
        helperService.AddNewRLSSID(params.get("SN"));
        return CompletableFuture.completedFuture("Pushed SSID change");
    }

    @Async("asyncExecutor")
    @PostMapping("/routedWanConfig")
    public CompletableFuture<String> routedWanConfig(@RequestBody Map<String, String> params) {
        helperService.AddRoutedWANConfiguration(params.get("SN"), params.get("VlanId"), params.get("ExternalIPAdd"), params.get("DefaultGateway"), params.get("SubnetMask"), params.get("DNSServers"));
        return CompletableFuture.completedFuture("Pushed Task for WAN2 Configuration");
    }

    @Async("asyncExecutor")
    @PostMapping("/bridgedWanConfig")
    public CompletableFuture<String> bridgedWanConfig(@RequestBody Map<String, String> params) {
        helperService.AddBridgedWANConfiguration(params.get("SN"), params.get("VlanId"));
        return CompletableFuture.completedFuture("Pushed Task for Bridged WAN2 Configuration");
    }

    @Async("asyncExecutor")
    @PostMapping("/deleteWanInstance")
    public CompletableFuture<String> deleteWanInstance(@RequestBody Map<String, String> params) {
        try {
            helperService.SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }
        helperService.DeleteWANInstance(params.get("serialNumber"), params.get("instance"));
        return CompletableFuture.completedFuture("Pushed Task for WAN Instance Delete");
    }

    @Async("asyncExecutor")
    @GetMapping("/getWanInstances")
    public CompletableFuture<String> getWanInstance(@RequestBody Map<String, String> params) {
        helperService.SaveTask(params.get("SN"), "GetParameterValues", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");
    }

    @Async("asyncExecutor")
    @GetMapping("/getParameterValues")
    public CompletableFuture<String> getParameterValues(@RequestBody Map<String, String> params) {
        helperService.SaveTask(params.get("SN"), "GetParameterValues","InternetGatewayDevice", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");
    }

    @Async("asyncExecutor")
    @GetMapping("/getAllParameterValues")
    public CompletableFuture<String> getAllParameterValues(@RequestBody Map<String, String> params) {
        helperService.SaveTask(params.get("SN"), "GetParameterValues", "Device", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");
    }

    @Async("asyncExecutor")
    @PostMapping("/setUserAdmin")
    public CompletableFuture<String> setUserAdminDefault(@RequestBody Map<String, String> params) {
        helperService.SetONUUserAdminCredentials(params.get("SN"), params.get("Username"), params.get("Password"));
        return CompletableFuture.completedFuture("User Admin credential change pushed");
    }

    @Async("asyncExecutor")
    @PostMapping("/setInformInterval")
    public CompletableFuture<String> setInformInterval(@RequestBody Map<String, String> params) {
        helperService.setDeviceInformInterval(params.get("serialNumber"), Integer.parseInt(params.get("time")));
        return CompletableFuture.completedFuture("Inform timeout pushed for change");
    }

    @Async("asyncExecutor")
    @PostMapping("/toggleWan")
    public CompletableFuture<String> toggleWanInstance(@RequestBody Map<String, String> params) throws InterruptedException {
        try {
            helperService.SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }

        String taskFault;
        helperService.ToggleWAN(params.get("serialNumber"), params.get("Instance"), params.get("Toggle"));
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
        helperService.SaveTask(params.get("serialNumber"), "SetParameterValues", "{," + params.get("parameter") + ",}", "None");
        return CompletableFuture.completedFuture("Task Pushed");
    }

    @Async("asyncExecutor")
    @PostMapping("/getWan2MacAddress")
    public CompletableFuture<String> getSecondWanAddress(@RequestBody Map<String, String> params) {
        helperService.SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.2.MACAddress", "None");
        return CompletableFuture.completedFuture("Check logs for feedback and PCAP");
    }

    @Async("asyncExecutor")
    @PostMapping("/resetSsid")
    public CompletableFuture<String> resetSsid(@RequestBody Map<String, String> params) {
        try {
            helperService.SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
            helperService.ConfigureSSID(params.get("serialNumber"), params.get("accountNo"));
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
        System.out.println(params.get("serialNumber") + params.get("accountNumber") + params.get("clientName") + params.get("vlanId") + params.get("ipAddress"));
        String ssid = params.get("accountNumber") != null ? params.get("accountNumber") : params.get("clientName"); 
        try {
            helperService.SaveTask(params.get("serialNumber"), "GetParameterValues", "InternetGatewayDevice", "None");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error on Configuration. Fault: Serial Number Not Found");
        }
        helperService.ConfigureSSID(params.get("serialNumber"), ssid); 
        helperService.AddRoutedWANConfiguration(params.get("serialNumber"), params.get("vlanId"), params.get("ipAddress"), params.get("defaultGateway"), null, null);
        helperService.toggleService(params.get("serialNumber"));

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
        helperService.UnrougeDevice(params.get("serialNumber"));
        return CompletableFuture.completedFuture("Device " + params.get("serialNumber") + " removed from Rogue");
    }

    @Async("asyncExecutor")
    @PostMapping("/rollbackSsid")
    public CompletableFuture<String> rollbackSsid(@RequestBody Map<String, String> params) {
        helperService.ConfigureDefaultSSID(params.get("serialNumber"));
        return CompletableFuture.completedFuture("Device " + params.get("serialNumber") + "'s SSID has been rolled back to default.");
    }
}
