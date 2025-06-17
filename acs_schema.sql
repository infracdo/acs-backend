--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: acsdb; Type: SCHEMA; Schema: -; Owner: apollo
--

CREATE SCHEMA acsdb;


ALTER SCHEMA acsdb OWNER TO apollo;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: auto_complete; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.auto_complete (
    id integer NOT NULL,
    command character varying(255),
    device_model character varying(255),
    suggestion_list bytea
);


ALTER TABLE acsdb.auto_complete OWNER TO apollo;

--
-- Name: client_list; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.client_list (
    id bigint NOT NULL,
    band character varying(255),
    down character varying(255),
    ip character varying(255),
    macc character varying(255),
    manufacturer character varying(255),
    os character varying(255),
    rssi character varying(255),
    serial_num character varying(255),
    ssid character varying(255),
    traffic character varying(255),
    up character varying(255)
);


ALTER TABLE acsdb.client_list OWNER TO apollo;

--
-- Name: cpe_response_log; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.cpe_response_log (
    id bigint NOT NULL,
    method character varying(255),
    payload character varying(255),
    serial_num character varying(255)
);


ALTER TABLE acsdb.cpe_response_log OWNER TO apollo;

--
-- Name: device; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device (
    id bigint NOT NULL,
    activated boolean,
    date_created character varying(255),
    date_modified character varying(255),
    date_offline character varying(255),
    device_name character varying(255),
    device_type character varying(255),
    location character varying(255),
    mac_address character varying(255),
    model character varying(255),
    parent character varying(255),
    serial_number character varying(255),
    status character varying(255),
    wan1_ip character varying(255),
    wan_ip character varying(255),
    second_wan_mac character varying(255)
);


ALTER TABLE acsdb.device OWNER TO apollo;

--
-- Name: device_logs; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device_logs (
    id bigint NOT NULL,
    offtime character varying(255),
    ontime character varying(255),
    reason character varying(255),
    serial_num character varying(255),
    type character varying(255),
    update_time character varying(255)
);


ALTER TABLE acsdb.device_logs OWNER TO apollo;

--
-- Name: device_model_parameters; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device_model_parameters (
    id bigint NOT NULL,
    con_req_url_parameter character varying(255),
    hardware_ver_parameter character varying(255),
    mac_address_parameter character varying(255),
    management_ip_parameter character varying(255),
    manufacturer character varying(255) NOT NULL,
    model character varying(255) NOT NULL,
    public_ip_parameter character varying(255),
    software_ver_parameter character varying(255),
    udp_con_req_url_parameter character varying(255),
    second_wan_mac character varying(255)
);


ALTER TABLE acsdb.device_model_parameters OWNER TO apollo;

--
-- Name: device_seq; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device_seq (
    next_val bigint
);


ALTER TABLE acsdb.device_seq OWNER TO apollo;

--
-- Name: device_traffic_24h; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device_traffic_24h (
    id bigint NOT NULL,
    date character varying(255),
    rx integer,
    serial_num character varying(255),
    "time" character varying(255),
    tx integer
);


ALTER TABLE acsdb.device_traffic_24h OWNER TO apollo;

--
-- Name: device_traffic_daily; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.device_traffic_daily (
    id bigint NOT NULL,
    date character varying(255),
    rx integer,
    serial_num character varying(255),
    tx integer
);


ALTER TABLE acsdb.device_traffic_daily OWNER TO apollo;

--
-- Name: devices; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.devices (
    id bigint NOT NULL,
    serial_num character varying(255),
    model character varying(255),
    manufacturer character varying(255),
    oui character varying(255),
    hardware_ver character varying(255),
    root_fs_ver character varying(255),
    firmware_ver character varying(255),
    ap_mode character varying(255),
    mac_address character varying(255),
    os_type character varying(255),
    host_name character varying(255),
    max_users character varying(255),
    ip character varying(255),
    last_reboot character varying(255),
    last_boot character varying(255),
    root_data_model character varying(255),
    web_auth character varying(255),
    group_path character varying(255),
    udp_con_req_url character varying(255),
    con_req_url character varying(255),
    cpu_usage character varying(255),
    cwmp_cycle_end smallint,
    device_alias character varying(255),
    management_ip character varying(255),
    memory_usage character varying(255),
    public_ip character varying(255),
    software_ver character varying(255),
    ssids character varying(255),
    second_wan_mac character varying(255)
);


ALTER TABLE acsdb.devices OWNER TO apollo;

--
-- Name: devices_seq; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.devices_seq (
    next_val bigint
);


ALTER TABLE acsdb.devices_seq OWNER TO apollo;

--
-- Name: group_command; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.group_command (
    id bigint NOT NULL,
    command character varying(255),
    description character varying(255),
    model character varying(255),
    parent character varying(255)
);


ALTER TABLE acsdb.group_command OWNER TO apollo;

--
-- Name: group_ssid; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.group_ssid (
    id bigint NOT NULL,
    auth boolean,
    downlink integer,
    encryption_mode character varying(255),
    forward_mode character varying(255),
    gateway_id character varying(255),
    limitless boolean,
    parent character varying(255),
    passphrase character varying(255),
    portal_ip character varying(255),
    portal_url character varying(255),
    seamless boolean,
    ssid character varying(255),
    uplink integer,
    vlan_id integer,
    wlan_id integer
);


ALTER TABLE acsdb.group_ssid OWNER TO apollo;

--
-- Name: groups; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.groups (
    id bigint NOT NULL,
    child character varying(255),
    date_created character varying(255),
    date_modified character varying(255),
    group_name character varying(255),
    location character varying(255),
    parent character varying(255)
);


ALTER TABLE acsdb.groups OWNER TO apollo;

--
-- Name: hibernate_sequence; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.hibernate_sequence (
    next_val bigint
);


ALTER TABLE acsdb.hibernate_sequence OWNER TO apollo;

--
-- Name: httprequestlog; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.httprequestlog (
    id bigint NOT NULL,
    cookie character varying(255),
    device_status character varying(255),
    last_request timestamp with time zone,
    serial_num character varying(255)
);


ALTER TABLE acsdb.httprequestlog OWNER TO apollo;

--
-- Name: radio_info; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.radio_info (
    id bigint NOT NULL,
    band_width character varying(255),
    channel character varying(255),
    gather_time character varying(255),
    power character varying(255),
    radio_index character varying(255),
    sn character varying(255),
    upload_time character varying(255),
    utilization character varying(255)
);


ALTER TABLE acsdb.radio_info OWNER TO apollo;

--
-- Name: taskhandler; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.taskhandler (
    id bigint NOT NULL,
    method character varying(255),
    optional character varying(255),
    parameters character varying(2000),
    serial_num character varying(255)
);


ALTER TABLE acsdb.taskhandler OWNER TO apollo;

--
-- Name: webcli_response_log; Type: TABLE; Schema: acsdb; Owner: apollo
--

CREATE TABLE acsdb.webcli_response_log (
    id bigint NOT NULL,
    command_output bytea,
    command_used bytea,
    device_sn character varying(255),
    time_saved timestamp with time zone
);


ALTER TABLE acsdb.webcli_response_log OWNER TO apollo;

--
-- Name: auto_complete; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.auto_complete (
    id integer NOT NULL,
    command character varying(255),
    device_model character varying(255),
    suggestion_list bytea
);


ALTER TABLE public.auto_complete OWNER TO apollo;

--
-- Name: cpe_response_log; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.cpe_response_log (
    id bigint NOT NULL,
    method character varying(255),
    payload character varying(255),
    serial_num character varying(255)
);


ALTER TABLE public.cpe_response_log OWNER TO apollo;

--
-- Name: device; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.device (
    id bigint NOT NULL,
    activated boolean,
    date_created character varying(255),
    date_modified character varying(255),
    date_offline character varying(255),
    device_name character varying(255),
    device_type character varying(255),
    location character varying(255),
    mac_address character varying(255),
    model character varying(255),
    parent character varying(255),
    serial_number character varying(255),
    status character varying(255)
);


ALTER TABLE public.device OWNER TO apollo;

--
-- Name: devices; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.devices (
    id bigint NOT NULL,
    ap_mode character varying(255),
    con_req_url character varying(255),
    cwmp_cycle_end boolean,
    firmware_ver character varying(255),
    group_path character varying(255),
    hardware_ver character varying(255),
    host_name character varying(255),
    ip character varying(255),
    last_boot character varying(255),
    last_reboot character varying(255),
    mac_address character varying(255),
    manufacturer character varying(255),
    max_users character varying(255),
    model character varying(255),
    os_type character varying(255),
    oui character varying(255),
    root_data_model character varying(255),
    root_fs_ver character varying(255),
    serial_num character varying(255),
    udp_con_req_url character varying(255),
    web_auth character varying(255)
);


ALTER TABLE public.devices OWNER TO apollo;

--
-- Name: group_command; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.group_command (
    id bigint NOT NULL,
    command character varying(255),
    description character varying(255),
    model character varying(255),
    parent character varying(255)
);


ALTER TABLE public.group_command OWNER TO apollo;

--
-- Name: group_ssid; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.group_ssid (
    id bigint NOT NULL,
    auth boolean,
    downlink integer,
    encryption_mode character varying(255),
    forward_mode character varying(255),
    gateway_id character varying(255),
    limitless boolean,
    parent character varying(255),
    passphrase character varying(255),
    portal_ip character varying(255),
    portal_url character varying(255),
    seamless boolean,
    ssid character varying(255),
    uplink integer,
    vlan_id integer,
    wlan_id integer
);


ALTER TABLE public.group_ssid OWNER TO apollo;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.groups (
    id bigint NOT NULL,
    child character varying(255),
    date_created character varying(255),
    date_modified character varying(255),
    group_name character varying(255),
    location character varying(255),
    parent character varying(255)
);


ALTER TABLE public.groups OWNER TO apollo;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: apollo
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO apollo;

--
-- Name: httprequestlog; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.httprequestlog (
    id bigint NOT NULL,
    cookie character varying(255),
    device_status character varying(255),
    last_request timestamp without time zone,
    serial_num character varying(255)
);


ALTER TABLE public.httprequestlog OWNER TO apollo;

--
-- Name: taskhandler; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.taskhandler (
    id bigint NOT NULL,
    method character varying(255),
    optional character varying(255),
    parameters character varying(255),
    serial_num character varying(255)
);


ALTER TABLE public.taskhandler OWNER TO apollo;

--
-- Name: webcli_response_log; Type: TABLE; Schema: public; Owner: apollo
--

CREATE TABLE public.webcli_response_log (
    id bigint NOT NULL,
    command_output bytea,
    command_used bytea,
    device_sn character varying(255)
);


ALTER TABLE public.webcli_response_log OWNER TO apollo;

--
-- Name: auto_complete idx_16389_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.auto_complete
    ADD CONSTRAINT idx_16389_primary PRIMARY KEY (id);


--
-- Name: client_list idx_16394_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.client_list
    ADD CONSTRAINT idx_16394_primary PRIMARY KEY (id);


--
-- Name: cpe_response_log idx_16399_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.cpe_response_log
    ADD CONSTRAINT idx_16399_primary PRIMARY KEY (id);


--
-- Name: device idx_16402_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.device
    ADD CONSTRAINT idx_16402_primary PRIMARY KEY (id);


--
-- Name: device_logs idx_16410_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.device_logs
    ADD CONSTRAINT idx_16410_primary PRIMARY KEY (id);


--
-- Name: device_model_parameters idx_16413_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.device_model_parameters
    ADD CONSTRAINT idx_16413_primary PRIMARY KEY (id);


--
-- Name: device_traffic_24h idx_16418_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.device_traffic_24h
    ADD CONSTRAINT idx_16418_primary PRIMARY KEY (id);


--
-- Name: device_traffic_daily idx_16421_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.device_traffic_daily
    ADD CONSTRAINT idx_16421_primary PRIMARY KEY (id);


--
-- Name: devices idx_16424_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.devices
    ADD CONSTRAINT idx_16424_primary PRIMARY KEY (id);


--
-- Name: group_command idx_16432_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.group_command
    ADD CONSTRAINT idx_16432_primary PRIMARY KEY (id);


--
-- Name: group_ssid idx_16435_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.group_ssid
    ADD CONSTRAINT idx_16435_primary PRIMARY KEY (id);


--
-- Name: groups idx_16440_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.groups
    ADD CONSTRAINT idx_16440_primary PRIMARY KEY (id);


--
-- Name: httprequestlog idx_16446_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.httprequestlog
    ADD CONSTRAINT idx_16446_primary PRIMARY KEY (id);


--
-- Name: radio_info idx_16449_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.radio_info
    ADD CONSTRAINT idx_16449_primary PRIMARY KEY (id);


--
-- Name: taskhandler idx_16454_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.taskhandler
    ADD CONSTRAINT idx_16454_primary PRIMARY KEY (id);


--
-- Name: webcli_response_log idx_16459_primary; Type: CONSTRAINT; Schema: acsdb; Owner: apollo
--

ALTER TABLE ONLY acsdb.webcli_response_log
    ADD CONSTRAINT idx_16459_primary PRIMARY KEY (id);


--
-- Name: auto_complete auto_complete_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.auto_complete
    ADD CONSTRAINT auto_complete_pkey PRIMARY KEY (id);


--
-- Name: cpe_response_log cpe_response_log_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.cpe_response_log
    ADD CONSTRAINT cpe_response_log_pkey PRIMARY KEY (id);


--
-- Name: device device_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT device_pkey PRIMARY KEY (id);


--
-- Name: devices devices_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT devices_pkey PRIMARY KEY (id);


--
-- Name: group_command group_command_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.group_command
    ADD CONSTRAINT group_command_pkey PRIMARY KEY (id);


--
-- Name: group_ssid group_ssid_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.group_ssid
    ADD CONSTRAINT group_ssid_pkey PRIMARY KEY (id);


--
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- Name: httprequestlog httprequestlog_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.httprequestlog
    ADD CONSTRAINT httprequestlog_pkey PRIMARY KEY (id);


--
-- Name: taskhandler taskhandler_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.taskhandler
    ADD CONSTRAINT taskhandler_pkey PRIMARY KEY (id);


--
-- Name: webcli_response_log webcli_response_log_pkey; Type: CONSTRAINT; Schema: public; Owner: apollo
--

ALTER TABLE ONLY public.webcli_response_log
    ADD CONSTRAINT webcli_response_log_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

