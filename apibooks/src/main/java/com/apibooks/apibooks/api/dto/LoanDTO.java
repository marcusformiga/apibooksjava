package com.apibooks.apibooks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {
  private Long id;
  private String customerName;
  private String customerEmail;
  private String isbn;
  private BookDTO book;

}
