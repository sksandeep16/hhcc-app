package com.demo.controller;

import com.demo.entity.Payment;
import com.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/make")
    public ResponseEntity<?> makePayment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Double amount = Double.valueOf(payload.get("amount").toString());
            String method = payload.get("method").toString();
            String transactionId = java.util.UUID.randomUUID().toString(); // Always generate a new transactionId
            Payment payment = paymentService.insertPayment(userId, amount, method, "succeeded", null, null, transactionId);
            log.info("[DEBUG] Payment to save: {}", payment);
            if (payment == null) {
                return ResponseEntity.status(500).body("Payment failed: insertPayment returned null");
            }
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Payment failed", e);
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }

    @PostMapping("/make/card")
    public ResponseEntity<?> makePaymentWithCard(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Double amount = Double.valueOf(payload.get("amount").toString());
            String method = payload.get("method").toString();
            String cardNumber = payload.get("cardNumber").toString();
            String cardLast4 = cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : cardNumber;
            String transactionId = java.util.UUID.randomUUID().toString(); // Always generate a new transactionId
            Payment payment = paymentService.insertPayment(userId, amount, method, "succeeded", cardLast4, null, transactionId);
            log.info("[DEBUG] Payment to save: {}", payment);
            if (payment == null) {
                return ResponseEntity.status(500).body("Payment failed: insertPayment returned null");
            }
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Payment failed", e);
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }

    @PostMapping("/card")
    public ResponseEntity<?> payWithCard(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Double amount = Double.valueOf(payload.get("amount").toString());
            String paymentMethodId = payload.get("paymentMethodId").toString();

            Stripe.apiKey = stripeSecretKey;

            // Retrieve card details
            PaymentMethod pm = PaymentMethod.retrieve(paymentMethodId);
            String cardBrand = pm.getCard().getBrand();
            String cardLast4 = pm.getCard().getLast4();


            PaymentIntentCreateParams.AutomaticPaymentMethods automaticPaymentMethods =
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                    .build();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long)(amount * 100)) // amount in cents
                .setCurrency("usd")
                .setPaymentMethod(paymentMethodId)
                .setAutomaticPaymentMethods(automaticPaymentMethods)
                .setConfirm(true)
                .build();

            PaymentIntent intent = PaymentIntent.create(params);
            log.info("Stripe PaymentIntent created: {}", intent.getId());

            if ("requires_action".equals(intent.getStatus()) && intent.getNextAction() != null) {
                // This payment requires additional authentication (e.g., 3D Secure)
                Map<String, Object> response = new HashMap<>();
                response.put("requiresAction", true);
                response.put("clientSecret", intent.getClientSecret());
                return ResponseEntity.ok(response);
            } else if ("succeeded".equals(intent.getStatus())) {
                // The payment was successful
                String transactionId = intent.getId();
                Payment payment = paymentService.insertPayment(userId, amount, "CARD", "succeeded", cardLast4, cardBrand, transactionId); // This is the line in question
                if (payment == null) {
                    log.error("Card payment failed: insertPayment returned null for transactionId {}", transactionId);
                    return ResponseEntity.status(500).body("Card payment failed: Failed to record payment.");
                }
                return ResponseEntity.ok(payment);
            }

            return ResponseEntity.status(500).body("Card payment failed with status: " + intent.getStatus());
        } catch (Exception e) {
            log.error("Card payment failed: {}", e.getMessage(), e); // Enhanced logging
            return ResponseEntity.status(500).body("Card payment failed: " + e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getPaymentHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    // Get payment methods for a user (stub implementation)
    @GetMapping("/methods/{userId}")
    public ResponseEntity<?> getPaymentMethods(@PathVariable Long userId) {
        // TODO: Replace with real lookup from DB/service if needed
        // Example stubbed methods
        var methods = Arrays.asList(
            new HashMap<String, Object>() {{ put("id", 1); put("type", "CREDIT_CARD"); put("last4", "1234"); put("brand", "Visa"); }},
            new HashMap<String, Object>() {{ put("id", 2); put("type", "APPLE_PAY"); }},
            new HashMap<String, Object>() {{ put("id", 3); put("type", "GOOGLE_PAY"); }}
        );
        return ResponseEntity.ok(methods);
    }
}
