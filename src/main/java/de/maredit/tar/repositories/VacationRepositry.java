package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.Vacation;

public interface VacationRepositry extends MongoRepository<Vacation, String> {
}