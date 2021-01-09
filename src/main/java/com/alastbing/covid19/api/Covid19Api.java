package com.alastbing.covid19.api;

import com.alastbing.covid19.service.Covid19Service;
import com.alastbing.covid19.edtity.DataResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/robot/covid19")
public class Covid19Api {

    @Resource(name = "covid19DXY")
    private Covid19Service covid19DXY;
    @Resource(name = "covid19QQ")
    private Covid19Service covid19QQ;

    @RequestMapping("/DXY")
    @ResponseBody
    public DataResult getCovid19DXYData(HttpSession session) {
        return covid19DXY.getData();
    }

    @RequestMapping("/QQ")
    @ResponseBody
    public DataResult getCovid19QQData(HttpSession session) {
        return covid19QQ.getData();
    }
}
