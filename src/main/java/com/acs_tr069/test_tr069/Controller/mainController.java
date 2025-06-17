package com.acs_tr069.test_tr069.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
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

import com.acs_tr069.test_tr069.Entity.auto_complete;
import com.acs_tr069.test_tr069.Entity.client_list;
import com.acs_tr069.test_tr069.Entity.device;
import com.acs_tr069.test_tr069.Entity.device_logs;
import com.acs_tr069.test_tr069.Entity.device_model_parameters;
import com.acs_tr069.test_tr069.Entity.device_traffic_24h;
import com.acs_tr069.test_tr069.Entity.device_traffic_daily;
import com.acs_tr069.test_tr069.Entity.devices;
import com.acs_tr069.test_tr069.Entity.group_command;
import com.acs_tr069.test_tr069.Entity.group_ssid;
import com.acs_tr069.test_tr069.Entity.groups;
import com.acs_tr069.test_tr069.Entity.radio_info;
import com.acs_tr069.test_tr069.Repo.auto_completeRepository;
import com.acs_tr069.test_tr069.Repo.client_listRepository;
import com.acs_tr069.test_tr069.Repo.device_frontendRepository;
import com.acs_tr069.test_tr069.Repo.device_logsRepository;
import com.acs_tr069.test_tr069.Repo.device_model_parametersRepository;
import com.acs_tr069.test_tr069.Repo.device_traffic_24hRepository;
import com.acs_tr069.test_tr069.Repo.device_traffic_dailyRepository;
import com.acs_tr069.test_tr069.Repo.devicesRepository;
import com.acs_tr069.test_tr069.Repo.group_commandRepo;
import com.acs_tr069.test_tr069.Repo.groupsRepository;
import com.acs_tr069.test_tr069.Repo.radio_infoRepository;
import com.acs_tr069.test_tr069.Repo.ssidRepository;
import com.acs_tr069.test_tr069.Services.HelperService;
import com.google.common.base.Charsets;

@CrossOrigin(origins = "*")
@RestController
public class mainController { // place all hive and zeep apis that arent in conflict with each other here
    
    @Autowired
    private devicesRepository devicesRepo; 
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
    public CompletableFuture<List<radio_info>> getradioinfo(@RequestParam("sn") String sn) throws JSONException {
        try {
            List<radio_info> data = radio_infoRepo.findBySerialNumEquals(sn);
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

                List<radio_info> device;
                try {
                    device = radio_infoRepo.findBySerialNumEquals(sn);
                } catch (Exception e) {
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
                        e.printStackTrace();
                    }
                } else {
                    Boolean already_exist = false;
                    radio_info tobe_saved = null;
                    try {
                        for (radio_info radio_info : device) {
                            if (radio_info.getradioIndex().contentEquals(current_data.getString("radioIndex").toString())) {
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
                        devices current_device = devicesRepo.getBySerialNum(serial_num);
                        current_device.setCpuUsage(jsondata.getString("cpu_rate"));
                        current_device.setMemoryUsage(jsondata.getString("memory_rate"));
                        devicesRepo.save(current_device);
                    } catch (Exception e) {
                        e.printStackTrace();
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
    public CompletableFuture<List<device_logs>> get_dev_log(@RequestParam("sn") String sn) {
        List<device_logs> logs = device_logRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(logs);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/getdevicedetails") 
    public CompletableFuture<devices> get_dev_details(@RequestParam("sn") String sn) {
        devices device_details = devicesRepo.getBySerialNum(sn);
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
                List<device_traffic_24h> dev_traffic_data = dev_traff_24Repo.findBySerialNumEquals(device.getSerialNumber());
                for (device_traffic_24h device_traffic : dev_traffic_data) {
                    if (device_traffic.getdate().contains(current_date)) {
                        rx = rx + device_traffic.getrx();
                        tx = tx + device_traffic.gettx();
                        dev_traff_24Repo.delete(device_traffic);
                    }
                }
                device_traffic_daily traffic_daily = new device_traffic_daily();
                traffic_daily.setserial_num(device.getSerialNumber());
                traffic_daily.setdate(current_date);
                traffic_daily.setrx((rx / 1024) / 1024);
                traffic_daily.settx((tx / 1024) / 1024);
                dev_traff_dailyRepo.save(traffic_daily);
            }
        }).start();
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "getdevice-daily-traffic") 
    public CompletableFuture<List<device_traffic_daily>> get_daily_traffic(@RequestParam("sn") String sn, @RequestParam("days") Integer days) {
        List<device_traffic_daily> traffic = dev_traff_dailyRepo.findBySerialNumEquals(sn);
        return CompletableFuture.completedFuture(traffic);
    }

    @Async("asyncExecutor")
    @RequestMapping(value = "/setdevicedailytraffic") 
    public CompletableFuture<device_traffic_daily> set_daily_traffic(@RequestParam("sn") String sn, @RequestParam("date") String date, @RequestParam("rx") Integer rx, @RequestParam("tx") Integer tx) {
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

    
    @Async("asyncExecutor")
    @RequestMapping(value = "/ExecuteGroupCommand/{SerialNum}, {ID}") 
    public CompletableFuture<String> ExecuteGroupCommand(@PathVariable String SerialNum, @PathVariable String ID) {
        Long id = Long.parseLong(ID);
        group_command current_command = GroupCommandRepo.getByID(id);
        device DevicesInGroup = device_front.getBySerialNum(SerialNum);

        String[] command_in_line = current_command.getcommand().split("\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int j = 0; j < command_in_line.length; j++) {
            sb.append(",Command:" + command_in_line[j]);
        }
        sb.append(",}");
        if (current_command.getmodel().contains("ALL")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            DevicesInGroup.setDateModified(dtf.format(now));
            device_front.save(DevicesInGroup);

            helperService.SaveTask(DevicesInGroup.getSerialNumber(), "Command", sb.toString(), "config");
        } else {
            String deviceModel = DevicesInGroup.getModel();
            if (current_command.getmodel().contains(deviceModel)) {
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
        device current_device = device_front.getBySerialNum(SerialNum);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        current_device.setDateModified(dtf.format(now));
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
        SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_FowardType:" + ssid_to_add.getforward_mode());

        if (ssid_to_add.getforward_mode().contains("Bridge")) {
            SSIDSettings.append(",Device.WiFi.SSID." + wlan_id + ".X_WWW-RUIJIE-COM-CN_VLANID:" + ssid_to_add.getvlan_id());
        }
        SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.ModeEnabled:" + encryptionMode);
        if (encryptionMode.contains("None") == false) {
            SSIDSettings.append(",Device.WiFi.AccessPoint." + wlan_id + ".Security.KeyPassphrase:" + ssid_to_add.getpassphrase() + ",}");
        } else {
            SSIDSettings.append(",}");
        }
        
        helperService.AddNewSSID(SSIDSettings.toString(), SerialNum, wlan_id.toString());

        if (ssid_to_add.getauth()) {
            StringBuilder AuthSettings = new StringBuilder();
            AuthSettings.append("{,WiFiDog");
            AuthSettings.append("," + ssid_to_add.getportal_ip());
            AuthSettings.append("," + ssid_to_add.getportal_url());
            AuthSettings.append(",js");
            AuthSettings.append("," + ssid_to_add.getgateway_id());
            AuthSettings.append(",true");
            AuthSettings.append("," + ssid_to_add.getseamless() + ",}");

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
    public CompletableFuture<List<device>> GetRougeDevices() { 
        List<device> roguedevices = device_front.findByGroup("unassigned");
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
            devices current_device = devicesRepo.getBySerialNum(SerialNum);
            String deviceModel = current_device.getModel();
            List<auto_complete> suggestion_lists = auto_completeRepo.findByDeviceModel(deviceModel);
            boolean found = false;
            if (!suggestion_lists.isEmpty()) {
                for (auto_complete auto_complete : suggestion_lists) {
                    System.out.println("from db" + auto_complete.get_command());
                    System.out.println("from ObjName" + ObjectName);
                    if (ObjectName.contains(auto_complete.get_command())) {
                        body = new String(auto_complete.get_suggestion_list(), Charsets.UTF_8);
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
                        auto_complete NewSuggestion = new auto_complete();
                        NewSuggestion.set_device_model(deviceModel);
                        NewSuggestion.set_command(ObjectName);
                        NewSuggestion.set_suggestion_list(body.getBytes(Charsets.UTF_8));
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
                        auto_complete NewSuggestion = new auto_complete();
                        NewSuggestion.set_device_model(deviceModel);
                        NewSuggestion.set_command(ObjectName);
                        NewSuggestion.set_suggestion_list(body.getBytes(Charsets.UTF_8));
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
    public CompletableFuture<Iterable<group_ssid>> getAllCustomers() { 
        List<group_ssid> customers = new ArrayList<>();
        ssidRepo.findAll().forEach(customers::add);
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
        GroupCommandRepo.findAll().forEach(commands::add);
        return CompletableFuture.completedFuture(commands);
    }

    @Async("asyncExecutor")
    @PostMapping("/adddevice")
    public CompletableFuture<device> postGroup(@RequestBody device DEVICE) { 
        device Device = device_front.save(new device(DEVICE.getDeviceName(), DEVICE.getMacAddress(), DEVICE.getSerialNumber(), DEVICE.getLocation(), DEVICE.getParent(), DEVICE.getDateCreated(), DEVICE.getDateModified(), DEVICE.getDateOffline(), DEVICE.getStatus(), DEVICE.getModel(), DEVICE.getDeviceType()));
        return CompletableFuture.completedFuture(Device);
    }

    @Async("asyncExecutor")
    @PostMapping("/addgroup")
    public CompletableFuture<groups> postGroup(@RequestBody groups GROUP) { 
        groups GroupS = group_repo.save(new groups(GROUP.getgroup_name(), GROUP.getlocation(), GROUP.getparent(), GROUP.getchild(), GROUP.getdate_created(), GROUP.getdate_modified()));
        return CompletableFuture.completedFuture(GroupS);
    }

    @Async("asyncExecutor")
    @PostMapping("/addcommand")
    public CompletableFuture<group_command> postCommand(@RequestBody group_command COMMAND) { 
        group_command Commands = GroupCommandRepo.save(new group_command(COMMAND.getmodel(), COMMAND.getdescription(), COMMAND.getparent(), COMMAND.getcommand()));
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
    public CompletableFuture<ResponseEntity<group_ssid>> updateCustomer(@PathVariable("id") long id, @RequestBody group_ssid ssID) { 
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
    public CompletableFuture<ResponseEntity<group_command>> updateCommand(@PathVariable("id") long id, @RequestBody group_command Command) { 
        Optional<group_command> commandData = GroupCommandRepo.findById(id);
        if (commandData.isPresent()) {
            group_command _command = commandData.get();
            _command.setmodel(Command.getmodel());
            _command.setparent(Command.getparent());
            _command.setdescription(Command.getdescription());
            _command.setcommand(Command.getcommand());
            return CompletableFuture.completedFuture(new ResponseEntity<>(GroupCommandRepo.save(_command), HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @Async("asyncExecutor")
    @PutMapping("/updategroup/{id}")
    public CompletableFuture<ResponseEntity<groups>> updateGroup(@PathVariable("id") long id, @RequestBody groups Group) { 
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
    public CompletableFuture<ResponseEntity<device>> updateDevice(@PathVariable("id") long id, @RequestBody device Device) { 
        Optional<device> deviceData = device_front.findById(id);
        if (deviceData.isPresent()) {
            device _device = deviceData.get();
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
                device_model_parameters new_model = device_model_parameters_repo.searchByManufacturerAndModel(params.get("manufacturer").toString(), params.get("model").toString());
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
            return CompletableFuture.completedFuture(new ResponseEntity<>("Error adding model", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @Async("asyncExecutor")
    @GetMapping("/getRogueDevices")
    public CompletableFuture<List<device>> getRogueDevices() {
        List<device> Device = new ArrayList<>();
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
