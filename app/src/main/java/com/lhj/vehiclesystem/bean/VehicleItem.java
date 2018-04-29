package com.lhj.vehiclesystem.bean;

import java.io.Serializable;

public class VehicleItem implements Serializable {
	protected int vehicleId;

	protected String businessId;

	protected String name;

	protected int typeId;

	protected String biref;

	protected String indexpicurl;

	protected String createTime;

	protected Double price;

	protected String typeTitle;

	protected String level;

	protected String identity;

	protected int statusId;

	protected String statusType;

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getBiref() {
		return biref;
	}

	public void setBiref(String biref) {
		this.biref = biref;
	}

	public String getIndexpicurl() {
		return indexpicurl;
	}

	public void setIndexpicurl(String indexpicurl) {
		this.indexpicurl = indexpicurl;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getTypeTitle() {
		return typeTitle;
	}

	public void setTypeTitle(String typeTitle) {
		this.typeTitle = typeTitle;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}
