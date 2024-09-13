package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.groups;
import org.springframework.data.repository.CrudRepository;

public interface groupsRepository extends CrudRepository<groups, Long> {
    List<groups> findByParent(String parent);
}
