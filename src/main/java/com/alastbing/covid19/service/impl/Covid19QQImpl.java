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

import java.util.HashMap;
import java.util.Map;

@Service("covid19QQ")
public class Covid19QQImpl implements Covid19Service {
    private final static String qqUrl = "https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5&callback=jQuery35107473212007057359_1610182406704&_=1610182406705";
    @Override
    public DataResult getData() {
        DataResult rs = new DataResult();
        try {
            Map<String, String> map = new HashMap<>();
            HttpClient client = HttpClientUtil.getHttpClient();
            HttpUriRequest method = HttpClientUtil.getRequestMethod(map, qqUrl, "get");
            HttpResponse response = client.execute(method);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String result = EntityUtils.toString(resEntity, "utf-8");
                     String s=result.split("jQuery35107473212007057359_1610182406704\\(")[1].split("\\)")[0];
                   JSONObject jsonObject=JSONObject.parseObject(s);

                    rs.setMessage("获取数据成功");
                    rs.setResult(JSONObject.parse(jsonObject.getString("data")));
                    rs.setErrorCode(200);
                    rs.setStatus(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs.setMessage("获取数据失败：" + e.getMessage());
            rs.setErrorCode(303);
            rs.setStatus(false);
        }
        return rs;
    }
}
