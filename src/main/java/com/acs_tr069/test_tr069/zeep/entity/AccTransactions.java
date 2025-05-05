package com.acs_tr069.test_tr069.zeep.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "acc_transactions")
public class AccTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_Number")
    private String accountNumber;

    @Column(name = "package")
    private String packageType;

    @Column(name = "vlanid")
    private String vlanId;

    @Column(name = "gw_id")
    private String gatewayId;

    @Column(name = "gw_sn")
    private String gatewaySerialNumber;

    @Column(name = "gw_address")
    private String gatewayAddress;

    @Column(name = "gw_port")
    private String gatewayPort;

    @Column(name = "ssid")
    private String ssid;

    @Column(name = "apmac")
    private String apMacAddress;

    @Column(name = "mac")
    private String userMacAddress;

    @Column(name = "device")
    private String userDevice;

    @Column(name = "ip")
    private String userIpAddress;

    @Column(name = "token")
    private String token;

    @Column(name = "stage")
    private String stage;

    @Column(name = "total_incoming_packets", columnDefinition = "float8")
    private Double totalIncomingPackets;

    @Column(name = "total_outgoing_packets", columnDefinition = "float8")
    private Double totalOutgoingPackets;

    @Column(name = "created_on", columnDefinition = "timestamptz")
    private Instant createdOn;

    @Column(name = "last_active")
    private String lastActive;
}
