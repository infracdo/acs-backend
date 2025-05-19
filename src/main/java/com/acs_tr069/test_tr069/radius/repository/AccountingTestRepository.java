package com.acs_tr069.test_tr069.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.radius.entity.AccountingTest;

@Repository
public interface AccountingTestRepository extends JpaRepository<AccountingTest, String> {

    // Query to get currently connected users
    @Query(value = "SELECT COUNT(*) " +
        "FROM accounting_test " +
        "WHERE acctstatustype = 'Start' " +
        "AND time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long countCurrentlyConnectedUsers(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get the number of currently connected access points
    @Query(value = "SELECT COUNT(DISTINCT called_station_id) " +
        "FROM accounting_test " +
        "WHERE acctstatustype = 'Start' " +
        "AND time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long countCurrentlyConnectedAPs(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get the total user connections for today
    @Query(value = "SELECT COUNT(*) " +
        "FROM accounting_test " +
        "WHERE time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long countTotalUserConnectionsToday(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get the total bandwidth consumption for today
    @Query(value = "SELECT COALESCE(SUM(acctinputoctets + acctoutputoctets), 0) " +
        "FROM accounting_test " +
        "WHERE time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay",
        nativeQuery = true)
    long totalBandwidthConsumptionToday(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get average connection time
    @Query(value = "SELECT AVG(acctsessiontime) " +
        "FROM accounting_test " +
        "WHERE acctstatustype = 'Stop' " +
        "AND acctsessiontime > 0",
    nativeQuery = true)
    Double findAverageConnectionTime();

    // Query to get the list of access points
    @Query(value = "SELECT DISTINCT called_station_id " +
        "FROM accounting_test ",
        nativeQuery = true)
    List<String> findAllAccessPoints();

    // Query to get currently connected users per access point
    @Query(value = "SELECT called_station_id, COUNT(*) as user_count " +
        "FROM accounting_test " +
        "WHERE acctstatustype = 'Start' " +
        "AND time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay " +
        "GROUP BY called_station_id",
        nativeQuery = true)
    List<Object[]> countCurrentlyConnectedUsersPerAP(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

    // Query to get list of currently connected users per access point
    @Query(value = "SELECT called_station_id, username, acctinputoctets, acctoutputoctets, nasport, calling_station_id, time_stamp " +
        "FROM accounting_test " +
        "WHERE acctstatustype = 'Start' " +
        "AND time_stamp >= :startOfDay " +
        "AND time_stamp < :endOfDay " +
        "ORDER BY called_station_id, time_stamp DESC",
        nativeQuery = true)
    List<Object[]> findCurrentlyConnectedUsersPerAP(
        @Param("startOfDay") long startOfDay,
        @Param("endOfDay") long endOfDay
    );

}
