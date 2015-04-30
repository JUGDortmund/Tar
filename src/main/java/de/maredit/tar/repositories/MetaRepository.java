package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.Meta;

public interface MetaRepository extends MongoRepository<Meta, String> {

}