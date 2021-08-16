package ru.popov.ms2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Message {

    private Long id;
    private Long sessionId;
    private Date MC1Time;
    private Date MC2Time;
    private Date MC3Time;
    private Date endTime;

    public Message() {
    }

    public Message(Long id, Long sessionId, Date MC1Time, Date MC2Time, Date MC3Time, Date endTime) {
        this.id = id;
        this.sessionId = sessionId;
        this.MC1Time = MC1Time;
        this.MC2Time = MC2Time;
        this.MC3Time = MC3Time;
        this.endTime = endTime;
    }
}
