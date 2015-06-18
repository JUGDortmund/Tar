package de.maredit.tar.repositories;

import de.maredit.tar.models.AccountEntry;

import org.springframework.data.mongodb.repository.MongoRepository;
import de.maredit.tar.models.CommentItem;

import java.util.List;

public interface CommentItemRepository extends MongoRepository<CommentItem, String> {
  List<CommentItem> findAllByVacation(AccountEntry vacation);
}
