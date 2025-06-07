package com.example.DZforT1.service1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client", uniqueConstraints = {
    @UniqueConstraint(name = "unique_client_id", columnNames = "client_id")
})
public class Client {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Id
    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @PrePersist
    public void generateClientId() {
        if (this.clientId == null) {
            this.clientId = UUID.randomUUID();
        }
    }
}