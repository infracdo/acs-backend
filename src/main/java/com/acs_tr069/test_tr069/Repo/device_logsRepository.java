package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.device_logs;


@Repository
public interface device_logsRepository extends CrudRepository<device_logs, Long>{
    @Query("SELECT d FROM device_logs d WHERE d.serial_num=?1")
    List<device_logs> findBySerialNumEquals(String serial_num);
}
