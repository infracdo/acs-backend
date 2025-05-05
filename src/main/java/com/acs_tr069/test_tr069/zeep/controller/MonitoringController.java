package com.acs_tr069.test_tr069.zeep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acs_tr069.test_tr069.zeep.service.MonitoringService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    // Get current number of connected users
    @GetMapping("/count-connected-users")
    public ResponseEntity<Map<String, Object>> getCountConnectedUsers() {
        long countConnectedUsers = monitoringService.getCountConnectedUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("connectedUsers", countConnectedUsers);

        return ResponseEntity.ok(response);
    }

    // Get current number of connected access points (APs)
    @GetMapping("/count-connected-aps")
    public ResponseEntity<Map<String, Object>> getCountConnectedAPs() {
        long countConnectedAPs = monitoringService.getCountConnectedAPs();

        Map<String, Object> response = new HashMap<>();
        response.put("connectedAPs", countConnectedAPs);

        return ResponseEntity.ok(response);
    }

    // Get total user connections for today
    @GetMapping("/total-user-connections-today")
    public ResponseEntity<Map<String, Object>> getTotalUserConnectionsToday() {
        long totalConnectedUsers = monitoringService.getTotalUserConnectionsToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUserConnectionsToday", totalConnectedUsers);

        return ResponseEntity.ok(response);
    }

    // Get total bandwidth consumption for today
    @GetMapping("/total-bandwidth-consumption-today")
    public ResponseEntity<Map<String, Object>> getTotalBandwidthConsumptionToday() {
        double totalBandwidthConsumptionToday = monitoringService.getTotalBandwidthConsumptionToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalBandwidthConsumptionToday", totalBandwidthConsumptionToday);

        return ResponseEntity.ok(response);
    }

    // Get average bandwidth per connection
    @GetMapping("/average-bandwidth-per-connection")
    public ResponseEntity<Map<String, Object>> getAvgBandwidthPerConnection() {
        double avgBandwidthPerConnection = monitoringService.getAvgBandwidthPerConnection();

        Map<String, Object> response = new HashMap<>();
        response.put("averageBandwidthPerConnection", avgBandwidthPerConnection);

        return ResponseEntity.ok(response);
    }

    // Get number of currently connected users per access point (AP)
    @GetMapping("/count-connected-users-per-ap")
    public ResponseEntity<List<Map<String, Object>>> getCountConnectedUsersPerAp() {
        Map<String, Long> currentConnectedUsersPerApCount = monitoringService.getCountConnectedUsersPerAP();
        List<Map<String, Object>> response = new ArrayList<>();

        currentConnectedUsersPerApCount.forEach((apMac, count) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("apMacAddress", apMac);
            entry.put("userCount", count);
            response.add(entry);
        });
        return ResponseEntity.ok(response);
    }

    // Get list of currently connected users per access point (AP)
    @GetMapping("/current-connected-users-per-ap")
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

    // Get list of currently connected access points (AP)
    @GetMapping("/current-connected-aps")
    public ResponseEntity<Map<String, Object>> getCurrentConnectedAPs() {
        List<String> connectedAPs = monitoringService.getCurrentConnectedAPs();

        Map<String, Object> response = new HashMap<>();
        if (connectedAPs == null || connectedAPs.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No currently connected access points found.");
            response.put("data", null);
            return ResponseEntity.status(404).body(response);
        }

        response.put("status", "success");
        response.put("message", "Currently connected access points retrieved successfully.");
        response.put("data", connectedAPs);
        return ResponseEntity.ok(response);
    }
}
