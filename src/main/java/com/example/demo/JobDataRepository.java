package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDataRepository extends JpaRepository<JobData, Long> {
}
