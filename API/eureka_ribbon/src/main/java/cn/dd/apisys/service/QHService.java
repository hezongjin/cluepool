package cn.dd.apisys.service;

import cn.dd.apisys.comm.entity.PvuvEntity;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;
/**
 * FileName: QHService.java
 * CreateTime: 2020/6/29 14:00.
 * Version: V1.0
 * Author: ChengTao
 * Description: 基于ribbon的Service服务类-启航项目
 */
@Service
public class QHService {
    @Autowired
    RestTemplate restTemplate;

    public JSONObject getPvuv(PvuvEntity reqData) {
        System.out.println("[REQ]"+ JSON.toJSONString(reqData));
        return restTemplate.postForObject("http://SERVICE-YWAPI/qhststsRest/getPvuv",reqData,JSONObject.class);
    }
}
