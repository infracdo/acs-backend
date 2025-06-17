package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.device;

@Repository
public interface device_frontendRepository extends CrudRepository<device, Long>{ // superior to zeep ver, will retain
   @Query(value = "SELECT * FROM device WHERE serial_number = ?1", nativeQuery = true)
   List<device> findBySerialNum(String serial_number);

   @Query(value = "SELECT * FROM device WHERE serial_number = ?1 AND parent=\'unassigned\'", nativeQuery = true) // found in hive not in zeep 
   List<device> findBySerialNumOnRogue(String serial_number);

   @Query(value = "SELECT * FROM device WHERE parent = ?1", nativeQuery = true)
   List<device> findByGroup(String parent);

   @Query(value = "SELECT * FROM device WHERE serial_number = ?1", nativeQuery = true)
   device getBySerialNum(String serial_number);
}
