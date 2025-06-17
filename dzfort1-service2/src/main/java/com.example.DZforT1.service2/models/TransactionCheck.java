package com.example.DZforT1.service2.models;

import com.example.DZforT1.core.ENUM.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_check")
public class TransactionCheck {

    @Id
    @Column(name = "check_id", nullable = false, unique = true)
    private UUID checkId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "is_blacklisted", nullable = false)
    private boolean isBlacklisted;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "check_timestamp", nullable = false)
    private LocalDateTime checkTimestamp;

    @PrePersist
    public void generateCheckId() {
        if (this.checkId == null) {
            this.checkId = UUID.randomUUID();
        }
    }
}
