package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import com.acs_tr069.test_tr069.Entity.cpe_response_log;

import java.util.List;

@Repository
public interface cpe_response_logRepository  extends CrudRepository<cpe_response_log, Long> { // same with zeep ver, will retain
    @Query("SELECT d FROM cpe_response_log d WHERE d.serial_num=?1")
    List<cpe_response_log> findBySerialNumEquals(String serial_num);
    @Query("SELECT d FROM cpe_response_log d WHERE d.serial_num=?1")
    cpe_response_log getBySerialNumEquals(String serial_num);
}
