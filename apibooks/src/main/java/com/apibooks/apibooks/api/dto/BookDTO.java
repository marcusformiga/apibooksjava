package com.apibooks.apibooks.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
  private Long id;
  @NotEmpty
  private String title;
  @NotEmpty
  private String author;
  @NotEmpty
  private String isbn;
}
