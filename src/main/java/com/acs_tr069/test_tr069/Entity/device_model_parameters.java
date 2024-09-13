package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "device_model_parameters")
public class device_model_parameters {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "mac_address_parameter", nullable = true)
    private String mac_address_parameter;

    @Column(name = "udp_con_req_url_parameter", nullable = true)
    private String udp_con_req_url_parameter;

    @Column(name = "con_req_url_parameter", nullable = true)
    private String con_req_url_parameter;

    @Column(name = "second_wan_mac", nullable = true)
    private String second_wan_mac;
    
    @Column(name = "management_ip_parameter", nullable = true)
    private String management_ip_parameter;

    @Column(name = "public_ip_parameter", nullable = true)
    private String public_ip_parameter;

    @Column(name = "hardware_ver_parameter", nullable = true)
    private String hardware_ver_parameter;

    @Column(name = "software_ver_parameter", nullable = true)
    private String software_ver_parameter;

}
