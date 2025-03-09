package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

public class Theater {
    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("district")
    private String district;

    // Getter methods
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getDistrict() { return district; }
}