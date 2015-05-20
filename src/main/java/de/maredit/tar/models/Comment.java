package de.maredit.tar.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

public class Comment {

  @Id
  private String id;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd.MM.yyyy")
  private LocalDate from;

  @DBRef
  @NotNull
  private User user;

  @DBRef
  @NotNull
  private Vacation vacation;

}
