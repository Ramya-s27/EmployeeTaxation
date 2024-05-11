package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.EmployeeTaxDetails;

public interface EmployeeTaxRepository extends JpaRepository<EmployeeTaxDetails, Long>{

}
