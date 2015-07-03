package de.maredit.tar.data;

import de.maredit.tar.models.TimelineItem;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ProtocolItem extends TimelineItem {

  String fieldName;

  Object oldValue;

  Object newValue;

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public void setOldValue(Object oldValue) {
    this.oldValue = oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public void setNewValue(Object newValue) {
    this.newValue = newValue;
  }
}
