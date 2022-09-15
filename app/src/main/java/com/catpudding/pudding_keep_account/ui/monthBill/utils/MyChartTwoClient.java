package com.catpudding.pudding_keep_account.ui.monthBill.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyChartTwoClient extends WebViewClient {
    private ArrayList<Double> yData;
    private ArrayList<String> xData;
    private double max;

    public MyChartTwoClient(ArrayList<Double> yData, ArrayList<String> xData,double max) {
        this.yData = yData;
        this.xData = xData;
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

                Integer topLine = (int)max;

                //把穿过来的值 ，转换成字符串
                String dataX="[";
                String dataY="[";
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                for(int i=0;i<xData.size();i++){
                    if(i<xData.size()-1){
                        dataX=dataX+"\""+xData.get(i)+"\",";
                        dataY=dataY+"\""+decimalFormat.format(yData.get(i))+"\",";
                    }else{
                        dataX=dataX+"\""+xData.get(i)+"\"]";
                        dataY=dataY+"\""+decimalFormat.format(yData.get(i))+"\"]";
                    }
                }

                String option = "{\n" +
                        "  visualMap: [\n" +
                        "    {\n" +
                        "      show: false,\n" +
                        "      type: 'continuous',\n" +
                        "      seriesIndex: 0,\n" +
                        "      min: 0,\n      " +
                        "      inRange:{\n" +
                        "        color:[\"#a5d8ff\",\"#4dabf7\",\"#228be6\"]" +
                        "      }," +
                        "      max: " + topLine + "\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  tooltip: {\n" +
                        "    trigger: 'axis',\n" +
                        "    backgroundColor: 'rgba(255, 255, 255, 0.8)'," +
                        "  },\n" +
                        "  xAxis: [\n" +
                        "    {\n" +
                        "      axisTick:{\n" +
                        "        show:false\n" +
                        "      },\n" +
                        "      axisPointer:{\n" +
                        "        type:\"shadow\"\n" +
                        "      },\n" +
                        "      data: " + dataX + ",\n" +
                        "      axisLine:{\n" +
                        "        lineStyle:{\n" +
                        "          color:\"#bfbfbf\",\n" +
                        "          width:\"0.5\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  yAxis: [\n" +
                        "    {\n" +
                        "      show:false\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  grid: [\n" +
                        "    {\n" +
                        "      show:false,\n" +
                        "      top:2,\n" +
                        "      left:0,\n" +
                        "      bottom: '15%',\n" +
                        "      right:0\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  series: [\n" +
                        "    {\n" +
                        "      type: 'line',\n" +
                        "      showSymbol: true,\n" +
                        "      smooth: true," +
                        "      data: "+dataY+",\n" +
                        "      lineStyle:{\n" +
                        "        width:1\n" +
                        "      },\n" +
                        "              areaStyle: {\n" +
                        "        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [\n" +
                        "          {\n" +
                        "            offset: 0,\n" +
                        "            color: 'rgba(34, 139, 230,1)'\n" +
                        "          },\n" +
                        "          {\n" +
                        "            offset: 1,\n" +
                        "            color: 'rgba(255,255,255,0)'\n" +
                        "          }\n" +
                        "        ])\n" +
                        "      }," +
                        "      markLine:{\n" +
                        "        symbol:\"none\",\n" +
                        "        lineStyle:{\n" +
                        "          color:\"#339af0\",\n" +
                        "          width:\"0.5\"\n" +
                        "        },\n" +
                        "        label:{\n" +
                        "            show:false\n" +
                        "        },\n" +
                        "        data:[\n" +
                        "          {\n" +
                        "            type:\"average\",\n" +
                        "            name:\"\",\n" +
                        "          },{\n" +
                        "            type:\"max\",\n" +
                        "            name:'fff',\n" +
                        "            lineStyle:{\n" +
                        "                type:\"solid\"\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
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
