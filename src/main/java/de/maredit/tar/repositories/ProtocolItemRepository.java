package de.maredit.tar.repositories;

import de.maredit.tar.models.AccountEntry;

import de.maredit.tar.models.ProtocolItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProtocolItemRepository extends MongoRepository<ProtocolItem, String> {
  List<ProtocolItem> findAllByVacation(AccountEntry vacation);
}
