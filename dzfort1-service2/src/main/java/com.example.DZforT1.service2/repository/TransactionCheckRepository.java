package com.example.DZforT1.service2.repository;

import com.example.DZforT1.service2.models.TransactionCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionCheckRepository extends JpaRepository<TransactionCheck, UUID> {
}
