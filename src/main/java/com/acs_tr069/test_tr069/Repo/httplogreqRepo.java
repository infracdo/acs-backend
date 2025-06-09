package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.httprequestlog;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface httplogreqRepo extends CrudRepository<httprequestlog, Long>{ // same with zeep ver, will retain
    @Query("SELECT d FROM httprequestlog d WHERE d.serial_num=?1")
    List<httprequestlog> findBySerialNumEquals(String serial_num);

    @Query("SELECT d FROM httprequestlog d WHERE d.cookie=?1")
    List<httprequestlog> findByCookie(String cookie);
    
    @Query("SELECT d FROM httprequestlog d WHERE d.cookie=?1")
    httprequestlog getByCookie(String cookie);

    @Query("SELECT d FROM httprequestlog d WHERE d.serial_num=?1")
    httprequestlog getBySerialNumEquals(String serial_num);
}
