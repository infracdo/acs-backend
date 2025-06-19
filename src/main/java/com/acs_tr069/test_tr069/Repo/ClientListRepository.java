package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.acs_tr069.test_tr069.Entity.ClientList;


@Repository
public interface ClientListRepository extends CrudRepository<ClientList, Long>{

    @Query(value = "SELECT * FROM client_list WHERE serial_num=?1", nativeQuery = true)
    List<ClientList> findBySerialNumEquals(String serial_num);

}
