package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "group_ssid")  
public class group_ssid { // same with zeep ver, will retain
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "ssid")
	private String ssid;

	@Column(name = "forward_mode")
	private String forward_mode;

	@Column(name = "vlan_id")
	private int vlan_id;

	@Column(name = "wlan_id")
	private int wlan_id;

	@Column(name = "encryption_mode")
	private String encryption_moode;

	@Column(name = "limitless")
	private boolean limitless;

	@Column(name = "uplink")
	private int uplink;

	@Column(name = "downlink")
	private int downlink;

	@Column(name = "auth")
	private boolean auth;

	@Column(name = "portal_url")
	private String portal_url;

	@Column(name = "portal_ip")
	private String portal_ip;

	@Column(name = "parent")
	private String parent;

	@Column(name = "gateway_id")
	private String gateway_id;

	@Column(name = "passphrase")
	private String passphrase;

	@Column(name = "seamless")
    private boolean seamless;
    
    public long getId() {
		return id;
	}

	public void setssid(String ssid) {
		this.ssid = ssid;
	}

	public String getssid() {
		return this.ssid;
	}

	public void setforward_mode(String forward_mode) {
		this.forward_mode = forward_mode;
	}

	public String getforward_mode() {
		return this.forward_mode;
	}

	public void setvlan_id(int vlan_id) {
		this.vlan_id = vlan_id;
	}

	public int getvlan_id() {
		return this.vlan_id;
	}

	public void setwlan_id(int wlan_id) {
		this.wlan_id = wlan_id;
	}

	public int getwlan_id() {
		return this.wlan_id;
	}

	public void setencryption_mode(String encryption_mode) {
		this.encryption_moode = encryption_mode;
	}

	public String getencryption_mode() {
		return this.encryption_moode;
	}

	public void setlimitless(boolean limitless) {
		this.limitless = limitless;
	}

	public boolean getlimitless() {
		return this.limitless;
	}

	public void setuplink(int uplink) {
		this.uplink = uplink;
	}

	public int getuplink() {
		return this.uplink;
	}

	public void setdownlink(int downlink) {
		this.downlink = downlink;
	}

	public int getdownlink() {
		return this.downlink;
	}

	public void setauth(boolean auth) {
		this.auth = auth;
	}

	public boolean getauth() {
		return this.auth;
	}

	public void setportal_url(String portal_url) {
		this.portal_url = portal_url;
	}

	public String getportal_url() {
		return this.portal_url;
	}

	public void setportal_ip(String portal_ip) {
		this.portal_ip = portal_ip;
	}

	public String getportal_ip() {
		return this.portal_ip;
	}

	public void setparent(String parent) {
		this.parent = parent;
	}

	public String getparent() {
		return this.parent;
	}

	public void setgateway_id(String gateway_id) {
		this.gateway_id = gateway_id;
	}

	public String getgateway_id() {
		return this.gateway_id;
	}

	public void setpassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getpassphrase() {
		return this.passphrase;
	}

	public void setseamless(boolean seamless) {
		this.seamless = seamless;
	}

	public boolean getseamless() {
		return this.seamless;
    }
    public group_ssid() {
	}
	public group_ssid(String ssid, String forward_mode, int vlan_id,int wlan_id, String encryption_mode, String passphrase, boolean limitless, int uplink, int downlink, boolean auth, String portal_url,
				String portal_ip, String parent, String gateway_id, boolean seamless) {
		this.ssid = ssid;
		this.forward_mode = forward_mode;
		this.vlan_id = vlan_id;
		this.wlan_id = wlan_id;
		this.encryption_moode = encryption_mode;
		this.passphrase = passphrase;
		this.limitless = limitless;
		this.uplink = uplink;
		this.downlink = downlink;
		this.auth = auth;
		this.portal_url = portal_url;
		this.portal_ip = portal_ip;
		this.parent = parent;
		this.gateway_id = gateway_id;
		this.seamless = seamless;
	}	
}
