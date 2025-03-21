package com.scaler.capstone.payment.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionDTO {
    private HttpStatus errorCode;
    private String message;

    public ExceptionDTO(HttpStatus errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

