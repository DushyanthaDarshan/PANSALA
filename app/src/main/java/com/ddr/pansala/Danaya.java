package com.ddr.pansala;

public class Danaya {

    private String username;
    private String danayaTime;
    private String danayaPlace;
    private String danayaDate;

    private String danayaStatus;
    private String createdBy;
    private Long createdTimestamp;
    private String updatedBy;
    private Long updatedTimestamp;

    public Danaya() {}

    public Danaya(String username, String danayaTime, String danayaPlace, String danayaDate,
                  String danayaStatus, String createdBy, Long createdTimestamp) {
        this.username = username;
        this.danayaTime = danayaTime;
        this.danayaPlace = danayaPlace;
        this.danayaDate = danayaDate;
        this.danayaStatus = danayaStatus;
        this.createdBy = createdBy;
        this.createdTimestamp = createdTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDanayaTime() {
        return danayaTime;
    }

    public void setDanayaTime(String danayaTime) {
        this.danayaTime = danayaTime;
    }

    public String getDanayaPlace() {
        return danayaPlace;
    }

    public void setDanayaPlace(String danayaPlace) {
        this.danayaPlace = danayaPlace;
    }

    public String getDanayaDate() {
        return danayaDate;
    }

    public void setDanayaDate(String danayaDate) {
        this.danayaDate = danayaDate;
    }

    public String getDanayaStatus() {
        return danayaStatus;
    }

    public void setDanayaStatus(String danayaStatus) {
        this.danayaStatus = danayaStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "Danaya{" +
                "username='" + username + '\'' +
                ", danayaTime='" + danayaTime + '\'' +
                ", danayaPlace='" + danayaPlace + '\'' +
                ", danayaDate='" + danayaDate + '\'' +
                ", danayaStatus='" + danayaStatus + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
