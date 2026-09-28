package com.festpass.dto;

import java.time.LocalDateTime;

public class CheckInResponse {
    private boolean success;
    private String message;
    private String ticketNumber;
    private String attendeeName;
    private String eventName;
    private String checkInTime;
    private LocalDateTime checkedInAt;

    public CheckInResponse() {
    }

    public CheckInResponse(boolean success, String message, String ticketNumber, String attendeeName, String eventName, String checkInTime, LocalDateTime checkedInAt) {
        this.success = success;
        this.message = message;
        this.ticketNumber = ticketNumber;
        this.attendeeName = attendeeName;
        this.eventName = eventName;
        this.checkInTime = checkInTime;
        this.checkedInAt = checkedInAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }
}
