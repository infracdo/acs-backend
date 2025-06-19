package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.HttpRequestLog;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HttpLogReqRepository extends CrudRepository<HttpRequestLog, Long> {

    @Query(value = "SELECT * FROM httprequestlog WHERE serial_num = ?1", nativeQuery = true)
    List<HttpRequestLog> findBySerialNumEquals(String serial_num);

    @Query(value = "SELECT * FROM httprequestlog WHERE cookie = ?1", nativeQuery = true)
    List<HttpRequestLog> findByCookie(String cookie);

    @Query(value = "SELECT * FROM httprequestlog WHERE cookie = ?1", nativeQuery = true)
    HttpRequestLog getByCookie(String cookie);

    @Query(value = "SELECT * FROM httprequestlog WHERE serial_num = ?1", nativeQuery = true)
    HttpRequestLog getBySerialNumEquals(String serial_num);

}
