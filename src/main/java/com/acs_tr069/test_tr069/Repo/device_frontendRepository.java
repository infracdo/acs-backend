package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.device;

@Repository
public interface device_frontendRepository extends CrudRepository<device, Long>{ // superior to zeep ver, will retain
   @Query("SELECT d FROM device d WHERE d.serial_number=?1")
   List<device> findBySerialNum(String serial_number);

   @Query("SELECT d FROM device d WHERE d.serial_number=?1 AND d.parent=\'unassigned\'") // found in hive not in zeep 
   List<device> findBySerialNumOnRogue(String serial_number);

   @Query("SELECT d FROM device d WHERE d.parent=?1")
   List<device> findByGroup(String parent);

   @Query("SELECT d FROM device d WHERE d.serial_number=?1")
   device getBySerialNum(String serial_number);

}
