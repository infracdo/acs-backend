package com.acs_tr069.test_tr069.zeep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.zeep.entity.AccTransactions;

@Repository
public interface AccTransactionsRepository extends JpaRepository<AccTransactions, Long> {

    // Query for current number of connected users
    @Query("SELECT COUNT(a) FROM AccTransactions a WHERE a.stage = 'authenticated'")
    long countConnectedUsers();

    // Query for current number of connected access points (APs)
    @Query("SELECT COUNT(DISTINCT a.apMacAddress) FROM AccTransactions a WHERE a.stage = 'authenticated'")
    long countConnectedAPs();

    // Query for total user connections for today
    @Query(value = "SELECT COUNT(*) " +
        "FROM acc_transactions " +
        "WHERE SUBSTRING(last_active, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')",
        nativeQuery = true)
    long totalUserConnectionsToday();

    // Query for number of currently connected users per access point (AP)
    @Query("SELECT a.apMacAddress, COUNT(a) FROM AccTransactions a WHERE a.stage = 'authenticated' GROUP BY a.apMacAddress")
    List<Object[]> countConnectedUsersPerAP();

    // Query for list of currently connected users per access point (AP)
    // NOTE: the account_Number field in the database has double quotes, either modify the column in db or keep this
    @Query(value = "SELECT apmac, \"account_Number\", package, mac, device, ip, ssid, last_active, total_incoming_packets, total_outgoing_packets " +
        "FROM acc_transactions " +
        "WHERE stage = 'authenticated' " +
        "ORDER BY apmac",
        nativeQuery = true)
    List<Object[]> findCurrentConnectedUsersPerAP();

    // Query for list of currently connected access points (AP)
    @Query("SELECT DISTINCT a.apMacAddress FROM AccTransactions a WHERE a.stage = 'authenticated'")
    List<String> findCurrentConnectedAPs();
    
}
