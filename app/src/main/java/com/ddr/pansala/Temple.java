package com.ddr.pansala;

import java.io.Serializable;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class Temple extends UserRole implements Serializable {

    private String templeName;
    private String wiharadhipathiHimi;
    private String telNo;
    private String templeAddress;
    private String templeDescription;

    public Temple() {
    }

    public Temple(String userId, String name, String email, String userType, String createdBy, Long createdTimestamp,
                  String templeName, String wiharadhipathiHimi, String telNo, String templeAddress) {
        super(userId, name, email, userType, createdBy, createdTimestamp);
        this.templeName = templeName;
        this.wiharadhipathiHimi = wiharadhipathiHimi;
        this.telNo = telNo;
        this.templeAddress = templeAddress;
    }

    public String getTempleName() {
        return templeName;
    }

    public void setTempleName(String templeName) {
        this.templeName = templeName;
    }

    public String getWiharadhipathiHimi() {
        return wiharadhipathiHimi;
    }

    public void setWiharadhipathiHimi(String wiharadhipathiHimi) {
        this.wiharadhipathiHimi = wiharadhipathiHimi;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getTempleAddress() {
        return templeAddress;
    }

    public void setTempleAddress(String templeAddress) {
        this.templeAddress = templeAddress;
    }

    public String getTempleDescription() {
        return templeDescription;
    }

    public void setTempleDescription(String templeDescription) {
        this.templeDescription = templeDescription;
    }

    @Override
    public String toString() {
        return "Temple{" +
                "templeName='" + templeName + '\'' +
                ", wiharadhipathiHimi='" + wiharadhipathiHimi + '\'' +
                ", telNo='" + telNo + '\'' +
                ", templeAddress='" + templeAddress + '\'' +
                ", templeDescription='" + templeDescription + '\'' +
                "} " + super.toString();
    }
}
