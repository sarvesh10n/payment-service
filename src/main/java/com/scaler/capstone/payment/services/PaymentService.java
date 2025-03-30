package com.scaler.capstone.payment.services;

import com.razorpay.*;
import com.scaler.capstone.payment.dtos.PaymentCallbackRequestDTO;
import com.scaler.capstone.payment.dtos.PaymentDTO;
import com.scaler.capstone.payment.exceptions.InvalidRefundException;
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
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amount*100);
        paymentLinkRequest.put("currency", currency);
        paymentLinkRequest.put("accept_partial", false);
        paymentLinkRequest.put("reference_id", invoiceNumber);
        paymentLinkRequest.put("callback_url", razorpayCallbackUrl);
        paymentLinkRequest.put("callback_method", "get");

        PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

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

    public String fetchPaymentStatus_Razorpay(String paymentId) throws RazorpayException {
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

    public Payment createRefund(String orderId) throws NotFoundException, RazorpayException, InvalidRefundException {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(()-> new NotFoundException("Payment not found for payment link id:" +orderId));

        if(!"paid".equals(payment.getStatus())){
            throw new InvalidRefundException("payment Not completed for the order");
        }
        String paymentId = payment.getPaymentId();
        Double amount = payment.getAmount();

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amount * 100);
        refundRequest.put("speed","optimum");

        Refund refund = razorpayClient.payments.refund(paymentId,refundRequest);

        payment.setRefundId(refund.get("id"));
        payment.setStatus("refunded");
        return paymentRepository.save(payment);
    }

    public String fetchRefundStatus(String refundId) throws RazorpayException {
        return razorpayClient.refunds.fetch(refundId).toString();
    }

}

