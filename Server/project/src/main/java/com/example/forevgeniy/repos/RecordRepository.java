package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.Record;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Integer> {
}
