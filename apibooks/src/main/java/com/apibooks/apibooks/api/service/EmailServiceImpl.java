package com.apibooks.apibooks.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

  @Autowired
  JavaMailSender javaMailSender;
  @Override
  public void sendEmail(String message, List<String> listEmails) {
    String[] mails = listEmails.toArray(new String[listEmails.size()]);
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom("libraryapi@mail.com");
    msg.setSubject("Livro com emprestimo atrasado");
    msg.setText(message);
    msg.setTo(mails);
    javaMailSender.send(msg);
    
  }
  
}
