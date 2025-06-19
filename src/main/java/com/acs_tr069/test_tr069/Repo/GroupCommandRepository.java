package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.acs_tr069.test_tr069.Entity.GroupCommand;

@Repository
public interface GroupCommandRepository extends CrudRepository<GroupCommand, Long> {

    List<GroupCommand> findBydescription(String description);

    @Query(value = "SELECT * FROM group_command WHERE parent = ?1", nativeQuery = true)
    List<GroupCommand> findByParent(String parent);

    @Query(value = "SELECT * FROM group_command WHERE id = ?1", nativeQuery = true)
    GroupCommand getByID(Long id);

}
