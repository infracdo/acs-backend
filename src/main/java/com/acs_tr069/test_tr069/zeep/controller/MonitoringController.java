package com.acs_tr069.test_tr069.zeep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acs_tr069.test_tr069.zeep.service.MonitoringService;

@RestController
@RequestMapping(path = "/api/monitoring/")
@CrossOrigin("*")
public class MonitoringController {
    
    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/count-current-connected-users") // Get current number of connected users
    public ResponseEntity<Map<String, Object>> getCountCurrentConnectedUsers() {
        long countConnectedUsers = monitoringService.getCountCurrentConnectedUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("connectedUsers", countConnectedUsers);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-current-connected-aps") // Get current number of connected access points (APs)
    public ResponseEntity<Map<String, Object>> getCountCurrentConnectedAPs() {
        long countConnectedAPs = monitoringService.getCountCurrentConnectedAPs();

        Map<String, Object> response = new HashMap<>();
        response.put("connectedAPs", countConnectedAPs);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-user-connections-today") // Get total user connections for today
    public ResponseEntity<Map<String, Object>> getTotalUserConnectionsToday() {
        long totalConnectedUsers = monitoringService.getTotalUserConnectionsToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUserConnectionsToday", totalConnectedUsers);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-bandwidth-consumption-today") // Get total bandwidth consumption for today
    public ResponseEntity<Map<String, Object>> getTotalBandwidthConsumptionToday() {
        double totalBandwidthConsumptionToday = monitoringService.getTotalBandwidthConsumptionToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalBandwidthConsumptionToday", totalBandwidthConsumptionToday);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/avg-connection-time") // Get average connection time
    public ResponseEntity<Map<String, Object>> getAvgConnectionTime() {
        double averageConnectionTime = monitoringService.getAvgConnectionTime();

        Map<String, Object> response = new HashMap<>();
        response.put("averageConnectionTime", averageConnectionTime);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/average-bandwidth-per-connection") // Get average bandwidth per connection
    public ResponseEntity<Map<String, Object>> getAvgBandwidthPerConnection() {
        double avgBandwidthPerConnection = monitoringService.getAvgBandwidthPerConnection();

        Map<String, Object> response = new HashMap<>();
        response.put("averageBandwidthPerConnection", avgBandwidthPerConnection);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-current-connected-users-per-ap") // Get number of currently connected users per ap
    public ResponseEntity<List<Map<String, Object>>> getCountCurrentConnectedUsersPerAp() {
        Map<String, Long> counts = monitoringService.getCountCurrentConnectedUsersPerAP();
        
        List<Map<String, Object>> response = counts.entrySet().stream()
            .map(entry -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("apMacAddress", entry.getKey());
                map.put("userCount", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-connected-users-per-ap") // Get list of currently connected users per ap
    public ResponseEntity<List<Map<String, Object>>> getCurrentConnectedUsersPerAP() {
        Map<String, List<Map<String, Object>>> currentConnectedUsersPerAP = monitoringService.getCurrentConnectedUsersPerAP();
        List<Map<String, Object>> response = new ArrayList<>();

        currentConnectedUsersPerAP.forEach((apMac, users) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("apMacAddress", apMac);
            entry.put("connectedUsers", users);
            response.add(entry);
        });

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-connected-aps") // Get list of currently connected ap
    public ResponseEntity<Map<String, Object>> getCurrentConnectedAPs() {
        List<String> connectedAPs = monitoringService.getCurrentConnectedAPs();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Currently connected access points retrieved successfully.");
        response.put("data", connectedAPs);
        return ResponseEntity.ok(response);
    }
    
}
