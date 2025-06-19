package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.DeviceTrafficDaily;

@Repository
public interface DeviceTrafficDailyRepository extends CrudRepository<DeviceTrafficDaily, Long>{

    @Query(value = "SELECT * FROM device_traffic_daily WHERE serial_num = ?1", nativeQuery = true)
    List<DeviceTrafficDaily> findBySerialNumEquals(String serial_num);

}
