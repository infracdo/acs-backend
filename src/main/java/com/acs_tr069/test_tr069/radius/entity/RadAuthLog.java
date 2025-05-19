package com.acs_tr069.test_tr069.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "radauthlog")
public class RadAuthLog {

    @Column(name = "time_stamp")
    private Long timestamp;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "type")
    private Integer type;

    @Column(name = "reason")
    private String reason;

}
