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
@Table(name = "acc_sessions")
public class AccSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_Number")
    private String accountNumber;

    @Column(name = "package")
    private String packageType;

    @Column(name = "limit_count")
    private Integer limitCount;

    @Column(name = "limit_type")
    private String limitType;

    @Column(name = "counter")
    private Integer counter;

    @Column(name = "incoming_packets", columnDefinition = "float8")
    private Double incomingPackets;

    @Column(name = "outgoing_packets", columnDefinition = "float8")
    private Double outgoingPackets;

    @Column(name = "created_on", columnDefinition = "timestamptz")
    private Instant createdOn;

    @Column(name = "last_modified")
    private String lastModified;
}
