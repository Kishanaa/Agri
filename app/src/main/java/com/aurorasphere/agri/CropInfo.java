package com.aurorasphere.agri;

public class CropInfo {
    public String sowingWindow;
    public String harvestWindow;
    public int growingPeriodDays;
    public int select_tempMax;
    public int select_tempMin;
    public int select_humidity;
    public int select_moisture;
    public String Cultivation;
    public int crop_price;

    public CropInfo(String sowingWindow, String harvestWindow, int growingPeriodDays, int select_tempMax, int select_tempMin, int select_humidity, int select_moisture, String Cultivation, int crop_price) {
        this.sowingWindow = sowingWindow;
        this.harvestWindow = harvestWindow;
        this.growingPeriodDays = growingPeriodDays;
        this.select_tempMax = select_tempMax;
        this.select_tempMin = select_tempMin;
        this.select_humidity = select_humidity;
        this.select_moisture = select_moisture;
        this.Cultivation = Cultivation;
        this.crop_price = crop_price;
    }
}