package com.lhj.vehiclesystem.third.wheelview;


import android.content.Context;

/**
 * WheelView3  的数字适配器
 * Numeric Wheel adapter.
 */
public class NumericWheelViewAdapter extends AbstractWheelTextAdapter {
    private String[] items;

    private String[] numItems;

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;

    private String label = "";

    /**
     * Default constructor
     */
    public NumericWheelViewAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelViewAdapter(Context context, int minValue, int maxValue) {
        super(context);
        if (maxValue <= minValue) {
            minValue = DEFAULT_MIN_VALUE;
            maxValue = DEFAULT_MAX_VALUE;
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        initItems();
    }

    private void initItems() {
        numItems = new String[maxValue - minValue + 1];
        for (int i = 0; i < maxValue - minValue + 1; i++) {
            numItems[i] = minValue + i + "";
        }
        items = new String[maxValue - minValue + 1];
        for (int i = 0; i < maxValue - minValue + 1; i++) {
            items[i] = minValue + i + "";
        }
        setLabel(label);
    }

    public void setLabel(String label) {
        this.label = label;
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i] + label;
        }
    }

    public void updateNumbers(int min, int max) {
        if (maxValue <= minValue) {
            minValue = DEFAULT_MIN_VALUE;
            maxValue = DEFAULT_MAX_VALUE;
        } else {
            minValue = min;
            maxValue = max;
        }
        initItems();
        notifyDataInvalidatedEvent();//刷新
    }

    public String[] getNumItems() {
        return numItems;
    }

    @Override
    protected CharSequence getItemText(int index) {
        if (null != items && items.length > 0) {
            if (index >= 0 && index < items.length) {
                String item = items[index];
                return item.toString();
            }
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        if (null != items && items.length > 0) {
            return items.length;
        }
        return 0;
    }
}
