package com.apibooks.apibooks.api.service;

import java.util.Optional;

import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.repository.BookRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

  private BookRepository repository;

  public BookServiceImpl(BookRepository repository) {
    this.repository = repository;
  }
  @Override
  public Book save(Book book) {
    if(repository.existsByIsbn(book.getIsbn())){
      throw new BusinessException("Isbn já cadastrado");
    }
    return this.repository.save(book);
  }
  @Override
  public Optional<Book> getById(Long id) {
  
    return this.repository.findById(id);
  }
  @Override
  public void delete(Book book) {
    if (book == null || book.getId() == null) {
      throw new IllegalArgumentException("Id não pode ser nulo");
    }
    this.repository.delete(book);
    
  }
  @Override
  public void update(Book book) {
    if (book == null || book.getId() == null) {
      throw new IllegalArgumentException("Id não pode ser nulo");
    }
    this.repository.save(book);
    
  }
  @Override
  public Page<Book> findPaginate(Book filter, Pageable pageReq) {
    Example<Book> bookEx = Example.of(filter, ExampleMatcher.matching()
        .withIgnoreCase().withIgnoreNullValues());
    return this.repository.findAll(bookEx, pageReq);
  }
  @Override
  public Optional<Book> getBookByIsbn(String isbn) {
    
    return this.repository.findByIsbn(isbn);
  }

}
