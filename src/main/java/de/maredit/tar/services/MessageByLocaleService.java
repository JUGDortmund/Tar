package de.maredit.tar.services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public interface MessageByLocaleService {

  String getMessage(String id);
}
