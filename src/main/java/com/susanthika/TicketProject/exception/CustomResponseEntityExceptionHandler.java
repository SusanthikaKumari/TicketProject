//package com.susanthika.TicketProject.exception;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
//
//    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){
//        Map<String, Object> body = new HashMap<>();
//        body.put("timeStamp", LocalDateTime.now());
//        body.put("status",status.value());
//        List<String> errors = ex.getBindingResult().getFieldErrors()
//                .stream()
//                .map(x->x.getField() + ": " + x.getDefaultMessage())
//                .collect(Collectors.toList());
//        body.put("errors", errors);
//        return new ResponseEntity<>(body,status);
//    }
//
//}
