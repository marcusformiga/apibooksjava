package com.apibooks.apibooks.api.model.repository;

import java.util.Optional;

import com.apibooks.apibooks.api.model.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
  boolean existsByIsbn(String isbn);

  Optional<Book> findByIsbn(String isbn);
}
