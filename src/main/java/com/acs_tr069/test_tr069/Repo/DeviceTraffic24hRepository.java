package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.DeviceTraffic24h;

@Repository
public interface DeviceTraffic24hRepository extends CrudRepository<DeviceTraffic24h, Long> {

    @Query(value = "SELECT * FROM device_traffic_24h WHERE serial_num = ?1", nativeQuery = true)
    List<DeviceTraffic24h> findBySerialNumEquals(String serial_num);

}
