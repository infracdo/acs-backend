package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.WebcliResponseLog;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebcliResponseLogRepository extends CrudRepository<WebcliResponseLog, Long> {

    @Query(value = "SELECT * FROM webcli_response_log WHERE device_sn = ?1", nativeQuery = true)
    List<WebcliResponseLog> findBySerialNumEquals(String device_sn);

    @Query(value = "SELECT * FROM webcli_response_log WHERE device_sn = ?1", nativeQuery = true)
    WebcliResponseLog getBySerialNumEquals(String device_sn);

    @Query(value = "SELECT * FROM webcli_response_log WHERE id = ?1", nativeQuery = true)
    WebcliResponseLog getByID(Long id);

}
