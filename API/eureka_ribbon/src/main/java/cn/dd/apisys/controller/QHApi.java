package cn.dd.apisys.controller;



import cn.dd.apisys.comm.entity.PvuvEntity;
import cn.dd.apisys.service.QHService;
import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: QHApi.java
 * CreateTime: 2020/6/29 14:00.
 * Version: V1.0
 * Author: ChengTao
 * Description: 基于ribbon的Controller类-启航项目接口
 */
@RestController
@RequestMapping(value = "/qhststsRest")
public class QHApi {
    @Autowired
    QHService qhService;
    @ResponseBody
    @PostMapping(value = "/getPvuv")
    public JSONObject getCallInfoByUserTel(@RequestBody PvuvEntity reqData) {
        return qhService.getPvuv(reqData);
    }

}
