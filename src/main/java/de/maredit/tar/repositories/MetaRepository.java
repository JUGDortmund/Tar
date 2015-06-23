package de.maredit.tar.repositories;

import de.maredit.tar.models.Meta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetaRepository extends MongoRepository<Meta, String> {

}
