package cn.dd.apisys.mapper;

import cn.dd.apisys.model.QHPVUV;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * FileName: CallCenterRecordMapper.java
 * CreateTime: 2020/6/9 13:28.
 * Version: V1.0
 * Author: ChengTao
 * Description: 启航新起统计模块查询
 */
public interface QHApiMapper {
    //根据传入日期及租户ID查询PV及环比数据
    @Select("select t1.tenantid as tenantid,t1.pv as pv,t1.uv as uv,t2.pv as opv,t2.uv as ouv from ce_qh_pvuv_base t1 left join ce_qh_pvuv_30before t2 on t1.tenantid=t2.tenantid where t1.tenantid=#{tenantId}")
    List<QHPVUV> selectByTenantIdStatDate(@Param("tenantId") String tenantId);

}
