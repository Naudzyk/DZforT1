package com.example.DZforT1.service1.repository;

import com.example.DZforT1.service1.models.CacheEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheEntryRepository extends JpaRepository<CacheEntry, Long> {
    Optional<CacheEntry> findById(String cachekey);
}
