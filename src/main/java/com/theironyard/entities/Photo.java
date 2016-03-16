package com.theironyard.entities;//Created by KevinBozic on 3/15/16.

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender;

    @ManyToOne
    User recipient;

    @Column(nullable = false)
    String fileName;

    @Column(nullable = false)
    LocalDateTime dateTime;

    @Column(nullable = false)
    int timeInput;

    public Photo() {
    }

    public Photo(User sender, User recipient, String fileName, LocalDateTime dateTime, int timeInput) {
        this.sender = sender;
        this.recipient = recipient;
        this.fileName = fileName;
        this.dateTime = dateTime;
        this.timeInput = timeInput;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getTimeInput() {
        return timeInput;
    }

    public void setTimeInput(int timeInput) {
        this.timeInput = timeInput;
    }
}
