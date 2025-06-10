package com.acs_tr069.test_tr069.zeep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.zeep.entity.AccSessions;

@Repository
public interface AccSessionsRepository extends JpaRepository<AccSessions, Long> {

    // Query for total bandwidth consumption for today
    //@Query(value = "SELECT COALESCE(SUM(incoming_packets + outgoing_packets), 0) " +
    //    "FROM acc_sessions " +
    //    "WHERE SUBSTRING(last_modified, 1, 10) >= TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')",
    //    nativeQuery = true
    //)
    //double totalBandwidthConsumptionToday();
}
