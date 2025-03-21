package com.scaler.capstone.payment.dtos;

import com.scaler.capstone.payment.models.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private String orderId; // Razorpay order ID (e.g., "order_123")
    private String paymentId; // Razorpay payment ID (e.g., "pay_123")
    private String refundId; //Razorpay refund ID
    private String status;
    private String paymentLink;

    public static PaymentDTO from(Payment payment, String paymentLink) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(payment.getOrderId());
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setRefundId(payment.getRefundId());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setPaymentLink(paymentLink);
        return paymentDTO;
    }
}
