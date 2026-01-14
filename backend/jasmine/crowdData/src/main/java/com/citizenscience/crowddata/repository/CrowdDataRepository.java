package com.citizenscience.crowddata.repository;
import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.citizenscience.crowddata.model.CrowdData;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

//makes a list of all the customer items
//which is what the controller uses to do all its commands
@Repository
public interface CrowdDataRepository extends JpaRepository<CrowdData, Long> {

	List<CrowdData> findByIsValid(boolean b);
}