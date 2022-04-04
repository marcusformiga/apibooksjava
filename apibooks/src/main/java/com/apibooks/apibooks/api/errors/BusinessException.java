package com.apibooks.apibooks.api.errors;

public class BusinessException extends RuntimeException{
  public BusinessException(String msg) {
    super(msg);
  }
}
