package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.Device;

@Repository
public interface DeviceFrontendRepository extends CrudRepository<Device, Long> {

   @Query(value = "SELECT * FROM device WHERE serial_number = ?1", nativeQuery = true)
   List<Device> findBySerialNum(String serial_number);

   @Query(value = "SELECT * FROM device WHERE serial_number = ?1 AND parent=\'unassigned\'", nativeQuery = true) // found in zeep, not in hive
   List<Device> findBySerialNumOnRogue(String serial_number);

   @Query(value = "SELECT * FROM device WHERE parent = ?1", nativeQuery = true)
   List<Device> findByGroup(String parent);

   @Query(value = "SELECT * FROM device WHERE serial_number = ?1", nativeQuery = true)
   Device getBySerialNum(String serial_number);

}
