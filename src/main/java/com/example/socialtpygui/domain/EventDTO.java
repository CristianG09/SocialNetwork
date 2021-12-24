package com.example.socialtpygui.domain;

import java.time.LocalDate;
import java.util.List;

public class EventDTO extends Entity<Integer>{
    private String description;
    private LocalDate date;
    private String location;
    private List<UserDTO> participants;
    private String name;

    public String getName() {
        return name;
    }

    public EventDTO(String description, LocalDate date, String location, List<UserDTO> participants, String name) {
        this.description = description;
        this.date = date;
        this.location = location;
        this.participants = participants;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }
}
