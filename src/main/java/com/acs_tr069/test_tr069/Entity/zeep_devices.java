package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // change table from devices to zeep_devices
@Table(name = "zeep_devices")
public class zeep_devices { // separated devices repo for zeep
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    
    private Long id;
    private String serial_num;
    private String model;
    private String manufacturer;
    private String oui;
    private String hardware_ver;
    private String root_fs_ver; // found in zeep not in hive
    private String firmware_ver; // found in zeep not in hive
    private String ap_mode; // found in zeep not in hive
    private String mac_address;
    private String os_type; // found in zeep not in hive
    private String host_name; // found in zeep not in hive
    private String max_users; // found in zeep not in hive
    private String ip; // found in zeep not in hive
    private String last_reboot; // found in zeep not in hive
    private String last_boot; // found in zeep not in hive
    private String root_data_model; // found in zeep not in hive
    private String web_auth; // found in zeep not in hive
    private String group_path;
    private String udp_con_req_url;

    public Long get_device_Id(){
        return id;
    }
    public String get_device_SN(){
        return serial_num;
    }
    public String get_device_Model(){
        return model;
    }
    public String get_device_Manufacturer(){
        return manufacturer;
    }
    public String get_device_OUI(){
        return oui;
    }
    public String get_device_Hardware_Ver(){
        return hardware_ver;
    }
    public String get_device_RootFS_Ver(){
        return root_fs_ver;
    }
    public String get_device_Firmware_Ver(){
        return firmware_ver;
    }
    public String get_device_AP_Mode(){
        return ap_mode;
    }
    public String get_device_MAC_ADD(){
        return mac_address;
    }
    public String get_device_OS_Type(){
        return os_type;
    }
    public String get_device_Hostname(){
        return host_name;
    }
    public String get_device_Max_Users(){
        return max_users;
    }
    public String get_IP(){
        return ip;
    }
    public String get_device_Last_Reboot(){
        return last_reboot;
    }
    public String get_device_Last_Boot(){
        return last_boot;
    }
    public String get_device_Root_Data_Model(){
        return root_data_model;
    }
    public String get_device_Web_Auth(){
        return web_auth;
    }
    public String get_device_Group_Path(){
        return group_path;
    }
    public String get_udp_con_req_url(){
        return udp_con_req_url;
    }


    public void set_device_SN(String sn){
        this.serial_num = sn;
    }
    public void set_device_Model(String model){
        this.model = model;
    }
    public void set_device_Manufacturer(String manufacturer){
        this.manufacturer = manufacturer;
    }
    public void set_device_OUI(String oui){
        this.oui = oui;
    }
    public void set_device_Hardware_Ver(String hardware_ver){
        this.hardware_ver = hardware_ver;
    }
    public void set_device_RootFS_Ver(String rootfs_ver){
        this.root_fs_ver = rootfs_ver;
    }
    public void set_device_Firmware_Ver(String firmware_ver){
        this.firmware_ver = firmware_ver;
    }
    public void set_device_AP_Mode(String ap_mode){
        this.ap_mode = ap_mode;
    }
    public void set_device_MAC_ADD(String mac){
        this.mac_address = mac;
    }
    public void set_device_OS_Type(String os_type){
        this.os_type = os_type;
    }
    public void set_device_Hostname(String hostname){
        this.host_name = hostname;
    }
    public void set_device_Max_Users(String max_users){
        this.max_users = max_users;
    }
    public void set_IP(String ip){
        this.ip = ip;
    }
    public void set_device_Last_Reboot(String last_reboot){
        this.last_reboot = last_reboot;
    }
    public void set_device_Last_Boot(String last_boot){
        this.last_boot = last_boot;
    }
    public void set_device_Root_Data_Model(String root_data_model){
        this.root_data_model = root_data_model;
    }
    public void set_device_Web_Auth(String web_auth){
        this.web_auth = web_auth;
    }
    public void set_device_Group_Path(String group_path){
        this.group_path = group_path;
    }
    public void set_udp_con_req_url(String udp_con_req_url){
        this.udp_con_req_url = udp_con_req_url;
    }

}
