package com.festpass.dto;

import java.util.List;

public class DashboardStatsResponse {

    private long totalEvents;
    private long totalTicketsIssued;
    private long totalCheckedIn;
    private long totalAvailableSeats;
    private long activeEvents;
    private List<EventResponse> events;
    private List<RecentCheckInItem> recentCheckIns;

    public static class RecentCheckInItem {
        private String ticketNumber;
        private String attendeeName;
        private String eventName;
        private String checkInTime;
        private String status;

        public RecentCheckInItem() {
        }

        public RecentCheckInItem(String ticketNumber, String attendeeName, String eventName, String checkInTime, String status) {
            this.ticketNumber = ticketNumber;
            this.attendeeName = attendeeName;
            this.eventName = eventName;
            this.checkInTime = checkInTime;
            this.status = status;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public DashboardStatsResponse() {
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getTotalTicketsIssued() {
        return totalTicketsIssued;
    }

    public void setTotalTicketsIssued(long totalTicketsIssued) {
        this.totalTicketsIssued = totalTicketsIssued;
    }

    public long getTotalCheckedIn() {
        return totalCheckedIn;
    }

    public void setTotalCheckedIn(long totalCheckedIn) {
        this.totalCheckedIn = totalCheckedIn;
    }

    public long getTotalAvailableSeats() {
        return totalAvailableSeats;
    }

    public void setTotalAvailableSeats(long totalAvailableSeats) {
        this.totalAvailableSeats = totalAvailableSeats;
    }

    public long getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(long activeEvents) {
        this.activeEvents = activeEvents;
    }

    public List<EventResponse> getEvents() {
        return events;
    }

    public void setEvents(List<EventResponse> events) {
        this.events = events;
    }

    public List<RecentCheckInItem> getRecentCheckIns() {
        return recentCheckIns;
    }

    public void setRecentCheckIns(List<RecentCheckInItem> recentCheckIns) {
        this.recentCheckIns = recentCheckIns;
    }
}
