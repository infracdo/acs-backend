package com.acs_tr069.test_tr069.Repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.Entity.DeviceModelParameters;

import javax.persistence.QueryHint;

@Repository
public interface DeviceModelParametersRepository extends CrudRepository<DeviceModelParameters, Long> {

    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    @Query(value = "SELECT * FROM device_model_parameters " + "WHERE manufacturer LIKE CONCAT(?1, '%') " + "AND model LIKE CONCAT(?2, '%')", nativeQuery = true)
    DeviceModelParameters searchByManufacturerAndModel(String manufacturer, String model);
    
}
