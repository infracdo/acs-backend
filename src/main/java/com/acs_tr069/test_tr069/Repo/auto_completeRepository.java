package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.auto_complete;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface auto_completeRepository extends CrudRepository<auto_complete, Long> {
    @Query("SELECT d FROM auto_complete d WHERE d.device_model=?1")
    List<auto_complete> findByDeviceModel(String device_model);
}