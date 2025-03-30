package com.scaler.capstone.payment.advices;

import com.razorpay.RazorpayException;
import com.scaler.capstone.payment.dtos.ExceptionDTO;
import com.scaler.capstone.payment.exceptions.InvalidRefundException;
import com.scaler.capstone.payment.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvices {

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRefundException.class)
    ResponseEntity<ExceptionDTO> handleInvalidRefundException(InvalidRefundException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RazorpayException.class)
    ResponseEntity<ExceptionDTO> handleRazorpayException(RazorpayException ex){
        ExceptionDTO exceptionDto = new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
