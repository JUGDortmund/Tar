package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {

}
