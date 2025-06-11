package com.example.DZforT1.service1.models;

import com.example.DZforT1.core.ENUM.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {

    @Id
    @Column(name = "transaction_id", unique = true, nullable = false)
    private UUID transactionId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Account account;

    @PrePersist
    public void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = UUID.randomUUID();
        }
    }

    public void setAccount(Account account) {
        this.account = account;
        if (account != null) {
            this.accountId = account.getAccountId();
            this.clientId = account.getClientId();
        } else {
            this.accountId = null;
            this.clientId = null;
        }
    }
}
