package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "group_ssid")  
public class GroupSsid { // same with zeep ver, will retain
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String ssid;

	@Column(name = "forward_mode")
	private String forwardMode;

	@Column(name = "vlan_id")
	private int vlanId;

	@Column(name = "wlan_id")
	private int wlanId;

	@Column(name = "encryption_mode")
	private String encryptionMode;

	private boolean limitless;
	private int uplink;
	private int downlink;
	private boolean auth;

	@Column(name = "portal_url")
	private String portalUrl;

	@Column(name = "portal_ip")
	private String portalIp;

	private String parent;

	@Column(name = "gateway_id")
	private String gatewayId;

	private String passphrase;
    private boolean seamless;
    
    public GroupSsid() {
	}
	public GroupSsid(String ssid, String forward_mode, int vlanId,int wlanId, String encryptionMode, String passphrase, boolean limitless, int uplink, int downlink, boolean auth, String portalUrl, String portalIp, String parent, String gatewayId, boolean seamless) {
		this.ssid = ssid;
		this.forwardMode = forward_mode;
		this.vlanId = vlanId;
		this.wlanId = wlanId;
		this.encryptionMode = encryptionMode;
		this.passphrase = passphrase;
		this.limitless = limitless;
		this.uplink = uplink;
		this.downlink = downlink;
		this.auth = auth;
		this.portalUrl = portalUrl;
		this.portalIp = portalIp;
		this.parent = parent;
		this.gatewayId = gatewayId;
		this.seamless = seamless;
	}	
}
