package cn.dd.apisys.service.impl;

import cn.dd.apisys.service.IImpalaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * FileName: ImpalaServiceImpl.java
 * CreateTime: 2020/7/21 11:23.
 * Version: V1.0
 * Author: ChengTao
 * Description: Impala查询接口实现类
 */
@Service("impalaService")
public class ImpalaServiceImpl implements IImpalaService {
    //读取Impala配置文件
    @Value("${impalaconf.driver}")
    private String impala_driver;
    @Value("${impalaconf.url}")
    private String impala_url;

    /**
     * 根据租户ID及检索日期范围及检索粒度进行地区分布数据查询
     */
    public HashMap<String, Object> getImpalaDataForAreaAnalysis(String tenantId, String startDate, String stopDate, String provinceName) {
        HashMap<String, Object> backRes = new HashMap<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sqlStr = "";
        String tableName = "rtm_result_"+(Integer.valueOf(tenantId)%10)+".area_d";
        if ("".equals(provinceName)) {
            //省份维度
            sqlStr = "select  count(t.name) counts,(case when name is NULL then '未知' else name end) as areaname,sum(t.pv) pv, sum(t.uv) uv, sum(t.ip) ip, sum(t.newuv) newuv, sum(t.session)  session, sum(t.sessionpass) sessionpass, sum(ABS(t.avg_time)) avg_time, sum(t.avg_page) avg_page, sum(t.avg_depth)  avg_depth  from  "+tableName+"  t where t.tenantid='" + tenantId + "' and concat(t.year,t.month,t.day)>='" + startDate + "' and concat(t.year,t.month,t.day)<='" + stopDate + "'  group by  t.name";
        } else {
            //城市维度
            sqlStr = "select  count(t.name) counts,(case when cityname is NULL then '未知' else cityname end) as areaname,sum(t.pv) pv, sum(t.uv) uv, sum(t.ip) ip, sum(t.newuv) newuv, sum(t.session)  session, sum(t.sessionpass) sessionpass, sum(ABS(t.avg_time)) avg_time, sum(t.avg_page) avg_page, sum(t.avg_depth)  avg_depth  from  "+tableName+"  t where t.tenantid='" + tenantId + "' and concat(t.year,t.month,t.day)>='" + startDate + "' and concat(t.year,t.month,t.day)<='" + stopDate + "' and  t.name ='" + provinceName + "' group by  t.cityname";
        }
        Connection conn = formatConn();
        //System.out.println("[SQL][" + sqlStr + "]");
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> tempData = new HashMap<>();
                tempData.put("PV", rs.getString("pv"));
                tempData.put("UV", rs.getString("uv"));
                tempData.put("IP", rs.getString("ip"));
                tempData.put("NVC", rs.getString("newuv"));
                tempData.put("SU", rs.getString("session"));
                tempData.put("OSU", rs.getString("sessionpass"));
                tempData.put("avg_time", rs.getString("avg_time"));
                tempData.put("avg_page", rs.getString("avg_page"));
                tempData.put("avg_depth", rs.getString("avg_depth"));
                tempData.put("counts", rs.getString("counts"));
                tempData.put("VT", rs.getString("counts"));
                tempData.put("VL", rs.getString("avg_time"));
                backRes.put(rs.getString("areaname"), tempData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                ps.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        //System.out.println("[Driver]" + impala_driver);
        //System.out.println("[Url]" + impala_url);
        flushConn(conn);
        return backRes;
    }

    /**
     * Impala连接创建
     */
    public Connection formatConn() {
        Connection conn = null;
        try {
            Class.forName(impala_driver);
            conn = DriverManager.getConnection(impala_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * Impala资源释放
     */
    public void flushConn(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
