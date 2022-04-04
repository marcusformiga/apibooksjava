package com.apibooks.apibooks.resources;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import com.apibooks.apibooks.api.dto.BookDTO;
import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.resources.BookController;
import com.apibooks.apibooks.api.service.BookService;
import com.apibooks.apibooks.api.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class) // cria mini contexto para rodar teste
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {
  
  static final String API_ENDPOINT = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  BookService bookService;
  @MockBean
  LoanService loanService;
  
  @Test
  @DisplayName("Deve criar um novo livro com sucesso")
  public void createValidBook() throws Exception {
    // faz uma requisicao para uma rota e espera um resultado
    // cenario
    
    BookDTO dto = createNewBook();
    Book savedBook = Book.builder().id(1l).title("validTitle").author("validAuthor").isbn("001").build();
    BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
    String json = new ObjectMapper().writeValueAsString(savedBook);
    
    // acão
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_ENDPOINT)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
        .content(json);
     
        // verificações
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("title").value(savedBook.getTitle()))
            .andExpect(MockMvcResultMatchers.jsonPath("author").value(savedBook.getAuthor()))
            .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(savedBook.getIsbn()));
  }
  
  @Test
  @DisplayName("Deve lançar um erro ao tentar criar um livro com dados inválidos")
  public void createInvalidBook() throws Exception {
    String json = new ObjectMapper().writeValueAsString(new BookDTO());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_ENDPOINT)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errs", Matchers.hasSize(3)));
  }
  @Test
  @DisplayName("Deve lançar um erro ao tentar criar um livro com isbn ja existente")
  public void notCreateBookWithDuplicatedIsbn() throws Exception {
    BookDTO dto = createNewBook();
    String json = new ObjectMapper().writeValueAsString(dto);
    BDDMockito.given(bookService.save(Mockito.any(Book.class)))
        .willThrow(new BusinessException("Isbn ja cadastrado"));
    
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API_ENDPOINT)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errs", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("errs[0]").value("Isbn ja cadastrado"));
  }

  @Test
  @DisplayName("Deve obter os detalhes de um livro")
  public void getDetailsBook() throws Exception {
    // cenario (given)
    Long id = 1l;
    Book book = Book.builder().id(id).author("validAuthor").title("validTitle").isbn("001").build();
    BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));
    // exec (when)
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_ENDPOINT.concat("/" + id))
        .accept(MediaType.APPLICATION_JSON);

    // assert
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));

  }

  @Test
  @DisplayName("Deve lançar um erro de livro não encontrado quando o livro não existir")
  public void notFoundBookTest() throws Exception {
    Long id = 1l;
    BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API_ENDPOINT.concat("/" + 1))
        .accept(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @DisplayName("Deve deletar um livro com sucesso")
  public void deleteBook() throws Exception {
    // para deletar um livro precisamos mockar um libro no bd
    Mockito.when(bookService.getById(Mockito.anyLong()))
        .thenReturn(Optional.of(Book.builder().id(1l).build()));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_ENDPOINT.concat("/" + 1));

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
  }
  @Test
  @DisplayName("Deve lançar um quando não encontrar um livro para ser deletado")
  public void notFoundBook() throws Exception {

    Long id = 1l;
    BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API_ENDPOINT.concat("/" + 1));

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Deve atualizar um livro com sucesso")
  public void updateBook() throws Exception {
    Long id = 1l;
    String json = new ObjectMapper().writeValueAsString(createNewBook());
    Book updatingBook = Book.builder().id(id).author("someAuthor")
        .title("someTitle").isbn("002").build();
    BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API_ENDPOINT.concat("/" + id))
        .accept(MediaType.APPLICATION_JSON)
        .content(json)
        .contentType(MediaType.APPLICATION_JSON);

    // assert
    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(updatingBook.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(updatingBook.getAuthor()));

  }

  // @Test
  // @DisplayName("Deve buscar livros filtrados")
  // public void findPaginateBooks() throws Exception {
  //   Long id = 1l;
  //   Book book = Book.builder().author(createNewBook().getAuthor())
  //       .title(createNewBook().getTitle()).isbn(createNewBook().getIsbn()).build();
  //   BDDMockito.given(bookService.findPaginate(Mockito.any(Book.class), Mockito.any(Pageable.class))
  //   ).willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 20), 1));
    
  //   String queryString = String.format("?title=%s&author=%s&page=0&size=20",
  //       book.getTitle(), book.getAuthor());
    
  //   MockHttpServletRequestBuilder request = MockMvcRequestBuilders
  //       .get(API_ENDPOINT.concat(queryString))
  //       .accept(MediaType.APPLICATION_JSON); // junta url com a query string

  //   mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
  //     .andExpect(MockMvcResultMatchers.jsonPath("content").value(1))
  //     .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
  //       .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(20));
  // }
  private BookDTO createNewBook() {
    return BookDTO.builder().author("validAuthor").title("validTitle").isbn("001").build();
  }
}
