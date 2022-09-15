package com.catpudding.pudding_keep_account.ui.monthBill.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.catpudding.pudding_keep_account.ui.monthBill.utils.model.ChartOneDataModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyChartOneClient extends WebViewClient {

    private ArrayList<ChartOneDataModel> data;
    private double max;

    public MyChartOneClient(ArrayList<ChartOneDataModel> data,double max) {
        this.data = data;
        if(max == 0){
            this.max = 100;
        }
        this.max = max;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.post(new Runnable() {
            @Override
            public void run() {
                //设置webView透明
                view.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                view.setBackgroundColor(0);
                view.getSettings().setAllowFileAccess(true);

                String chartData ="[\n";
                if(data.size()>0){
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    for(int i=0;i<data.size();i++){
                        String item = "{";
                        item += " name:\'" + data.get(i).getName() + "\',";
                        item += " value:" + decimalFormat.format(data.get(i).getValue()) + "}";
                        if(i<data.size()-1){
                            chartData += item + ",\n";
                        }else{
                            chartData += item + "\n]";
                        }
                    }
                }else{
                    chartData += "{name:\"暂无数据\",value:100}]";
                }
                //把穿过来的值 ，转换成字符串
                System.out.println(chartData);

                String option = "{\n" +
                        "  tooltip:{\n" +
                        "    trigger:\"item\",\n" +
                        "    formatter:\"<b>{b}</b><br/>花费：<b>{c}</b><br\\>占比：<b>{d}%</b>\",\n" +
                        "    backgroundColor: 'rgba(255, 255, 255, 0.8)',\n" +
                        "  },\n" +
                        "  legend:{\n" +
                        "    top:\"center\",\n" +
                        "    orient:\"vertical\",\n" +
                        "    right:\"10%\",\n" +
                        "    type:\"scroll\"," +
                        "    bottom:\"5%\"\n" +
                        "  },\n" +
                        "\n" +
                        "  visualMap: [\n" +
                        "    {\n" +
                        "      show: false,\n" +
                        "      type: 'continuous',\n" +
                        "      inRange:{\n" +
                        "        color:[\"#a5d8ff\",\"#4dabf7\",\"#228be6\"]\n" +
                        "      },\n" +
                        "      seriesIndex: 0,\n" +
                        "      min: 0,\n" +
                        "      max: " + (int)max + "\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      name: 'Access From',\n" +
                        "      type: 'pie',\n" +
                        "      radius: ['35%', '70%'],\n" +
                        "      center:[\"30%\",\"50%\"],\n" +
                        "      avoidLabelOverlap: false,\n" +
                        "      itemStyle: {\n" +
                        "        borderRadius: 6,\n" +
                        "        borderColor: '#fff',\n" +
                        "        borderWidth: 2\n" +
                        "      },\n" +
                        "      label: {\n" +
                        "        show: false,\n" +
                        "        position: 'center'\n" +
                        "      },\n" +
                        "      emphasis: {\n" +
                        "        label: {\n" +
                        "          show: true,\n" +
                        "          fontSize: '12',\n" +
                        "          fontWeight: 'bold'\n" +
                        "        }\n" +
                        "      },\n" +
                        "      labelLine: {\n" +
                        "        show: false\n" +
                        "      },\n" +
                        "      data: "+chartData +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                view.loadUrl(String.format("javascript:setData(%s)",option));
            }
        });
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }
}
