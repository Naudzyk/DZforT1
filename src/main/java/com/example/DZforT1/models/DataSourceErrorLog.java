package com.example.DZforT1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "data_source_error_log")
@Data
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String exceptionMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;


    private String methodSignature;


    private LocalDateTime timestamp;


}
