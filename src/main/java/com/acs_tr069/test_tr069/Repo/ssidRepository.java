package com.acs_tr069.test_tr069.Repo;
import java.util.List;

import com.acs_tr069.test_tr069.Entity.group_ssid;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ssidRepository extends CrudRepository<group_ssid, Long> {
    List<group_ssid> findByssid(String ssid);
    
    @Query("SELECT d FROM group_ssid d WHERE d.parent=?1")
    List<group_ssid> findByGroup(String parent);

    @Query("SELECT d FROM group_ssid d WHERE d.id=?1")
    group_ssid getByID(Long id);
}
