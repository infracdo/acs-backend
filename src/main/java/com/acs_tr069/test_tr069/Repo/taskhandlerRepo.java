package com.acs_tr069.test_tr069.Repo;

import java.util.List;

import com.acs_tr069.test_tr069.Entity.taskhandler;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface taskhandlerRepo extends CrudRepository<taskhandler, Long> {
    @Query("SELECT d FROM taskhandler d WHERE d.serial_num=?1")
    List<taskhandler> findBySerialNumEquals(String serial_num);

    @Query("SELECT d FROM taskhandler d WHERE d.id=?1")
    taskhandler getByID(Long id);
}
