package com.scaler.capstone.payment.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payment extends BaseModel{
    private String orderId;
    private String paymentId;
    private String refundId;
    private String status;
    private Double amount;
    private String currency;
    private String invoiceNumber;
}
