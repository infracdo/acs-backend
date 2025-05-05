package com.acs_tr069.test_tr069.zeep.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.acs_tr069.test_tr069.zeep.repository.AccSessionsRepository;
import com.acs_tr069.test_tr069.zeep.repository.AccTransactionsRepository;

@Service
public class MonitoringService {

    private final AccTransactionsRepository accTransactionsRepository;
    private final AccSessionsRepository accSessionsRepository;

    public MonitoringService(AccTransactionsRepository accTransactionsRepository,
                            AccSessionsRepository accSessionsRepository) {
        
        this.accTransactionsRepository = accTransactionsRepository;
        this.accSessionsRepository = accSessionsRepository;
    }

    // Return current number of connected users
    public long getCountConnectedUsers() {
        return accTransactionsRepository.countConnectedUsers();
    }

    // Return current number of connected access points (APs)
    public Long getCountConnectedAPs() {
        return accTransactionsRepository.countConnectedAPs();
    }

    // Return total user connections for today
    public Long getTotalUserConnectionsToday() {
        return accTransactionsRepository.totalUserConnectionsToday();
    }

    // Return total bandwidth consumption for today
    public double getTotalBandwidthConsumptionToday() {
        return accSessionsRepository.totalBandwidthConsumptionToday();
    }

    // Return average bandwidth per connection
    public double getAvgBandwidthPerConnection() {
        double totalBandwidth = accSessionsRepository.totalBandwidthConsumptionToday();
        long totalConnections = accTransactionsRepository.totalUserConnectionsToday();

        if (totalConnections == 0) {
            return 0.0;
        }
        
        return totalBandwidth / totalConnections;
    }

    // Return number of currently connected users per access point (AP)
    public Map<String, Long> getCountConnectedUsersPerAP() {
        List<Object[]> results = accTransactionsRepository.countConnectedUsersPerAP();

        Map<String, Long> response = new HashMap<>();
        for (Object[] row : results) {
            response.put((String) row[0], (Long) row[1]);
        }
        return response;
    }

    // Return list of currently connected users per access point (AP)
    public Map<String, List<Map<String, Object>>> getCurrentConnectedUsersPerAP() {
        List<Object[]> results = accTransactionsRepository.findCurrentConnectedUsersPerAP();

        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        for (Object[] row : results) {
            String apMac = (String) row[0];
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("accountNumber", row[1] != null ? row[1].toString() : "");
            userMap.put("package", row[2] != null ? row[2].toString() : "");
            userMap.put("macAddress", row[3] != null ? row[3].toString() : "");
            userMap.put("device", row[4] != null ? row[4].toString() : "");
            userMap.put("ipAddress", row[5] != null ? row[5].toString() : "");
            userMap.put("ssid", row[6] != null ? row[6].toString() : "");
            userMap.put("lastActive", row[7] != null ? row[7].toString() : "");
            userMap.put("totalIncomingPackets", row[8] != null ? row[8].toString() : "");
            userMap.put("totalOutgoingPackets", row[9] != null ? row[9].toString() : "");

            response.computeIfAbsent(apMac, k -> new ArrayList<>()).add(userMap);
        }

        return response;
    }

    // Return list of current connected access points (AP)
    public List<String> getCurrentConnectedAPs() {
        return accTransactionsRepository.findCurrentConnectedAPs();
    }
}
