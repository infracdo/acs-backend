package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "device_model_parameters")
public class DeviceModelParameters {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "mac_address_parameter", nullable = true)
    private String MacAddressParameter;

    @Column(name = "udp_con_req_url_parameter", nullable = true)
    private String UdpConReqUrlParameter;

    @Column(name = "con_req_url_parameter", nullable = true)
    private String ConReqUrlParameter;

    @Column(name = "second_wan_mac", nullable = true)
    private String SecondWanMac;
    
    @Column(name = "management_ip_parameter", nullable = true)
    private String ManagementIpParameter;

    @Column(name = "public_ip_parameter", nullable = true)
    private String PublicIpParameter;

    @Column(name = "hardware_ver_parameter", nullable = true)
    private String HardwareVerParameter;

    @Column(name = "software_ver_parameter", nullable = true)
    private String SoftwareVerParameter;

}
