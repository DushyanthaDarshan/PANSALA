package com.ddr.pansala;

import java.io.Serializable;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class UserRole implements Serializable {

    private String userId;
    private String name;
    private String email;
    private String userType;
    private String preferenceTempleId;

    private String userStatus;
    private String createdBy;
    private Long createdTimestamp;
    private String updatedBy;
    private Long updatedTimestamp;

    public UserRole() {
    }

    public UserRole(String userId, String name, String email, String userType, String createdBy,
                    Long createdTimestamp) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.userStatus = "ACTIVE";
        this.createdBy = createdBy;
        this.createdTimestamp = createdTimestamp;
        this.updatedBy = null;
        this.updatedTimestamp = null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getPreferenceTempleId() {
        return preferenceTempleId;
    }

    public void setPreferenceTempleId(String preferenceTempleId) {
        this.preferenceTempleId = preferenceTempleId;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", preferenceTempleId='" + preferenceTempleId + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
