package com.demo.service;

import com.demo.entity.Payment;
import com.demo.entity.User;
import com.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, EmailService emailService, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    public Payment insertPayment(Long userId, Double amount, String method, String status, String cardLast4, String receiptUrl, String transactionId) {
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(amount)
                .method(method)
                .status(status)
                .cardLast4(cardLast4)
                .receiptUrl(receiptUrl)
                .transactionId(transactionId)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        System.out.println("[DEBUG] Payment before save: " + payment);
        Payment saved = paymentRepository.save(payment);
        // Send receipt email
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String to = user.getEmail();
            if ("succeeded".equals(status)) {
                String subject = "Payment Successful - Transaction ID: " + transactionId;
                String text = String.format("Dear %s,\n\nYour payment was successful.\n\nAmount: %.2f\nMethod: %s\nTransaction ID: %s\nDate: %s\n\nBest regards,\nHHCC Team",
                        user.getUsername(), amount, method, transactionId, saved.getCreatedAt());
                try {
                    emailService.sendEmail(to, subject, text);
                    System.out.println("[INFO] Payment success email sent to " + to);
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to send payment success email to " + to + ": " + e.getMessage());
                }
            } else {
                String subject = "Payment Failed - Transaction ID: " + transactionId;
                String text = String.format("Dear %s,\n\nYour payment has failed.\n\nPlease try again or contact support if the problem persists.\n\nAmount: %.2f\nMethod: %s\nTransaction ID: %s\nDate: %s\n\nBest regards,\nHHCC Team",
                        user.getUsername(), amount, method, transactionId, saved.getCreatedAt());
                try {
                    emailService.sendEmail(to, subject, text);
                    System.out.println("[INFO] Payment failure email sent to " + to);
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to send payment failure email to " + to + ": " + e.getMessage());
                }
            }
        }
        return saved;
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
}
