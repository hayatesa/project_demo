package com.dev.main.common.controller;

import com.dev.main.common.util.JsonUtils;
import com.dev.main.common.util.ResultMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/restful")
public class RESTfulExampleController extends BaseController {


    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ResultMap searchExample(@PathVariable Integer id) {
        return ResultMap.success("查找ID为" + id + "的资源");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResultMap createExample(@RequestBody Map<String, Object> data) {
        ResultMap result = ResultMap.success();
        result.putAll(data);
        return result;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResultMap updateExample(@RequestBody Map<String, Object> data) {
        ResultMap result = ResultMap.success();
        result.putAll(data);
        return result;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResultMap deleteExample(@PathVariable Integer id) {
        return ResultMap.success("删除ID为" + id + "的资源");
    }


}
