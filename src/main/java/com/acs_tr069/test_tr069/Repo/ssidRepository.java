package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.GroupSsid;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SsidRepository extends CrudRepository<GroupSsid, Long> {

    List<GroupSsid> findByssid(String ssid);

    @Query(value = "SELECT * FROM group_ssid WHERE parent = ?1", nativeQuery = true)
    List<GroupSsid> findByGroup(String parent);

    @Query(value = "SELECT * FROM group_ssid WHERE id = ?1", nativeQuery = true)
    GroupSsid getByID(Long id);

}
