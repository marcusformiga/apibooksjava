package com.apibooks.apibooks.api.model.repository;

import java.time.LocalDate;
import java.util.List;

import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  Page<Loan> findByBook(Book book, Pageable pageable);

  @Query(" select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is null or l.returned is false)")
  List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

  //@Query("select case when (count(l.id)> 0 )then true else false end from Loan l where l.book =:book and l.returned is not true")
  //boolean findBooksWhenReturnedIsFalse(@Param("book") Book book);
  
}
