package de.maredit.tar.repositories;

import de.maredit.tar.models.ProtocolItem;
import de.maredit.tar.models.Vacation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProtocolItemRepository extends MongoRepository<ProtocolItem, String> {
  List<ProtocolItem> findAllByVacation(Vacation vacation);
}
