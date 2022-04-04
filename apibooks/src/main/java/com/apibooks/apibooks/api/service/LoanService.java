package com.apibooks.apibooks.api.service;

import java.util.List;
import java.util.Optional;

import com.apibooks.apibooks.api.dto.LoanFilterDTO;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {

  Loan save(Loan loan);

  Optional<Loan> getById(Long id);
  Page<Loan> findPaginate(LoanFilterDTO dto, Pageable pageRequest);
  Loan update(Loan loan);

  List<Loan> getAllLateLoans();
  Page<Loan> getLoansByBook(Book book, Pageable pageable);
  
}
