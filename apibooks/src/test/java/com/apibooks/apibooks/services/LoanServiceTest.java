package com.apibooks.apibooks.services;

import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.Optional;

import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;
import com.apibooks.apibooks.api.model.repository.LoanRepository;
import com.apibooks.apibooks.api.service.LoanService;
import com.apibooks.apibooks.api.service.LoanServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
  @MockBean
  LoanRepository loanRepository;
  LoanService loanService;

  @BeforeEach
  public void setup() {
    this.loanService = new LoanServiceImpl(loanRepository);
  }
  
  @Test
  @DisplayName("Deve ser capaz de salvar um emprestimo com sucesso")
  public void saveLoan() {
    Book book = Book.builder().id(1l).isbn("1110").build();
    String customer = "validCustomer";
    Loan savingLoan = createLoan();
    Loan savedLoan = Loan.builder().id(1l).book(book).customer(customer).loanDate(LocalDate.now()).build();
    //BDDMockito.when(loanRepository.findBooksWhenReturnedIsFalse(book)).thenReturn(false);
    BDDMockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);
    Loan loan = loanService.save(savingLoan);

    Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
    Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
    Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
    Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

  }
  
  // @Test
  // @DisplayName("Não deve ser capaz de salvar um emprestimo de um livro ja emprestado")
  // public void notSaveLoan() {
  //   Book book = Book.builder().id(1l).isbn("1110").build();
  //   String customer = "validCustomer";
  //   Loan savingLoan = Loan.builder().book(book).loanDate(LocalDate.now()).customer(customer).build();
  //   BDDMockito.when(loanRepository.findBooksWhenReturnedIsFalse(book)).thenReturn(false);

  //   Throwable exception = Assertions.catchThrowable(() -> loanService.save(savingLoan));

  //   Assertions.assertThat(exception).isInstanceOf(BusinessException.class)
  //       .hasMessage("O livro ja está emprestado");
  //   Mockito.verify(loanRepository, never()).save(savingLoan);

  // }
  @Test
  @DisplayName("Deve obter as informações de um emprestimo com sucesso")
  public void getLoanDetails() throws Exception {
    // cenario
    Long id = 1l;
    Loan loan = createLoan();
    loan.setId(id);
    BDDMockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
    //exec
    Optional<Loan> result = loanService.getById(id);
    //verificacoes
    Assertions.assertThat(result.isPresent()).isTrue();
    Assertions.assertThat(result.get().getId()).isEqualTo(id);
    Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
    Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());

  }

  @Test
  @DisplayName("Deve ser capaz de atualizar a devolução de um livro emprestado")
  public void updatedStatusBook() throws Exception {
    Loan loan = createLoan();
    loan.setId(1l);
    loan.setReturned(true);

    BDDMockito.when(loanRepository.save(loan)).thenReturn(loan);

    Loan updatedLoan = loanService.update(loan);
    Assertions.assertThat(updatedLoan.getReturned()).isTrue();
    Mockito.verify(loanRepository).save(loan);
   
  }

  private Loan createLoan() {
    Book book = Book.builder().id(1l).build();
    String customer = "validCustomer";
    return Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
  }
}
