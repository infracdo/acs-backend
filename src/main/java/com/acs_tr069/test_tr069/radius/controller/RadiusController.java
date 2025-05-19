package com.acs_tr069.test_tr069.radius.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acs_tr069.test_tr069.radius.service.RadiusService;

@RestController
@RequestMapping("/api/radius/")
@CrossOrigin("*")
public class RadiusController {

    private final RadiusService radiusService;

    public RadiusController(RadiusService radiusService) {
        this.radiusService = radiusService;
    }


    // Get number of currently connected users
    @GetMapping("count-currently-connected-users")
    public ResponseEntity<Map<String, Object>> getCountCurrentlyConnectedUsers() {
        long currentlyConnectedUsers = radiusService.getCountCurrentlyConnectedUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("currentlyConnectedUsers", currentlyConnectedUsers);

        return ResponseEntity.ok(response);
    }

    // Get number of currently connected access points
    @GetMapping("count-currently-connected-aps")
    public ResponseEntity<Map<String, Object>> getCountCurrentlyConnectedAPs() {
        long currentlyConnectedAPs = radiusService.getCountCurrentlyConnectedAPs();

        Map<String, Object> response = new HashMap<>();
        response.put("currentlyConnectedAPs", currentlyConnectedAPs);

        return ResponseEntity.ok(response);
    }

    // Get total number of user connections for today
    @GetMapping("total-user-connections-today")
    public ResponseEntity<Map<String, Object>> getTotalUserConnectionsToday() {
        long totalUserConnections = radiusService.getCountTotalUserConnectionsToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalUserConnectionsToday", totalUserConnections);

        return ResponseEntity.ok(response);
    }

    // Get total bandwidth consumption for today
    @GetMapping("total-bandwidth-consumption-today")
    public ResponseEntity<Map<String, Object>> getTotalBandwidthConsumptionToday() {
        String formattedTotalBandwidth = radiusService.getTotalBandwidthConsumptionToday();

        Map<String, Object> response = new HashMap<>();
        response.put("totalBandwidthConsumptionToday", formattedTotalBandwidth);

        return ResponseEntity.ok(response);
    }

    // Get average connection time
    @GetMapping("average-connection-time")
    public ResponseEntity<Map<String, String>> getAvgConnectionTime() {
        String formattedTime = radiusService.getAverageConnectionTime();
        return ResponseEntity.ok(Collections.singletonMap("averageConnectionTime", formattedTime));
    }

    // Get average bandwidth per connection
    @GetMapping("average-bandwidth-per-connection")
    public ResponseEntity<Map<String, Object>> getAverageBandwidthPerConnection() {
        String avgBandwidth = radiusService.getAverageBandwidthPerConnection();

        Map<String, Object> response = new HashMap<>();
        response.put("averageBandwidthPerConnection", avgBandwidth);

        return ResponseEntity.ok(response);
    }

    // Get list of access points
    @GetMapping("access-points")
    public ResponseEntity<Map<String, Object>> getAllAccessPoints() {
        List<String> accessPoints = radiusService.getAllAccessPoints();

        Map<String, Object> response = new HashMap<>();
        response.put("accessPoints", accessPoints);
        return ResponseEntity.ok(response);
    }

    // Get number of currently connected users per access point
    @GetMapping("count-currently-connected-users-per-ap")
    public ResponseEntity<List<Map<String, Object>>> getCountCurrentlyConnectedUsersPerAP() {
        Map<String, Long> currentlyConnectedUsers = radiusService.getCountCurrentlyConnectedUsersPerAP();

        List<Map<String, Object>> response = currentlyConnectedUsers.entrySet().stream()
            .map(entry -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("calledStationId", entry.getKey());
                map.put("currentlyConnectedUsers", entry.getValue());
                return map;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Get list of currently connected users per access point
    @GetMapping("currently-connected-users-per-ap")
    public ResponseEntity<List<Map<String, Object>>> getCurrentlyConnectedUsersPerAP() {
        List<Map<String, Object>> usersPerAP = radiusService.getCurrentlyConnectedUsersPerAP()
            .entrySet()
            .stream()
            .map(entry -> {
                Map<String, Object> apEntry = new LinkedHashMap<>();
                apEntry.put("called_station_id", entry.getKey());
                apEntry.put("currently_connected_users", entry.getValue());
                return apEntry;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(usersPerAP);
    }
}
