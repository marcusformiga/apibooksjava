package com.apibooks.apibooks.api.errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;


public class ApiErrors {
  private List<String> errs;

  public ApiErrors(BusinessException ex) {
    this.errs = Arrays.asList(ex.getMessage());
  }

  public ApiErrors(BindingResult bindingResult) {
    this.errs = new ArrayList<>();
    bindingResult.getAllErrors().forEach(err -> this.errs.add(err.getDefaultMessage()));
  }

  public ApiErrors(ResponseStatusException ex) {
    this.errs = Arrays.asList(ex.getReason());
  }
  
  // public ApiErrors(NotFoundException ex) {
  //   this.errs = Arrays.asList(ex.getMessage());
  // }
  public List<String> getErrs() {
    return errs;
  }
}
