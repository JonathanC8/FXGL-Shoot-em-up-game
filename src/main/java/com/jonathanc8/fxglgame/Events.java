package com.jonathanc8.fxglgame;

import javafx.event.Event;
import javafx.event.EventType;

public class Events extends Event {
    public Events(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
