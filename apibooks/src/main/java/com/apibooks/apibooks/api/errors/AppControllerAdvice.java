package com.apibooks.apibooks.api.errors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AppControllerAdvice {
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ApiErrors handleMethodNotValidException(MethodArgumentNotValidException ex) {
    BindingResult bindResult = ex.getBindingResult(); // pega todos os erros da validação do body
    return new ApiErrors(bindResult);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(code=HttpStatus.BAD_REQUEST)
  public ApiErrors handleBusinessException(BusinessException ex) {
    return new ApiErrors(ex);
  }
  
  // @ExceptionHandler(NotFoundException.class)
  // @ResponseStatus(code = HttpStatus.NOT_FOUND)
  // public ApiErrors handleNotFoundException(NotFoundException ex) {
  //   return new ApiErrors(ex);
  // }
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity handleResponseStatusError(ResponseStatusException ex){
    return new ResponseEntity<>(new ApiErrors(ex), ex.getStatus());
  }
  
}
