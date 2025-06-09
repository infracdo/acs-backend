package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.acs_tr069.test_tr069.Entity.zeep_devices;

public interface zeepDevicesRepository extends CrudRepository<zeep_devices, Long>{
   @Query("SELECT d FROM zeep_devices d WHERE d.serial_num=?1")
   List<zeep_devices> findBySerialNumEquals(String serial_num);

   @Query("SELECT d.group_path FROM zeep_devices d WHERE d.serial_num=?1")
   List<zeep_devices> getGroupPathBySerialNumEquals(String serial_num); // found in zeep not in hive

   @Query("SELECT d FROM zeep_devices d WHERE d.serial_num=?1")
   zeep_devices gEntityBySerialnum(String serial_num);
}
