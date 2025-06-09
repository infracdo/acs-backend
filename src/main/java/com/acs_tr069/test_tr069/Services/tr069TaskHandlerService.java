package com.acs_tr069.test_tr069.Services;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.acs_tr069.test_tr069.CWMPResponses.tr069Response;
import com.acs_tr069.test_tr069.Entity.hive_devices;
import com.acs_tr069.test_tr069.Entity.taskhandler;
import com.acs_tr069.test_tr069.Repo.hiveDevicesRepository;
import com.acs_tr069.test_tr069.Repo.taskhandlerRepo;
import com.acs_tr069.test_tr069.UDP.udp_sender;

@Service
public class tr069TaskHandlerService {
    
    
    private tr069Response tr069response;

    @Autowired
    private taskhandlerRepo taskhandlerRepo;

    @Autowired
    private hiveDevicesRepository hiveDevicesRepo;

    private String Tr069ResponseHandler(String Method, String Parameters, String Option){
        
        if(Method.contains("AddObject")){
            String body =  tr069response.AddObject(Parameters);
            return body;
        }
        if(Method.contains("GetParameterValues")){
            String body = tr069response.GetParameterValues(Parameters);
            return body;
        }
        if(Method.contains("GetParameterNames")){
            String body = tr069response.GetParameterNames(Parameters);
            return body;
        }
        if(Method.contains("SetParameterValues")){
            String body = tr069response.SetParameterValues(Parameters);
            return body;
        }
        if(Method.contains("Command")){
            String body = tr069response.Command(Parameters, "config");
            return body;
        }
        if(Method.contains("WebCli")){
            String body = tr069response.Command(Parameters, Option);
            return body;
        }
        if(Method.contains("GetRPCMethods")){
            String body = tr069response.GetRPCMethods();
            return body;
        }
        if(Method.contains("Reboot")){
            String body = tr069response.Reboot();
            return body;
        }
        if(Method.contains("DeleteObject")){
            String body = tr069response.DeleteObject(Parameters);
            return body;
        }
        if(Method.contains("Save")){
            String body = tr069response.SaveConfig();
            return body;
        }
        if(Method.contains("FactoryReset")){
            String body = tr069response.FactoryReset();
            return body;
        }
        return "Wrong RPC_Method";
    }

    public void SaveTask(String SN, String Method,String Parameters,String Optional){
        taskhandler newTasK = new taskhandler();
        newTasK.set_SN(SN);
        newTasK.set_method(Method);
        newTasK.set_parameters(Parameters);
        newTasK.set_optional(Optional);
        taskhandlerRepo.save(newTasK);
        hive_devices current_device = hiveDevicesRepo.gEntityBySerialnum(SN);
        if(current_device.getcwmp_cycle_end()){
            if(!current_device.getmanufacturer().equals("HGU")){
                try {
                    SendUDPRequest(SN);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    //@RequestMapping(value="/TestSendConnectionRequest/{SN}")
    public void SendUDPRequest(@PathVariable String SN) throws IOException{
        
        new Thread(()->{
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            Instant instant = Instant.now();
            long timeStampSeconds = instant.toEpochMilli();

            //String result = "";
            hive_devices current_device = hiveDevicesRepo.gEntityBySerialnum(SN);
            String udp_url = current_device.getudp_con_req_url();
            String[] device_udp_url = udp_url.split(":");
            String host = device_udp_url[0];
            Integer portnum = Integer.parseInt(device_udp_url[1]);

          
            StringBuilder sb = new StringBuilder();

            sb.append("GET http://"+udp_url+"?ts="+timeStampSeconds+"&id="+timeStampSeconds+"&un=&cn=XTG&sig=DEFAULTSIGDEFAULTSIGDEFAULTSIGDEFAULTSIG HTTP/1.1\r\n");
            sb.append("Accept:*/*\r\n");
            sb.append("Accept-Language:zh-cn\r\n");
            sb.append("host:localhost\r\n");
            sb.append("Content-Length:0\r\n");
            
            
            String msg = sb.toString();
            for(int i=0;i<2;i++){
                udp_sender udpclient = null; 
                try {
                    udpclient = new udp_sender();
                } catch (SocketException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (UnknownHostException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    udpclient.sendConnectionRequest(host, portnum, msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println(e);
                }
                udpclient.close();
            }
            
        }).start();
        //result = host+":"+portnum;
        //return result;
    }
}
