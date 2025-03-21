package com.scaler.capstone.payment.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCallbackRequestDTO {
    private String razorpay_payment_id;
    private String razorpay_payment_link_id;
    private String razorpay_payment_link_reference_id;
    private String razorpay_payment_link_status;
    private String razorpay_signature;
}
