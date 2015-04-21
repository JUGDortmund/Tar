package de.maredit.tar.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

public class Vacation {
  @Id
  private String id;
  
  @Reference
  private User user;
  
  private Date from;
  private Date to;
}