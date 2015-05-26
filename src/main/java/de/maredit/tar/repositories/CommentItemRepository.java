package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.CommentItem;

public interface CommentItemRepository extends MongoRepository<CommentItem, String> {

}
