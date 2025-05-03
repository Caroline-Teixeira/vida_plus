package br.com.vidaplus.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


// Controller para tratamento de erros http
@ControllerAdvice
public class ErrorMapper {
 
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", String.valueOf(HttpStatus.FORBIDDEN.value()));
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Acesso negado: você não tem permissão para acessar este recurso.");
        errorResponse.put("path", ex.getMessage()); // Opcional: pode ajustar para incluir o URI da requisição
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}