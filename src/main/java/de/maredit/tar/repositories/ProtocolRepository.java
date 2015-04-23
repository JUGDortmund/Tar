package de.maredit.tar.repositories;

import de.maredit.tar.models.Protocol;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProtocolRepository extends MongoRepository<Protocol, String> {

}