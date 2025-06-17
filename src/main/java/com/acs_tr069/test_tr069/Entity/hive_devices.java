// package com.acs_tr069.test_tr069.Entity;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.Table;

// @Entity // change table from devices to hive_devices
// @Table(name = "hive_devices")
// public class hive_devices { // separated devices repo for hive
//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
    
//     private Long id; 
//     private String serial_num;
//     private String model;
//     private String manufacturer;
//     private String oui;
//     private String mac_address;
//     private String second_wan_mac; // found in hive not in zeep
    
//     public String getSecond_wan_mac() {
//         return second_wan_mac;
//     }
//     public void setSecond_wan_mac(String second_wan_mac) {
//         this.second_wan_mac = second_wan_mac;
//     }
//     private String udp_con_req_url;
//     private String con_req_url;
//     private Boolean cwmp_cycle_end;
    
//     private String management_ip;
//     private String public_ip;
//     private String hardware_ver;
//     private String software_ver;
//     private String device_alias;
//     private String ssids;
//     private String memory_usage;
//     private String cpu_usage;




//     public Long getid(){
//         return id;
//     }
//     public String getserial_num(){
//         return serial_num;
//     }
//     public String getmodel(){
//         return model;
//     }
//     public String getmanufacturer(){
//         return manufacturer;
//     }
//     public String getoui(){
//         return oui;
//     }
//     public String getmac_address(){
//         return mac_address;
//     }
//     public String getudp_con_req_url(){
//         return udp_con_req_url;
//     }
//     public String getcon_req_url(){
//         return con_req_url;
//     }
//     public Boolean getcwmp_cycle_end(){
//         return cwmp_cycle_end;
//     }
    
//     public String getmanagement_ip(){
//         return management_ip;
//     }
//     public String getpublic_ip(){
//         return public_ip;
//     }
//     public String gethardware_ver(){
//         return hardware_ver;
//     }
//     public String getsoftware_ver(){
//         return software_ver;
//     }
//     public String getdevice_alias(){
//         return device_alias;
//     }
//     public String getssids(){
//         return ssids;
//     }
//     public String getmemory_usage(){
//         return memory_usage;
//     }
//     public String getcpu_usage(){
//         return cpu_usage;
//     }




//     public void setserial_num(String serial_num){
//         this.serial_num = serial_num;
//     }
//     public void setmodel(String model){
//         this.model = model;
//     }
//     public void setmanufacturer(String manufacturer){
//         this.manufacturer = manufacturer;
//     }
//     public void setoui(String oui){
//         this.oui = oui;
//     }
//     public void setmac_address(String mac_address){
//         this.mac_address = mac_address;
//     }
//     public void setudp_con_req_url(String udp_con_req_url){
//         this.udp_con_req_url = udp_con_req_url;
//     }
//     public void setcon_req_url(String con_req_url){
//         this.con_req_url = con_req_url;
//     }
//     public void setcwmp_cycle_end(Boolean 	cwmp_cycle_end){
//         this.cwmp_cycle_end = cwmp_cycle_end;
//     }

//     public void setmanagement_ip(String management_ip){
//         this.management_ip = management_ip;
//     }
//     public void setpublic_ip(String public_ip){
//         this.public_ip = public_ip;
//     }
//     public void sethardware_ver(String hardware_ver){
//         this.hardware_ver = hardware_ver;
//     }
//     public void setsoftware_ver(String software_ver){
//         this.software_ver = software_ver;
//     }
//     public void setdevice_alias(String device_alias){
//         this.device_alias = device_alias;
//     }
//     public void setssids(String ssids){
//         this.ssids = ssids;
//     }
//     public void setmemory_usage(String memory_usage){
//         this.memory_usage = memory_usage;
//     }
//     public void setcpu_usage(String cpu_usage){
//         this.cpu_usage = cpu_usage;
//     }

// }
