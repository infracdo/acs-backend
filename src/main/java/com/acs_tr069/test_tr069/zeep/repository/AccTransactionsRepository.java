package com.acs_tr069.test_tr069.zeep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.zeep.entity.AccTransactions;

@Repository
public interface AccTransactionsRepository extends JpaRepository<AccTransactions, Long> {

    // Query for current number of connected users
    // @Query("SELECT COUNT(a) FROM AccTransactions a WHERE a.stage = 'authenticated'")
    @Query(value = "SELECT COUNT(*) " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') " +
        "AND stage = 'authenticated'",
        nativeQuery = true)
    long countCurrentConnectedUsers();

    // Query for current number of connected access points (APs)
    // @Query("SELECT COUNT(DISTINCT a.apMacAddress) FROM AccTransactions a WHERE a.stage = 'authenticated'")
    @Query(value = "SELECT COUNT(DISTINCT apmac) " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') " +
        "AND stage = 'authenticated'",
        nativeQuery = true)
    long countCurrentConnectedAPs();

    // Query for total user connections for today
    @Query(value = "SELECT COUNT(*) " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')",
        nativeQuery = true)
    long totalUserConnectionsToday();

    // Query for total bandwidth consumption for today
    @Query(value = "SELECT COALESCE(SUM(total_incoming_packets + total_outgoing_packets), 0) " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')",
        nativeQuery = true)
    Long totalBandwidthConsumptionToday();

    // Query for average connection time
    @Query(value = "SELECT COALESCE(AVG(" +
        "EXTRACT(SECOND FROM diff) + " +
        "EXTRACT(MINUTE FROM diff) * 60 + " +
        "EXTRACT(HOUR FROM diff) * 3600" +
        "), 0) " +
        "FROM (" +
        "   SELECT " +
        "       created_on, " +
        "       (TO_TIMESTAMP(SUBSTRING(last_active, 1, 19), 'YYYY-MM-DD HH24:MI:SS') - created_on) AS diff " +
        "   FROM acc_transactions " +
        "   WHERE SUBSTRING(last_active, 1, 10) = TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')" +
        ") AS time_diffs",
        nativeQuery = true)
    Double avgConnectionTime();

    // Query for number of currently connected users per access point (AP)
    // @Query("SELECT a.apMacAddress, COUNT(a) FROM AccTransactions a WHERE a.stage = 'authenticated' GROUP BY a.apMacAddress")
    @Query(value = "SELECT apmac, COUNT(*) as user_count " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) = TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') " +
        "AND stage = 'authenticated' " +
        "GROUP BY apmac",
        nativeQuery = true)
    List<Object[]> countCurrentConnectedUsersPerAP();

    // Query for list of currently connected users per access point (AP)
    @Query(value = "SELECT apmac, \"account_Number\", package, mac, device, ip, ssid, last_active, total_incoming_packets, total_outgoing_packets " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) = TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') " +
        "AND stage = 'authenticated' " +
        "ORDER BY apmac, last_active DESC",
        nativeQuery = true)
    List<Object[]> findCurrentConnectedUsersPerAP();

    // Query for list of currently connected access points (AP)
    // @Query("SELECT DISTINCT a.apMacAddress FROM AccTransactions a WHERE a.stage = 'authenticated'")
    @Query(value = "SELECT DISTINCT apmac " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) = TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD') " +
        "AND stage = 'authenticated'",
        nativeQuery = true)
    List<String> findCurrentConnectedAPs();
    
}
