package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.acs_tr069.test_tr069.Entity.devices;

public interface devicesRepository extends CrudRepository<devices, Long>{
   @Query(value = "SELECT * FROM devices WHERE serial_num = ?1", nativeQuery = true)
   List<devices> findBySerialNum(String serial_num);

   @Query(value = "SELECT group_path FROM devices WHERE serial_num = ?1", nativeQuery = true)
   List<devices> getGroupPathBySerialNumEquals(String serial_num); // found in zeep not in hive

   @Query(value = "SELECT * FROM devices WHERE serial_num = ?1", nativeQuery = true)
   devices getBySerialNum(String serial_num);

   @Modifying
   @Transactional
   @Query(value = "UPDATE devices SET second_wan_mac = ?1 WHERE serial_num = ?2", nativeQuery = true)
   void updateSecondMacAddressBySerialNum(String second_wan_mac, String serial_num); // found in hive not in zeep
}
