package com.apibooks.apibooks.api.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.apibooks.apibooks.api.dto.BookDTO;
import com.apibooks.apibooks.api.dto.LoanDTO;
import com.apibooks.apibooks.api.dto.LoanFilterDTO;
import com.apibooks.apibooks.api.dto.ReturnedLoanDTO;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;
import com.apibooks.apibooks.api.service.BookService;
import com.apibooks.apibooks.api.service.LoanService;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping(path = "/api/loans")
public class LoanController {
  
  private final LoanService loanService;
  private final BookService bookService;
  private final ModelMapper modelMapper;

  public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
    this.bookService = bookService;
    this.loanService = loanService;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public Long create(@RequestBody LoanDTO dto) {
    Book book = bookService.getBookByIsbn(dto.getIsbn())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    Loan entity = Loan.builder().book(book)
        .customer(dto.getCustomerName())
        .loanDate(LocalDate.now())
        .build();
    entity = loanService.save(entity);
    return entity.getId();
  }
  @PatchMapping(path = "/{id}")
  public void returnBookLoaned(@PathVariable Long id, ReturnedLoanDTO dto) {
    Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    loan.setReturned(dto.getReturned());
    loanService.update(loan);
  }
  @GetMapping
  public Page<LoanDTO> findPaginate(LoanFilterDTO dto, Pageable pageRequest) {
    Page<Loan> result = loanService.findPaginate(dto, pageRequest);
    List<LoanDTO> listLoans = result.getContent().stream().map(entity -> {
      Book book = entity.getBook();
      BookDTO bookDto = modelMapper.map(book, BookDTO.class);
      LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
      loanDTO.setBook(bookDto);
      return loanDTO;
      
    }).collect(Collectors.toList());
    return new PageImpl<LoanDTO>(listLoans, pageRequest, result.getTotalElements());
  }
}
