package de.maredit.tar.models;

import org.springframework.data.annotation.Id;

public class Vacation {
  @Id
  private String id;
  
  private int days;
  
  private User user;
}