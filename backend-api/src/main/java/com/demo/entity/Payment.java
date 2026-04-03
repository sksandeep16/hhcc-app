package com.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String method;

    @Builder.Default
    @Column(nullable = false)
    private String status = "SUCCESS";

    @Column(name = "card_last4")
    private String cardLast4;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "transaction_id")
    private String transactionId;
}
