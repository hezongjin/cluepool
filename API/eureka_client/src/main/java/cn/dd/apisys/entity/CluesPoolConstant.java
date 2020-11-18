package cn.dd.apisys.entity;

/**
 * FileName: CluesPoolConstant.java
 * CreateTime: 2020/6/3 17:18.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class CluesPoolConstant {

    public interface RESULT_CODE {
        //成功
        int SUCCESS_ = 200;
        //无记录
        int INVALID_ = 201;
        //远程请求出错
        int ERROR_ = 501;
        //请求参数出错
        int PARERROR_ = 502;
        //必要更新出错
        int UPDATEERROR_ = 503;
        //必要数据查询出错
        int DATAERROR_ = 504;
        //失败
        int WARN_ = 507;
    }

}