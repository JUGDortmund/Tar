package de.maredit.tar.repositories;

import de.maredit.tar.models.Vacation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VacationRepository extends MongoRepository<Vacation, String> {
}