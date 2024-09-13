package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.client_list;


@Repository
public interface client_listRepository extends CrudRepository<client_list, Long>{
    @Query("SELECT d FROM client_list d WHERE d.serial_num=?1")
    List<client_list> findBySerialNumEquals(String serial_num);
}
