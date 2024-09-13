package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.device_traffic_daily;


@Repository
public interface device_traffic_dailyRepository extends CrudRepository<device_traffic_daily, Long>{
    @Query("SELECT d FROM device_traffic_daily d WHERE d.serial_num=?1")
    List<device_traffic_daily> findBySerialNumEquals(String serial_num);
}
