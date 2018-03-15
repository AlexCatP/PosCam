package com.psy.model;

public class PosLib {
    private int posId;
    private int typeId;
    private int posPb;
    private String tags;
    private int userId;
    private String posUrl;
    private String posName;
    private String posContent;

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getPosUrl() {
        return posUrl;
    }

    public void setPosUrl(String posUrl) {
        this.posUrl = posUrl;
    }

    public int getPosPb() {
        return posPb;
    }

    public void setPosPb(int posPb) {
        this.posPb = posPb;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    public String getPosContent() {
        return posContent;
    }

    public void setPosContent(String posContent) {
        this.posContent = posContent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PosLib{" +
                "posId=" + posId +
                ", typeId=" + typeId +
                ", posPb=" + posPb +
                ", tags='" + tags + '\'' +
                ", userId=" + userId +
                ", posUrl='" + posUrl + '\'' +
                ", posName='" + posName + '\'' +
                ", posContent='" + posContent + '\'' +
                '}';
    }

}