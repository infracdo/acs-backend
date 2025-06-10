package com.acs_tr069.test_tr069.zeep.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.acs_tr069.test_tr069.zeep.repository.AccSessionsRepository;
import com.acs_tr069.test_tr069.zeep.repository.AccTransactionsRepository;

@Service
public class MonitoringService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    private final AccTransactionsRepository accTransactionsRepository;
    private final AccSessionsRepository accSessionsRepository;

    public MonitoringService(AccTransactionsRepository accTransactionsRepository,
                            AccSessionsRepository accSessionsRepository) {
        
        this.accTransactionsRepository = accTransactionsRepository;
        this.accSessionsRepository = accSessionsRepository;
    }

    // Return current number of connected users
    public long getCountCurrentConnectedUsers() {
        return accTransactionsRepository.countCurrentConnectedUsers();
    }

    // Return current number of connected access points (APs)
    public Long getCountCurrentConnectedAPs() {
        return accTransactionsRepository.countCurrentConnectedAPs();
    }

    // Return total user connections for today
    public Long getTotalUserConnectionsToday() {
        return accTransactionsRepository.totalUserConnectionsToday();
    }

    // Return total bandwidth consumption for today
    public double getTotalBandwidthConsumptionToday() {
        Long totalBandwidthConsumptionToday = accTransactionsRepository.totalBandwidthConsumptionToday();

        // return accSessionsRepository.totalBandwidthConsumptionToday();
        return Math.round((totalBandwidthConsumptionToday / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    // Return average connection time
    public double getAvgConnectionTime() {
        try {
            Double averageSeconds = accTransactionsRepository.avgConnectionTime();
            if (averageSeconds == null || averageSeconds == 0) {
                return 0.0;
            }

            double averageMinutes = averageSeconds / 60.0;

            return Math.round(averageMinutes * 10.0) / 10.0;
        } catch (Exception e) {
            log.error("Error calculating average connection time: ", e);
            return 0.0; // Fallback value
        }
    }

    // Return average bandwidth per connection
    public double getAvgBandwidthPerConnection() {
        // double totalBandwidth = accSessionsRepository.totalBandwidthConsumptionToday();
        long totalBandwidthBytes = accTransactionsRepository.totalBandwidthConsumptionToday();
        long totalConnections = accTransactionsRepository.totalUserConnectionsToday();

        if (totalConnections == 0) {
            return 0.0;
        }

        double totalBandwidthMB = totalBandwidthBytes / (1024.0 * 1024.0);
        double averageMB = totalBandwidthMB / totalConnections;
        
        return Math.round(averageMB * 100.0) / 100.0;
    }

    // Return number of currently connected users per access point (AP)
    public Map<String, Long> getCountCurrentConnectedUsersPerAP() {
        List<Object[]> results = accTransactionsRepository.countCurrentConnectedUsersPerAP();
        Map<String, Long> response = new HashMap<>();

        for (Object[] row : results) {
            String apMac = (String) row[0];
            Number count = (Number) row[1];
            response.put(apMac, count.longValue());
        }

        return response;
    }

    // Return list of currently connected users per access point (AP)
    public Map<String, List<Map<String, Object>>> getCurrentConnectedUsersPerAP() {
        List<Object[]> results = accTransactionsRepository.findCurrentConnectedUsersPerAP();
        Map<String, List<Map<String, Object>>> response = new LinkedHashMap<>();

        for (Object[] row : results) {
            String apMac = (String) row[0];
            long totalBytes  = ((Number) row[8]).longValue() + ((Number) row[9]).longValue();
            double bandwidthMB = totalBytes / (1024.0 * 1024.0);
            
            Map<String, Object> user = new LinkedHashMap<>();
            user.put("accountNumber", row[1] != null ? row[1].toString() : "");
            user.put("package", row[2] != null ? row[2].toString() : "");
            user.put("macAddress", row[3] != null ? row[3].toString() : "");
            user.put("device", row[4] != null ? row[4].toString() : "");
            user.put("ipAddress", row[5] != null ? row[5].toString() : "");
            user.put("ssid", row[6] != null ? row[6].toString() : "");
            user.put("lastActive", row[7] != null ? row[7].toString() : "");
            user.put("bandwidthMB", Math.round(bandwidthMB * 100.0) / 100.0);
            // user.put("totalIncomingPackets", row[8] != null ? row[8].toString() : "");
            // user.put("totalOutgoingPackets", row[9] != null ? row[9].toString() : "");

            response.computeIfAbsent(apMac, k -> new ArrayList<>()).add(user);
        }

        return response;
    }

    // Return list of current connected access points (AP)
    public List<String> getCurrentConnectedAPs() {
        return accTransactionsRepository.findCurrentConnectedAPs();
    }
}
