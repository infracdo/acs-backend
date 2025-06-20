package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.AutoComplete;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoCompleteRepository extends CrudRepository<AutoComplete, Integer> {

    @Query(value = "SELECT * FROM auto_complete WHERE device_model = ?1", nativeQuery = true)
    List<AutoComplete> findByDeviceModel(String device_model);

}