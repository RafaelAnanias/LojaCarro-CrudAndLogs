package br.org.edu.ifrn.LojaCarro.exception;

import br.org.edu.ifrn.LojaCarro.CarroException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarroException.class)
    public ResponseEntity<Map<String, String>> handleCarroException(CarroException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}

