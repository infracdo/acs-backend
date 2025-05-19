package com.acs_tr069.test_tr069.radius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acs_tr069.test_tr069.radius.entity.RadAuthLog;

@Repository
public interface RadAuthLogRepository extends JpaRepository<RadAuthLog, String> {

}
