package com.svijayr007.oncampusdelivery.model;

public class DeliveryOrderModel {
    private String agentId;
    private OrderModel orderModel;
    private DeliveryAgentUserModel deliveryAgentModel;
    private PartnerUserModel partnerUserModel;
    private String key;
    private int deliveryStatus;

    public DeliveryOrderModel() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public DeliveryAgentUserModel getDeliveryAgentModel() {
        return deliveryAgentModel;
    }

    public void setDeliveryAgentModel(DeliveryAgentUserModel deliveryAgentModel) {
        this.deliveryAgentModel = deliveryAgentModel;
    }

    public PartnerUserModel getPartnerUserModel() {
        return partnerUserModel;
    }

    public void setPartnerUserModel(PartnerUserModel partnerUserModel) {
        this.partnerUserModel = partnerUserModel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
