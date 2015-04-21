package de.maredit.tar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public List<User> findAllUsers() {
    
    return null;
  }
}