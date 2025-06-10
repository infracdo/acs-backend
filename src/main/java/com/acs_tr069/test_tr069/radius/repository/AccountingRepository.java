package com.acs_tr069.test_tr069.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.radius.entity.Accounting;

@Repository
public interface AccountingRepository extends JpaRepository<Accounting, String> {

    // Query to get number of currently connected users
    // @Query(value = "SELECT COUNT(DISTINCT calling_station_id) " +
    //     "FROM accounting " +
    //     "WHERE acctstatustype = 'Start' " +
    //     "AND time_stamp >= :startOfDay " +
    //     "AND time_stamp < :endOfDay",
    //     nativeQuery = true)
    // long countCurrentlyConnectedUsers(
    //     @Param("startOfDay") long startOfDay,
    //     @Param("endOfDay") long endOfDay
    // );
    // NOTE: since we couldn't just query directly the users with acctstatustype of 'Start' as the table only inserts data (no updating nor removing a data),
    // we just have to make sure that the certain 'Start' session doesn't have a corresponding 'Stop' status type
    @Query(value = "SELECT COUNT(DISTINCT a1.calling_station_id) " +
        "FROM accounting a1 " +
        "WHERE a1.acctstatustype = 'Start' " +
        // "AND a1.time_stamp >= :startOfDay " +    // uncomment if you need to get the currently connected users for today
        // "AND a1.time_stamp < :endOfDay " +       // uncomment if you need to get the currently connected users for today
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM accounting a2 " +
        "   WHERE a2.acctstatustype = 'Stop' " +
        "   AND a2.calling_station_id = a1.calling_station_id " +
        "   AND a2.time_stamp >= a1.time_stamp " +
        // "   AND a2.time_stamp < :endOfDay" +     // uncomment if you need to get the currently connected users for today
        ")",
        nativeQuery = true)
    // long countCurrentlyConnectedUsers(           // uncomment if you need to get the currently connected users for today
    //     @Param("startOfDay") long startOfDay,
    //     @Param("endOfDay") long endOfDay
    // );
    long countCurrentlyConnectedUsers();
    
    // Query to get the number of currently connected access points
    // @Query(value = "SELECT COUNT(DISTINCT called_station_id) " +
    //     "FROM accounting " +
    //     "WHERE acctstatustype = 'Start' " +
    //     "AND time_stamp >= :startOfDay " +
    //     "AND time_stamp < :endOfDay",
    //     nativeQuery = true)
    // long countCurrentlyConnectedAPs(
    //     @Param("startOfDay") long startOfDay,
    //     @Param("endOfDay") long endOfDay
    // );
    // NOTE: since we couldn't just query directly the access points with acctstatustype of 'Start' as the table only inserts data (no updating nor removing a data),
    // we just have to make sure that the certain 'Start' session doesn't have a corresponding 'Stop' status type
    @Query(value = "SELECT COUNT(DISTINCT called_station_id) " +
        "FROM accounting a1 " +
        "WHERE a1.acctstatustype = 'Start' " +
        // "AND time_stamp >= :startOfDay " +
        // "AND time_stamp < :endOfDay",
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM accounting a2 " +
        "   WHERE a2.acctstatustype = 'Stop' " +
        "   AND a2.called_station_id = a1.called_station_id " +
        "   AND a2.time_stamp >= a1.time_stamp" +
        // "   AND a2.time_stamp < :endOfDay" +     // uncomment if you need to get the currently connected users for today
        ")",
        nativeQuery = true)
    long countCurrentlyConnectedAPs();

    // Query to get the total user connections for today
    @Query(value = "SELECT COUNT(DISTINCT calling_station_id) " +
        "FROM accounting " +
        "WHERE time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long countTotalUserConnectionsToday(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get the total bandwidth consumption for today
    @Query(value = "SELECT COALESCE(SUM(acctinputoctets + acctoutputoctets), 0) " +
        "FROM accounting " +
        "WHERE time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long totalBandwidthConsumptionToday(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get average connection time
    @Query(value = "SELECT AVG(acctsessiontime) " +
        "FROM accounting " +
        "WHERE acctstatustype = 'Stop' " +
        "AND acctsessiontime > 0",
    nativeQuery = true)
    Double findAverageConnectionTime();

    // Query to get the average bandwidth per connection
    @Query(value = "SELECT AVG((a.acctinputoctets + a.acctoutputoctets) / a.acctsessiontime) " +
        "FROM accounting a " +
        "WHERE a.acctstatustype = 'Stop' " +
        "AND a.acctsessiontime > 0",
        // "AND a.time_stamp >= :startTime " +  // Uncomment if need to get the average bandwidth per connection for today
        // "AND a.time_stamp < :endTime",       // Uncomment if need to get the average bandwidth per connection for today
    nativeQuery = true)
    Double findAverageBandwidthPerConnection(
        // @Param("startTime") long startTime,  // Uncomment if need to get the average bandwidth per connection for today
        // @Param("endTime") long endTime       // Uncomment if need to get the average bandwidth per connection for today
    );

    // Query to get the list of access points
    @Query(value = "SELECT DISTINCT called_station_id " +
        "FROM accounting ",
        nativeQuery = true)
    List<String> findAllAccessPoints();

    // Query to get number of currently connected users per access point
    // @Query(value = "SELECT called_station_id, COUNT(DISTINCT calling_station_id) as user_count " +
    //     "FROM accounting " +
    //     "WHERE acctstatustype = 'Start' " +
    //     "AND time_stamp >= :startOfDay " +
    //     "AND time_stamp < :endOfDay " +
    //     "GROUP BY called_station_id",
    //     nativeQuery = true)
    // List<Object[]> countCurrentlyConnectedUsersPerAP(
    //     @Param("startOfDay") long startOfDay,
    //     @Param("endOfDay") long endOfDay
    // );
    // NOTE: since we couldn't just query directly the users with acctstatustype of 'Start' as the table only inserts data (no updating nor removing a data),
    // we just have to make sure that the certain 'Start' session doesn't have a corresponding 'Stop' status type
    @Query(value = "SELECT a1.called_station_id, COUNT(DISTINCT a1.calling_station_id) as user_count " +
        "FROM accounting a1 " +
        "WHERE a1.acctstatustype = 'Start' " +
        // "AND a1.time_stamp >= :startOfDay " +   // Uncomment if you need to get the currently connected users for today
        // "AND a1.time_stamp < :endOfDay " +      // Uncomment if you need to get the currently connected users for today
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM accounting a2 " +
        "   WHERE a2.acctstatustype = 'Stop' " +
        "   AND a2.calling_station_id = a1.calling_station_id " +
        "   AND a2.time_stamp >= a1.time_stamp " +
        // "   AND a2.time_stamp < :endOfDay " +    // Uncomment if you need to get the currently connected users for today
        ")" +
        "GROUP BY a1.called_station_id",
        nativeQuery = true)
    List<Object[]> countCurrentlyConnectedUsersPerAP();

    // Query to get list of currently connected users per access point
    // @Query(value = "SELECT called_station_id, username, acctinputoctets, acctoutputoctets, nasport, calling_station_id, time_stamp " +
    //     "FROM accounting " +
    //     "WHERE acctstatustype = 'Start' " +
    //     "AND time_stamp >= :startOfDay " +
    //     "AND time_stamp < :endOfDay " +
    //     "ORDER BY called_station_id, time_stamp DESC",
    //     nativeQuery = true)
    // List<Object[]> findCurrentlyConnectedUsersPerAP(
    //     @Param("startOfDay") long startOfDay,
    //     @Param("endOfDay") long endOfDay
    // );
    // NOTE: since we couldn't just query directly the users with acctstatustype of 'Start' as the table only inserts data (no updating nor removing a data),
    // we just have to make sure that the certain 'Start' session doesn't have a corresponding 'Stop' status type
    @Query(value = "SELECT a1.called_station_id, a1.username, a1.acctinputoctets, a1.acctoutputoctets, a1.nasport, a1.calling_station_id, a1.time_stamp " +
        "FROM accounting a1 " +
        "WHERE a1.acctstatustype = 'Start' " +
        // "AND a1.time_stamp >= :startOfDay " +    // Uncomment if need to get currently connected users for today
        // "AND a1.time_stamp < :endOfDay " +       // Uncomment if need to get currently connected users for today
        "AND NOT EXISTS (" +
        "   SELECT 1 FROM accounting a2 " +
        "   WHERE a2.acctstatustype = 'Stop' " +
        "   AND a2.calling_station_id = a1.calling_station_id " +
        "   AND a2.time_stamp >= a1.time_stamp " +
        // "   AND a2.time_stamp < :endOfDay " +    // Uncomment if you need to get currently connected users for today
        ")" +
        "ORDER BY a1.called_station_id, a1.time_stamp DESC",
        nativeQuery = true)
    List<Object[]> findCurrentlyConnectedUsersPerAP(
        // @Param("startOfDay") long startOfDay,    // Uncomment if need to get currently connected users for today
        // @Param("endOfDay") long endOfDay         // Uncomment if need to get currently connected users for today
    );
}
