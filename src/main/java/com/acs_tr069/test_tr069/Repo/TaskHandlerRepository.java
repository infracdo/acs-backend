package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.TaskHandler;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskHandlerRepository extends CrudRepository<TaskHandler, Long> {

    @Query(value = "SELECT * FROM taskhandler WHERE serial_num = ?1", nativeQuery = true)
    List<TaskHandler> findBySerialNumEquals(String serial_num);

    @Query(value = "SELECT * FROM taskhandler WHERE id = ?1", nativeQuery = true)
    TaskHandler getByID(Long id);

}
