package com.acs_tr069.test_tr069.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class udp_sender {
    private DatagramSocket socket;
    private InetAddress address;
    private Integer port;
    private byte[] buf;

    public udp_sender() throws SocketException, UnknownHostException{
        socket = new DatagramSocket(3478); // different in zeep ver, port not specified
    }

    public void sendConnectionRequest(String host, Integer portnum, String msg) throws IOException{
        address = InetAddress.getByName(host);
        port = portnum;
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        
        try {
          socket.send(packet);
        } catch (Exception e) {
          System.out.println(e);
        }
    }
    
    public void close() {
        socket.close();
    }
}
