package de.maredit.tar.repositories;
import de.maredit.tar.models.TimelineItem;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineItemRepository extends MongoRepository<TimelineItem, String> {

}
