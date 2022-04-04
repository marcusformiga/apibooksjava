package com.apibooks.apibooks.resources;

import java.time.LocalDate;
import java.util.Optional;

import com.apibooks.apibooks.api.dto.LoanDTO;
import com.apibooks.apibooks.api.dto.ReturnedLoanDTO;
import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.entity.Loan;
import com.apibooks.apibooks.api.resources.LoanController;
import com.apibooks.apibooks.api.service.BookService;
import com.apibooks.apibooks.api.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class) // cria mini contexto para rodar teste
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
  private static final String LOAN_API = "/api/loans";
  
  @Autowired
  MockMvc mvc;

  @MockBean
  BookService bookService;

  @MockBean
  LoanService loanService;

  @Test
  @DisplayName("Deve ser capaz de criar um emprestimo de um livro")
  public void createLoanBook() throws Exception {
    // cenario
    Book book = Book.builder().id(1l).isbn("111").build();
    LoanDTO dto = LoanDTO.builder().customerName("validCustomer").isbn("111").build();
    String json = new ObjectMapper().writeValueAsString(dto);
    BDDMockito.given(bookService.getBookByIsbn("111"))
        .willReturn(Optional.of(book));
    Loan loan = Loan.builder().id(1l).customer("validCustomer").book(book).loanDate(LocalDate.now()).build();
    BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

    // exec
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);
    // verificacoes
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().string("1"));

  }

  @Test
  @DisplayName("Deve lançar um erro ao tentar fazer emprestimo de um livro para um isbn inesxistente")
  public void notLoanBookIfIsbnIsInvalid() throws Exception {
    LoanDTO dto = LoanDTO.builder().customerName("validCustomer").isbn("111").build();
    String json = new ObjectMapper().writeValueAsString(dto);
    BDDMockito.given(bookService.getBookByIsbn("111"))
        .willReturn(Optional.empty());
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);
    // verificacoes
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("errs", Matchers.hasSize(1)));

  }
  
  @Test
  @DisplayName("Deve lançar um erro ao tentar fazer emprestimo de um livro que ja esteja emprestado")
  public void notLoanBookIfBookAlreadyLoaned() throws Exception {

      LoanDTO dto = LoanDTO.builder().customerName("validCustomer").isbn("111").build();
      String json = new ObjectMapper().writeValueAsString(dto);
      Book book = Book.builder().id(1l).isbn("111").build();
      BDDMockito.given(bookService.getBookByIsbn("111"))
              .willReturn(Optional.of(book));
      BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
              .willThrow(new BusinessException("O livro ja está emprestado"));
      MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(json);
      // verificacoes
      mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.jsonPath("errs", Matchers.hasSize(1)));

  }

  @Test
  @DisplayName("Deve ser capaz de devolver um livro com sucesso")
  public void returnLoanBook() throws Exception {
      // cenario -> {returned:true}
      ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
      String json = new ObjectMapper().writeValueAsString(dto);
      Loan loan = Loan.builder().id(1l).returned(true).build();
      BDDMockito.when(loanService.getById(Mockito.anyLong())).thenReturn(Optional.of(loan));

      MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(json);

      mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  @DisplayName("Deve retornar um erro 404 ao tentar devolver um livro que nao existe")
  public void returnInexistentBook() throws Exception {
      // cenario -> {returned:true}
      ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
      String json = new ObjectMapper().writeValueAsString(dto);
      BDDMockito.when(loanService.getById(Mockito.anyLong())).thenReturn(Optional.empty());

      MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(json);

      mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
