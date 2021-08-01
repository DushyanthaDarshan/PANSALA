package com.ddr.pansala;

/**
 * author : Dushyantha Darshan Rubasinghe
 */
public class Img {

    private String imgId;
    private String referenceId;

    public Img() {
    }

    public Img(String imgId, String referenceId) {
        this.imgId = imgId;
        this.referenceId = referenceId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public String toString() {
        return "Img{" +
                "imgId='" + imgId + '\'' +
                ", referenceId='" + referenceId + '\'' +
                '}';
    }
}
