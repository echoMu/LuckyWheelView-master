package com.phone580.luckywheelview;

public class Price {
    private String priceName;
    private String priceIcon;

    public Price() {
    }

    public Price(String priceName, String priceIcon) {
        this.priceName = priceName;
        this.priceIcon = priceIcon;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getPriceIcon() {
        return priceIcon;
    }

    public void setPriceIcon(String priceIcon) {
        this.priceIcon = priceIcon;
    }

    @Override
    public String toString() {
        return "Price{" +
                "priceName='" + priceName + '\'' +
                ", priceIcon='" + priceIcon + '\'' +
                '}';
    }
}
