package com.apibooks.apibooks.api.service;

import java.util.Optional;

import com.apibooks.apibooks.api.model.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
  Book save(Book any);

  void delete(Book book);
  Optional<Book> getById(Long id);

  void update(Book book);

  Page<Book> findPaginate(Book filter, Pageable pageRequest);

  Optional<Book> getBookByIsbn(String isbn);
}
