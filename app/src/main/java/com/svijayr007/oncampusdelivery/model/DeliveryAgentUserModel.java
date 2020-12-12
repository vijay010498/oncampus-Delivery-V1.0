package com.svijayr007.oncampusdelivery.model;

public class DeliveryAgentUserModel {
    private boolean active;
    private String agentId,ImageUrl,name,partner,phone,restaurant,email;
    private long lastVisited;

    public DeliveryAgentUserModel() {
    }

    public DeliveryAgentUserModel(boolean active,String agentId, String imageUrl, String name, String partner, String phone, String restaurant, String email, long lastVisited) {
        this.active = active;

        this.agentId = agentId;
        ImageUrl = imageUrl;
        this.name = name;
        this.partner = partner;
        this.phone = phone;
        this.restaurant = restaurant;
        this.email = email;
        this.lastVisited = lastVisited;
    }

    public long getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(long lastVisited) {
        this.lastVisited = lastVisited;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }
}
