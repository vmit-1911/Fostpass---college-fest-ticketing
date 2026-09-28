package com.festpass.dto;

import java.util.List;

public class ReportSummaryResponse {

    private long totalTicketsSold;
    private double averageTicketPrice;
    private double totalRevenue;
    private long totalCheckedIn;
    private List<EventReportItem> eventReports;

    public static class EventReportItem {
        private Long eventId;
        private String eventName;
        private Integer capacity;
        private long ticketsSold;
        private long checkedIn;
        private long remaining;
        private double attendancePercentage;
        private double ticketPrice;
        private double revenue;

        public EventReportItem() {
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

        public long getTicketsSold() {
            return ticketsSold;
        }

        public void setTicketsSold(long ticketsSold) {
            this.ticketsSold = ticketsSold;
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

        public double getTicketPrice() {
            return ticketPrice;
        }

        public void setTicketPrice(Double ticketPrice) {
            this.ticketPrice = (ticketPrice != null) ? ticketPrice : 0.0;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }
    }

    public ReportSummaryResponse() {
    }

    public long getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public void setTotalTicketsSold(long totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public double getAverageTicketPrice() {
        return averageTicketPrice;
    }

    public void setAverageTicketPrice(double averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalCheckedIn() {
        return totalCheckedIn;
    }

    public void setTotalCheckedIn(long totalCheckedIn) {
        this.totalCheckedIn = totalCheckedIn;
    }

    public List<EventReportItem> getEventReports() {
        return eventReports;
    }

    public void setEventReports(List<EventReportItem> eventReports) {
        this.eventReports = eventReports;
    }
}
