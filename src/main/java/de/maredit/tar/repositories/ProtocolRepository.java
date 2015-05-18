package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.Protocol;

public interface ProtocolRepository extends MongoRepository<Protocol, String> {

}
