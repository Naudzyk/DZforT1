package com.example.DZforT1.service1.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cache_entry")
public class CacheEntry {

    @Id
    private String id;

    @Column(name = "cache_value", columnDefinition = "TEXT")
    private String cacheValue;

    @Column(name = "time_at")
    private LocalDateTime timeAt;
}
