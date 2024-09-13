package com.acs_tr069.test_tr069.Repo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.Entity.device_model_parameters;

import java.util.List;

import javax.persistence.QueryHint;


@Repository
public interface device_model_parametersRepository extends CrudRepository<device_model_parameters, Long>{
    
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    @Query(value = "SELECT * FROM device_model_parameters a "
        + "WHERE (a.manufacturer LIKE :deviceManufacturer%) "
        + "AND (a.model LIKE :deviceModel%) "
        , nativeQuery = true)
    device_model_parameters searchByManufacturerAndModel(
        @Param("deviceManufacturer") String manufacturer, 
        @Param("deviceModel") String model
    );
}
