package de.maredit.tar.repositories;

import de.maredit.tar.data.ProtocolItem;
import de.maredit.tar.data.Vacation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProtocolItemRepository extends MongoRepository<ProtocolItem, String> {
  List<ProtocolItem> findAllByVacation(Vacation vacation);
}
