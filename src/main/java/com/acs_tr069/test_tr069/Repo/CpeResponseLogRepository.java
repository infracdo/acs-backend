package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import com.acs_tr069.test_tr069.Entity.CpeResponseLog;

import java.util.List;

@Repository
public interface CpeResponseLogRepository extends CrudRepository<CpeResponseLog, Long> {

    @Query(value = "SELECT * FROM cpe_response_log WHERE serial_num = ?1", nativeQuery = true)
    List<CpeResponseLog> findBySerialNumEquals(String serial_num);

    @Query(value = "SELECT * FROM cpe_response_log WHERE serial_num = ?1", nativeQuery = true)
    CpeResponseLog getBySerialNumEquals(String serial_num);

}
