package de.maredit.tar.repositories;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.User;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.State;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ManualEntryRepository extends MongoRepository<ManualEntry, String> {

}
