package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.DeviceLogs;

@Repository
public interface DeviceLogsRepository extends CrudRepository<DeviceLogs, Long> {

    @Query(value = "SELECT * FROM device_logs WHERE serial_num = ?1", nativeQuery = true)
    List<DeviceLogs> findBySerialNumEquals(String serial_num);

}
