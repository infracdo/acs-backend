package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.acs_tr069.test_tr069.Entity.group_command;


@Repository
public interface group_commandRepo extends CrudRepository<group_command, Long> { // same with zeep ver, will retain
    List<group_command> findBydescription(String description);
    
    @Query("SELECT d FROM group_command d WHERE d.parent=?1")
    List<group_command> findByParent(String parent);

    @Query("SELECT d FROM group_command d WHERE d.id=?1")
    group_command getByID(Long id);
}
