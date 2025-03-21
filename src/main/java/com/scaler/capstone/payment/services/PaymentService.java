package com.scaler.capstone.payment.services;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.scaler.capstone.payment.exceptions.NotFoundException;
import com.scaler.capstone.payment.models.Payment;
import com.scaler.capstone.payment.repositories.PaymentRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private RazorpayClient razorpayClient;
    private PaymentRepository paymentRepository;

    public PaymentService(RazorpayClient razorpayClient, PaymentRepository paymentRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    public String createPaymentOrder(Double amount, String currency, String invoiceNumber) throws RazorpayException {
        //We need to create checkout page to do it using order
        //        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", amount * 100); // Amount in paise
//        orderRequest.put("currency", currency);
//        orderRequest.put("receipt", invoiceNumber);
//
//        Order order = razorpayClient.orders.create(orderRequest);
//        System.out.println("Order created: " + order.toString());

        // Do it using payment link
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amount*100); // Amount in paise (e.g., 1000 = ₹10)
        paymentLinkRequest.put("currency", currency);
        paymentLinkRequest.put("accept_partial", false);
        paymentLinkRequest.put("reference_id", invoiceNumber); // Unique reference ID (e.g., order ID)
        paymentLinkRequest.put("callback_url", "http://localhost:4000/payments/callback"); // Callback URL
        paymentLinkRequest.put("callback_method", "get"); // Callback method (GET or POST)

        PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);
        System.out.println("Order created: " + paymentLink.toString());

        // Save payment details to the database
        Payment payment = new Payment();
        payment.setOrderId(paymentLink.get("id"));
        payment.setAmount(amount);
        payment.setInvoiceNumber(invoiceNumber);
        payment.setCurrency(currency);
        payment.setStatus(paymentLink.get("status"));

        paymentRepository.save(payment);
        return paymentLink.get("short_url");
    }

    public String getPaymentStatus(String orderId) throws RazorpayException, NotFoundException {
        Payment payment= paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Payment not found for orderId: "+orderId));
        return payment.getStatus();
    }

    public void fetchPaymentStatus(String paymentId) throws RazorpayException, NotFoundException {

    }


}

