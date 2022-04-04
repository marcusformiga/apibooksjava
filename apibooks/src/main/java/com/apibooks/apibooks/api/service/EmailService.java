package com.apibooks.apibooks.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

  void sendEmail(String message, List<String> listEmails);
  
}
