package com.example.recovery.domain.oauth2;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "OAUTH2_PROVIDERS")
public class Oauth2Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id", nullable = false)
    private Long id;

    @Column(name = "provider", nullable = false, length = 255)
    private String provider;

    @ColumnDefault("true")
    @Column(name = "activation", nullable = false)
    private Boolean activation;
    
}