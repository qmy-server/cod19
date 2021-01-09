package com.alastbing.covid19.service.impl;

import com.alastbing.covid19.service.Covid19Service;
import com.alastbing.covid19.edtity.DataResult;
import com.alastbing.covid19.utils.HttpClientUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("covid19DXY")
public class Covid19DXYImpl implements Covid19Service {

    private final static String dxyUrl = "https://ncov.dxy.cn/ncovh5/view/pneumonia";
    //private final static String baiduUrl = "https://voice.baidu.com/act/newpneumonia/newpneumonia";

    @Override
    public DataResult getData() {
        DataResult rs = new DataResult();
        try {
            List<Object> list = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            Map<String, String> map = new HashMap<>();
            HttpClient client = HttpClientUtil.getHttpClient();
            HttpUriRequest method = HttpClientUtil.getRequestMethod(map, dxyUrl, "get");
            HttpResponse response = client.execute(method);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String result = EntityUtils.toString(resEntity, "utf-8");
                    String[] data = result.split("<script");
                    for (String datum : data) {
                        if (datum.contains("id=\"getStatisticsService\">try ")) {
                            JSONObject jsonObject = stringTransformObj(datum);
                            JSONObject rej = new JSONObject();
                            assert jsonObject != null;
                            rej.put("seriousCount", jsonObject.getJSONObject("getStatisticsService").get("seriousCount"));//现存无症状
                            rej.put("overseasCount", jsonObject.getJSONObject("getStatisticsService").get("suspectedCount"));//境外输入
                            rej.put("currentConfirmedCount", jsonObject.getJSONObject("getStatisticsService").get("currentConfirmedCount"));//现存确诊
                            rej.put("confirmedCount", jsonObject.getJSONObject("getStatisticsService").get("confirmedCount"));//累计确诊
                            rej.put("deadCount", jsonObject.getJSONObject("getStatisticsService").get("deadCount"));//累计死亡
                            rej.put("curedCount", jsonObject.getJSONObject("getStatisticsService").get("currentConfirmedCount"));//累计治愈
                            list.add(rej);
                        }
                        if (datum.contains("id=\"getAreaStat\">try ")) {
                            list.add(stringTransformObj(datum));
                        }

                    }

                    resultMap.put("dxyData", list);
                    rs.setMessage("获取数据成功");
                    rs.setResult(resultMap);
                    rs.setErrorCode(200);
                    rs.setStatus(true);
                } else {
                    throw new Exception("远程获取数据出错，请重试。");
                }
            } else {
                throw new Exception("远程获取数据出错，请重试。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs.setMessage("获取数据失败：" + e.getMessage());
            rs.setErrorCode(303);
            rs.setStatus(false);
        }
        return rs;
    }

    private JSONObject stringTransformObj(String data) {
        String[] subData = data.split("try \\{ ");
        for (String subDatum : subData) {
            if (subDatum.contains("window.")) {
                String json = subDatum.replace("window.", "{\"");
                json = json.replace(" = ", "\":");
                json = json.replace("catch(e){}</script>", "");
                return JSONObject.parseObject(json);
            }
        }
        return null;
    }

}
