package com.catpudding.pudding_keep_account.ui.monthBill.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyChartThreeClient extends WebViewClient {
    private ArrayList<Double> data;

    public MyChartThreeClient(ArrayList<Double> data) {
        this.data = data;
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
                view.getSettings().setJavaScriptEnabled(true);
                //把穿过来的值 ，转换成字符串
                String dataY="[";
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                for(int i=0;i<data.size();i++){
                    if(i<data.size()-1){
                        dataY=dataY+decimalFormat.format(data.get(i))+",";
                    }else{
                        dataY=dataY+decimalFormat.format(data.get(i))+"]";
                    }
                }

                String option = "{\n" +
                        "  tooltip:{\n" +
                        "    trigger:\"axis\",\n" +
                        "    formatter:\"<b>{b}月</b><br/>支出：<b>{c}元</b>\",\n" +
                        "    backgroundColor: 'rgba(255, 255, 255, 0.8)',\n" +
                        "  },\n" +
                        "  grid:{\n" +
                        "    show:false,\n" +
                        "    left:0,\n" +
                        "    top:0,\n" +
                        "    right:0,\n" +
                        "    bottom:20\n" +
                        "  },\n" +
                        "  xAxis: {\n" +
                        "    data: " + "[1,2,3,4,5,6,7,8,9,10,11,12]" + ",\n" +
                        "    axisTick: {\n" +
                        "      show: false\n" +
                        "    },\n" +
                        "    axisLabel: {\n" +
                        "      fontSize:10\n" +
                        "    },\n" +
                        "    axisLine: {\n" +
                        "      show: false\n" +
                        "    },\n" +
                        "    z: 10\n" +
                        "  },\n" +
                        "  yAxis: {\n" +
                        "    show:false,\n" +
                        "    axisLine: {\n" +
                        "      show: false\n" +
                        "    },\n" +
                        "    axisTick: {\n" +
                        "      show: true\n" +
                        "    },\n" +
                        "    axisLabel: {\n" +
                        "      color: '#999'\n" +
                        "    }\n" +
                        "  },\n" +
                        "  dataZoom: [\n" +
                        "    {\n" +
                        "      type: 'inside'\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      type: 'bar',\n" +
                        "      showBackground: true,\n" +
                        "      itemStyle: {\n" +
                        "        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [\n" +
                        "          { offset: 0, color: '#83bff6' },\n" +
                        "          { offset: 0.5, color: '#188df0' },\n" +
                        "          { offset: 1, color: '#188df0' }\n" +
                        "        ]),\n" +
                        "        borderRadius:5,\n" +
                        "      },\n" +
                        "      data: "+dataY+"\n" +
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
