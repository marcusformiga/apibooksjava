package com.apibooks.apibooks.api.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.apibooks.apibooks.api.dto.BookDTO;
import com.apibooks.apibooks.api.dto.LoanDTO;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;
import com.apibooks.apibooks.api.service.BookService;
import com.apibooks.apibooks.api.service.LoanService;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;


import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping(path = "/api/books")
@RestController
public class BookController {

  private BookService bookService;
  private ModelMapper modelMapper;
  private LoanService loanService;
  public BookController(BookService bookService, ModelMapper modelMapper, LoanService loanService) {
    this.bookService = bookService;
    this.modelMapper = modelMapper;
    this.loanService = loanService;
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public BookDTO create(@RequestBody @Valid BookDTO dto) {
    Book entity = modelMapper.map(dto, Book.class); // transforma dto em entity
    entity = bookService.save(entity);
    return modelMapper.map(entity, BookDTO.class); // retorna um bookDto

  }

  @GetMapping(path = "/{id}")
  public BookDTO findById(@PathVariable Long id) {
    return bookService.getById(id).map(book -> 
      modelMapper.map(book, BookDTO.class))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping(path = "/{id}/loans")
  public Page<LoanDTO> findLoansByBook(@PathVariable Long id, Pageable pageable) {
    Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    Page<Loan> result = loanService.getLoansByBook(book, pageable);
    List<LoanDTO> list = result.getContent().stream().map(loan -> {
      Book loanBook = loan.getBook();
      BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
      LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
      loanDTO.setBook(bookDTO);
      return loanDTO;
    }).collect(Collectors.toList());
    return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
  } 
  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    Book book = bookService.getById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    bookService.delete(book);
  }

  @PutMapping(path = "/{id}")
  public BookDTO update(@PathVariable Long id, @RequestBody BookDTO body) {
    Book book = bookService.getById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    book.setAuthor(body.getAuthor());
    book.setTitle(body.getTitle());
    bookService.update(book);
    return modelMapper.map(book, BookDTO.class);
  }

  @GetMapping
  public Page<BookDTO> findPaginate(BookDTO dto, Pageable pageRequest) {
    Book filter = modelMapper.map(dto, Book.class);
    Page<Book> result = bookService.findPaginate(filter, pageRequest);
    List<BookDTO> list = result.getContent()
      .stream()
      .map(entity -> modelMapper.map(entity, BookDTO.class))
        .collect(Collectors.toList());
    return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
  }


}
