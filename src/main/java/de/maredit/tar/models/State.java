package de.maredit.tar.models;

/**
 * Created by czillmann on 20.04.15.
 */
public enum State {

    REQUESTED_SUBSTITUTE,
    WAITING_FOR_APPROVEMENT,
    APPROVED,
    REJECTED,
    ERROR;
}
