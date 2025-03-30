package com.scaler.capstone.payment.dtos;

import com.scaler.capstone.payment.models.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private String orderId;
    private String paymentId;
    private String refundId;
    private String status;
    private String paymentLink;

    public static PaymentDTO from(Payment payment, String paymentLink) {
        PaymentDTO paymentDto = from(payment);
        paymentDto.setPaymentLink(paymentLink);
        return paymentDto;
    }

    public static PaymentDTO from(Payment payment) {
        PaymentDTO paymentDto = new PaymentDTO();
        paymentDto.setOrderId(payment.getOrderId());
        paymentDto.setPaymentId(payment.getPaymentId());
        paymentDto.setRefundId(payment.getRefundId());
        paymentDto.setStatus(payment.getStatus());
        return paymentDto;
    }
}
