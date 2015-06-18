package de.maredit.tar.repositories;

import de.maredit.tar.models.AccountEntry;

import de.maredit.tar.models.StateItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateItemRepository extends MongoRepository<StateItem, String> {
  List<StateItem> findAllByVacation(AccountEntry vacation);
}
