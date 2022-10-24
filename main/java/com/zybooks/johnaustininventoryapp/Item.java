package com.zybooks.johnaustininventoryapp;

public class Item {
    private long mId;
    private String mName;
    private String mQuantity;
    private String mDescription;
    private String mCategory;


    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }

    public long getId() {
        return mId;
    }
    public void setId(long id) {
        mId = id;
    }

    public String getQuantity() {
        return mQuantity;
    }
    public void setQuantity(String quantity) {
        this.mQuantity = quantity;
    }

    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getCategory() {
        return mCategory;
    }
    public void setCategory(String category) {
        this.mCategory = category;
    }


}
