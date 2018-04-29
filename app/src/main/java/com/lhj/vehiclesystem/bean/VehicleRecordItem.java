package com.lhj.vehiclesystem.bean;

import java.io.Serializable;

public class VehicleRecordItem implements Serializable {

    private int id;

    private int vehicleId;

    private int userId;

    private String businessId;

    private String createTime;

    private long createTimeStmp;

    private String startTime;

    private String finishTime;

    private String message;

    private VehicleItem vehicleBean;

    private UserItem userBean;

    //0申请中，1使用中，2已拒绝，3已完成
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getCreateTimeStmp() {
        return createTimeStmp;
    }

    public void setCreateTimeStmp(long createTimeStmp) {
        this.createTimeStmp = createTimeStmp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VehicleItem getVehicleBean() {
        return vehicleBean;
    }

    public void setVehicleBean(VehicleItem vehicleBean) {
        this.vehicleBean = vehicleBean;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserItem getUserBean() {
        return userBean;
    }

    public void setUserBean(UserItem userBean) {
        this.userBean = userBean;
    }
}
