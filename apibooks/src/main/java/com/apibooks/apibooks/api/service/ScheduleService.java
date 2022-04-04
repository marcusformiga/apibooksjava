package com.apibooks.apibooks.api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.apibooks.apibooks.api.model.entity.Loan;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
  
  //private final static String CRON_LATE_LOAN_TIME = "0 0 0 1/1 * ?";
  //@Value("${application.mail.lateloans.msg}")
 
  private LoanService loanService;
  private EmailService emailService;

  public ScheduleService(LoanService loanService, EmailService emailService) {
  
    this.loanService = loanService;
    this.emailService = emailService;
  }
  
  // zero min zero sec zero hora todos os dias qualquer mes e ano
  @Scheduled(cron = "0 0 0 1/1 * ?")
  public void sendEmailToLateLoans() {
    List<Loan> allLateLoans = loanService.getAllLateLoans();
    // retirando a lista de emails da entidade
    List<String> listEmails = allLateLoans.stream().map(loan -> 
    loan.getEmail()).collect(Collectors.toList());
    String message = "Teste";
    emailService.sendEmail(message,listEmails);
  }
}
