package com.catpudding.pudding_keep_account.ui.notifications.utils;

public class RankItem {
    private Integer source;
    private Integer percent = 0;
    private Double totalPrice = 0d;
    private double percentTip = 0;

    public double getPercentTip() {
        return percentTip;
    }

    public void setPercentTip(double percentTip) {
        this.percentTip = percentTip;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
