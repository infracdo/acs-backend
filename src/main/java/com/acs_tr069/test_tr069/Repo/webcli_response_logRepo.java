package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.webcli_response_log;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface webcli_response_logRepo extends CrudRepository<webcli_response_log, Long> {
    @Query("SELECT d FROM webcli_response_log d WHERE d.device_sn=?1")
    List<webcli_response_log> findBySerialNumEquals(String device_sn);

    @Query("SELECT d FROM webcli_response_log d WHERE d.device_sn=?1")
    webcli_response_log getBySerialNumEquals(String device_sn);

    @Query("SELECT d FROM webcli_response_log d WHERE d.id=?1")
    webcli_response_log getByID(Long id);
}
