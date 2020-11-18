package cn.dd.apisys.controller;

import cn.dd.apisys.comm.entity.*;
import cn.dd.apisys.service.Ori1stService;
import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: Ori1stApi.java
 * CreateTime: 2020/6/17 13:02.
 * Version: V1.0
 * Author: ChengTao
 * Description:基于ribbon的Controller类-一号线索池项目接口
 */
@RestController
@RequestMapping(value = "/cluesPoolRest")
public class Ori1stApi {
    @Autowired
    Ori1stService ori1stService;

    /**
     * 一号线索池-接口16(复写原接口1)
     * 获取企业列表信息接口
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoList")
    public JSONObject getCallInfoByUserTel(@RequestBody EntBaseInfoEntity reqData) {
        return ori1stService.getEntInfoList(reqData);
    }

    /**
     * 一号线索池-接口17
     * 手机号精查接口
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoListByUserTel")
    public JSONObject getEntInfoListByUserTel(@RequestBody PhoneEntEntity reqData) {
        return ori1stService.getEntInfoListByUserTel(reqData);
    }

    /**
     * 一号线索池-接口18
     * 手机号查询通话记录接口
     */
    @ResponseBody
    @PostMapping(value = "/getCallInfoByUserTel")
    public JSONObject getCallInfoByUserTel(@RequestBody CallRecordEntity reqData) {
        return ori1stService.getCallInfoByUserTel(reqData);
    }

    /**
     * 一号线索池-接口2
     * 营销管理-企业详情-企业基本信息(多表)
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfo")
    public JSONObject getEntInfo(@RequestBody EntBaseInfoEntity reqData) {
        return ori1stService.getEntInfo(reqData);
    }

    /**
     * 一号线索池-接口10
     * 营销管理-企业详情-联系人信息
     */
    @ResponseBody
    @PostMapping(value = "/getContactInfomations")
    public JSONObject getContactInfomations(@RequestBody EntContactInfoEntity reqData) {
        return ori1stService.getContactInfomations(reqData);
    }
    /**
     * 一号线索池-接口15
     * 营销管理-企业详情-按名称精确查询企业基本信息
     */
    @ResponseBody
    @PostMapping(value = "/getEntInfoByEntName")
    public JSONObject getEntInfoByEntName(@RequestBody EntBaseInfoEntity reqData) {
        return ori1stService.getEntInfoByEntName(reqData);
    }
    /**
     * 一号线索池-接口3
     * 营销管理-企业详情-网络推广列表(单表)
     */
    @ResponseBody
    @PostMapping(value = "/getEntNetPopularization")
    public JSONObject getEntNetPopularization(@RequestBody EntNetPopularizationEntity reqData) {
        return ori1stService.getEntNetPopularization(reqData);
    }
    /**
     * 一号线索池-接口4
     * 营销管理-企业详情-招聘信息列表(单表)
     */
    @ResponseBody
    @PostMapping(value = "/getEntAdvertiseNotice")
    public JSONObject getEntAdvertiseNotice(@RequestBody EntAdvertiseNoticeEntity reqData) {
        return ori1stService.getEntAdvertiseNotice(reqData);
    }
    /**
     * 一号线索池-接口5
     * 营销管理-企业详情-企业公众号信息列表（单表）
     */
    @ResponseBody
    @PostMapping(value = "/getEntSubscriptions")
    public JSONObject getEntSubscriptions(@RequestBody EntSubscriptionsEntity reqData) {
        return ori1stService.getEntSubscriptions(reqData);
    }
    /**
     * 一号线索池-接口6
     * 营销管理-企业详情-新闻舆情-新闻动态信息列表
     */
    @ResponseBody
    @PostMapping(value = "/getEntPublicNews")
    public JSONObject getEntPublicNews(@RequestBody EntPublicNewsEntity reqData) {
        return ori1stService.getEntPublicNews(reqData);
    }
    /**
     * 一号线索池-接口7
     * 营销管理-企业详情-知识产权-专利信息列表
     */
    @ResponseBody
    @PostMapping(value = "/getEntPatentInfo")
    public JSONObject getEntPatentInfo(@RequestBody EntPatentInfoEntity reqData) {
        return ori1stService.getEntPatentInfo(reqData);
    }
    /**
     * 一号线索池-接口8
     * 营销管理-企业详情-知识产权-商标信息列表
     */
    @ResponseBody
    @PostMapping(value = "/getEntTrademarkInfo")
    public JSONObject getEntTrademarkInfo(@RequestBody EntTrademarkInfoEntity reqData) {
        return ori1stService.getEntTrademarkInfo(reqData);
    }
    /**
     * 一号线索池-接口9
     * 营销管理-企业详情-网站信息列表
     */
    @ResponseBody
    @PostMapping(value = "/getEntWebsiteInfo")
    public JSONObject getEntWebsiteInfo(@RequestBody EntWebsiteInfoEntity reqData) {
        return ori1stService.getEntWebsiteInfo(reqData);
    }
    /**
     * 一号线索池-接口11
     * 营销管理-企业详情-招标投标
     */
    @ResponseBody
    @PostMapping(value = "/getEntBidInfo")
    public JSONObject getEntBidInfo(@RequestBody EntBidInfoEntity reqData) {
        return ori1stService.getEntBidInfo(reqData);
    }

    /**
     * 一号线索池-接口12
     * 营销管理-企业详情-企业异常名录
     */
    @ResponseBody
    @PostMapping(value = "/getEntAbnormalOperInfo")
    public JSONObject getEntAbnormalOperInfo(@RequestBody EntBidInfoEntity reqData) {
        return ori1stService.getEntAbnormalOperInfo(reqData);
    }
    /**
     * 一号线索池-接口13
     * 营销管理-企业详情-相关企业-对外投资列表
     */
    @ResponseBody
    @PostMapping(value = "/getEntInvestInfo")
    public JSONObject getEntInvestInfo(@RequestBody EntInvestInfoEntity reqData) {
        return ori1stService.getEntInvestInfo(reqData);
    }
    /**
     * 一号线索池-接口14
     * 营销管理-企业详情-相关企业-股东信息
     */
    @ResponseBody
    @PostMapping(value = "/getEntShareholderInfo")
    public JSONObject getEntShareholderInfo(@RequestBody EntShareholderInfoEntity reqData) {
        return ori1stService.getEntShareholderInfo(reqData);
    }
}
