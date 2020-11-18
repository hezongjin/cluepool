package cn.dd.apisys.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * FileName: ImpalaOP.java
 * CreateTime: 2020/7/20 18:00.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class ImpalaOP {
    static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
    static String CONNECTION_URL = "jdbc:impala://10.20.23.23:21050/default";

    public static void main(String[] args)
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try
        {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(CONNECTION_URL);
            ps = con.prepareStatement("select  (case when cityname is NULL then '未知' else cityname end) as cityname, count(t.cityname) counts,  sum(t.pv) pv, sum(t.uv) uv, sum(t.ip) ip, sum(t.newuv) newuv, sum(t.session)  session, sum(t.sessionpass) sessionpass, sum(t.avg_time) avg_time, sum(t.avg_page) avg_page, sum(t.avg_depth)  avg_depth  from  area_dv2  t where t.tenantid='140020' and t.provname='河北'  group by  t.cityname ; ");
            rs = ps.executeQuery();
            while (rs.next())
            {
                System.out.println(rs.getString("cityname") + '\t' + rs.getString(2)+ '\t' + rs.getString(3)+ '\t' + rs.getString(4)+ '\t' + rs.getString(5));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            //关闭rs、ps和con
            try {
                rs.close();
                ps.close();
                con.close();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }
}
