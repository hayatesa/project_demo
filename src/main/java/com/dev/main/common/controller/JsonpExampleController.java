package com.dev.main.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JsonpExampleController extends BaseController {

    @RequestMapping(path="/jsonp")
    public String createByJsonp(@RequestParam(value = "callback", defaultValue = "jsonpCallback",required=false) String callback, String data){
        JSONPObject jpo = new JSONPObject(callback);
        jpo.addParameter(JSONObject.parseObject(data));
        return callback + "("+data+")";
    }

}
