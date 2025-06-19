package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.RadioInfo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RadioInfoRepository extends CrudRepository<RadioInfo, Long> {

    @Query(value = "SELECT * FROM radio_info WHERE sn = ?1", nativeQuery = true)
    List<RadioInfo> findBySerialNumEquals(String sn);

}
