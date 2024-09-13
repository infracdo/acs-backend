package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.radio_info;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface radio_infoRepository extends CrudRepository<radio_info, Long>{
    @Query("SELECT d FROM radio_info d WHERE d.sn=?1")
    List<radio_info> findBySerialNumEquals(String sn);
}
