package com.scaler.capstone.payment.services;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.scaler.capstone.payment.dtos.PaymentCallbackRequestDTO;
import com.scaler.capstone.payment.dtos.PaymentDTO;
import com.scaler.capstone.payment.exceptions.NotFoundException;
import com.scaler.capstone.payment.models.Payment;
import com.scaler.capstone.payment.repositories.PaymentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.callback_url}")
    private String razorpayCallbackUrl;

    private RazorpayClient razorpayClient;
    private PaymentRepository paymentRepository;

    public PaymentService(RazorpayClient razorpayClient, PaymentRepository paymentRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentDTO createPaymentLink(Double amount, String currency, String invoiceNumber) throws RazorpayException {
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
        paymentLinkRequest.put("callback_url", razorpayCallbackUrl); // Callback URL
        paymentLinkRequest.put("callback_method", "get");

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
        return PaymentDTO.from(payment,paymentLink.get("short_url"));
    }

    public Payment getPaymentDetails(String orderId) throws NotFoundException {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Payment not found for orderId: "+orderId));
    }

    public String fetchPaymentStatus_Razorpay(String paymentId) throws RazorpayException, NotFoundException {
        return razorpayClient.payments.fetch(paymentId).toString();
    }

    public boolean verifySignature(PaymentCallbackRequestDTO request)
            throws RazorpayException {

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_signature",request.getRazorpay_signature());
        attributes.put("payment_link_status",request.getRazorpay_payment_link_status());
        attributes.put("payment_link_id",request.getRazorpay_payment_link_id());
        attributes.put("payment_link_reference_id", request.getRazorpay_payment_link_reference_id());
        attributes.put("razorpay_payment_id",request.getRazorpay_payment_id());
        return Utils.verifyPaymentLink(attributes, razorpayKeySecret);
    }

    public void updatePaymentDetails(PaymentCallbackRequestDTO request) throws NotFoundException {
        Payment payment = paymentRepository.findByOrderId(request.getRazorpay_payment_link_id())
                .orElseThrow(()-> new NotFoundException("Payment not found for payment link id:"
                        +request.getRazorpay_payment_link_id()));

        payment.setPaymentId(request.getRazorpay_payment_id());
        payment.setStatus(request.getRazorpay_payment_link_status());
        paymentRepository.save(payment);
    }

}

