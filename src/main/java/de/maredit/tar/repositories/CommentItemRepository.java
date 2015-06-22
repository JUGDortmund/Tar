package de.maredit.tar.repositories;

import de.maredit.tar.models.CommentItem;
import de.maredit.tar.models.Vacation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentItemRepository extends MongoRepository<CommentItem, String> {
  List<CommentItem> findAllByVacation(Vacation vacation);
}
