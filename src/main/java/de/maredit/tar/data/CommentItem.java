package de.maredit.tar.data;

import de.maredit.tar.models.TimelineItem;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class CommentItem extends TimelineItem {

  private String text;

  private LocalDateTime modifed;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public LocalDateTime getModifed() {
    return modifed;
  }

  public void setModifed(LocalDateTime modifed) {
    this.modifed = modifed;
  }
}
