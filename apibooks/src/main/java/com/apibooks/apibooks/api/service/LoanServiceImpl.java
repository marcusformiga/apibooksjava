package com.apibooks.apibooks.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.apibooks.apibooks.api.dto.LoanFilterDTO;
import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;
import com.apibooks.apibooks.api.model.repository.LoanRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

  private LoanRepository loanRepository;

  public LoanServiceImpl(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }
  @Override
  public Loan save(Loan loan) {
    // deve buscar todos os livro em que o retorno seja false.
    // if (loanRepository.findBooksWhenReturnedIsFalse(loan.getBook())) {
    //   throw new BusinessException("O livro já está emprestado");
    // }
    return this.loanRepository.save(loan);
  }
  @Override
  public Optional<Loan> getById(Long id) {
    
    return this.loanRepository.findById(id);
  }
  @Override
  public Loan update(Loan loan) {
    return loanRepository.save(loan);
    
  }
  @Override
  public Page<Loan> findPaginate(LoanFilterDTO dto, Pageable pageRequest) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
    return this.loanRepository.findByBook(book, pageable);
    
  }
  @Override
  public List<Loan> getAllLateLoans() {
    final Integer loanDays = 4;
    LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
    return loanRepository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
  }
  
}
