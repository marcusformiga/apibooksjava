package com.apibooks.apibooks.services;

import java.util.Arrays;
import java.util.Optional;

import com.apibooks.apibooks.api.errors.BusinessException;
import com.apibooks.apibooks.api.model.entity.Book;
import com.apibooks.apibooks.api.model.repository.BookRepository;
import com.apibooks.apibooks.api.service.BookService;
import com.apibooks.apibooks.api.service.BookServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
  
  BookService bookService;

  // quando utilizamos o mock de algo o valor retornado eh o seu valor padrao
  // por exemplo o default de boolean eh false
  @MockBean
  BookRepository bookRepository;

  @BeforeEach
  public void setUp(){
    this.bookService = new BookServiceImpl(bookRepository);
  }
  
  @Test
  @DisplayName("Deve salvar um livro com sucesso")
  public void saveBook() {
    // cenario
    Book book = createValidBook();
    Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
    Mockito.when(bookRepository.save(book)).thenReturn(Book.builder().id(1l)
        .author("validAuthor").title("validBook").isbn("001").build());
    // exec
    Book savedBook = bookService.save(book);

    // verificacoes
    Assertions.assertThat(savedBook.getId()).isNotNull();
    Assertions.assertThat(savedBook.getAuthor()).isEqualTo(savedBook.getAuthor());
    Assertions.assertThat(savedBook.getTitle()).isEqualTo(savedBook.getTitle());
    Assertions.assertThat(savedBook.getIsbn()).isEqualTo(savedBook.getIsbn());
    Mockito.verify(bookRepository, Mockito.times(1)).save(book);
  }

  @Test
  @DisplayName("Deve buscar um livro por id com sucesso")
  public void findBookById() throws Exception {
    Long id = 1l;
    Book book = createValidBook();
    book.setId(id);
    Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    Optional<Book> foundBook = bookService.getById(id);
    Assertions.assertThat(foundBook.isPresent()).isTrue();
    Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);

  }
  
  @Test
  @DisplayName("Deve retorna vazio quando nao existir um livro com id informado")
  public void notFoundBookById() throws Exception {
    Long id = 1l;

    Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

    Optional<Book> book = bookService.getById(id);
    Assertions.assertThat(book.isPresent()).isFalse();

  }
  @Test
  @DisplayName("Deve lançar um erro de regra de negocio se ja existir um livro com isbn informado")
  public void notSavedBookWithIsbnDuplicated() throws Exception {
    // cenario
    Book book = createValidBook();
    Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

    // exec -> ao tentar salvar um livro captura a msg de erro
    Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));
    
    // verificacoes
    Assertions.assertThat(exception).isInstanceOf(BusinessException.class)
        .hasMessage("Isbn já cadastrado");

    Mockito.verify(bookRepository, Mockito.never()).save(book);    
  }
  @Test
  @DisplayName("Deve fazer uma busca filtrada pelas propiedades informadas")
  public void findBooksPaginate() throws Exception {
    // cenario
    Book book = createValidBook();
    PageRequest pageReq = PageRequest.of(0, 10);
    Page<Book> page = new PageImpl<>(Arrays.asList(book), pageReq, 1);
    Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class))).thenReturn(page);
    // exec
    Page<Book> result = bookService.findPaginate(book, pageReq);
    // verificacoes
    Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);

  }

  @Test
  @DisplayName("Deve ser capaz de buscar um livro por isbn")
  public void findBookByIsbn() throws Exception {
    String isbn = "111";
    Book book = Book.builder().id(1l).isbn(isbn).build();
    Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));

    Optional<Book> foundBook = bookService.getBookByIsbn(isbn);
    Assertions.assertThat(foundBook.isPresent()).isTrue();
    Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(isbn);
    Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
  }
  private Book createValidBook() {
    return Book.builder().id(1l).title("validTitle").author("validAuthor").isbn("001").build();
  }
}
