package com.lhj.vehiclesystem.bean;

import java.io.Serializable;

/**
 * Created by Songzhihang on 2018/2/25.
 * 栏目栏栏目的实体
 */
public class TagItem implements Serializable{

    private int order;

    private int typeId;

    private String value;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
