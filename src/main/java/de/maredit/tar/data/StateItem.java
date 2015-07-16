package de.maredit.tar.data;

import de.maredit.tar.models.TimelineItem;
import de.maredit.tar.models.enums.State;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StateItem extends TimelineItem {

  private State oldState;

  private State newState;

  public State getNewState() {
    return newState;
  }

  public void setNewState(State newState) {
    this.newState = newState;
  }

  public State getOldState() {
    return oldState;
  }

  public void setOldState(State oldState) {
    this.oldState = oldState;
  }
}
