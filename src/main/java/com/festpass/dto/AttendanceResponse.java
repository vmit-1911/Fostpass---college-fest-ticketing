package com.festpass.dto;

public class AttendanceResponse {
    private Long eventId;
    private String eventName;
    private Integer capacity;
    private long ticketsIssued;
    private long checkedIn;
    private long remaining;
    private double attendancePercentage;

    public AttendanceResponse() {
    }

    public AttendanceResponse(Long eventId, String eventName, Integer capacity, long ticketsIssued, long checkedIn, long remaining, double attendancePercentage) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.capacity = capacity;
        this.ticketsIssued = ticketsIssued;
        this.checkedIn = checkedIn;
        this.remaining = remaining;
        this.attendancePercentage = attendancePercentage;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public long getTicketsIssued() {
        return ticketsIssued;
    }

    public void setTicketsIssued(long ticketsIssued) {
        this.ticketsIssued = ticketsIssued;
    }

    public long getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(long checkedIn) {
        this.checkedIn = checkedIn;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
