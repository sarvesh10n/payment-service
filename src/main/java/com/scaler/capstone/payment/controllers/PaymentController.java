package com.scaler.capstone.payment.controllers;

import com.razorpay.RazorpayException;
import com.scaler.capstone.payment.services.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public String createOrder(@RequestParam Double amount,
                              @RequestParam String currency,
                              @RequestParam String invoiceNo) throws RazorpayException {

        return paymentService.createPaymentOrder(amount, currency, invoiceNo);
    }

    //On using get callback method, payment details are provided as part of url only
    @GetMapping("/callback")
    public String handlePaymentCallback(
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_payment_link_id") String paymentLinkId,
            @RequestParam("razorpay_payment_link_reference_id") String orderId,
            @RequestParam("razorpay_payment_link_status") String status,
            @RequestParam("razorpay_signature") String signature) {
        // Handle the payment response
        if ("paid".equals(status)) {
            // Update the order status to "PAID"
            //orderService.updateOrderStatus(order_id, "PAID");
            return "Payment successful!";
        } else {
            // Update the order status to "FAILED"
            //orderService.updateOrderStatus(order_id, "FAILED");
            return "Payment failed!";
        }
    }
}
