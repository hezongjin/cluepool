package cn.dd.apisys.mapper;

import cn.dd.apisys.model.CallCenterRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import static com.alibaba.druid.stat.TableStat.Mode.Select;

/**
 * FileName: CallCenterRecordMapper.java
 * CreateTime: 2020/6/9 13:28.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public interface CallCenterRecordMapper {
    //根据电话号码查询会叫记录
    @Select("select * from ce_ori1st_call_center where phoneNum=#{phoneNum}")
    List<CallCenterRecord> selectByDomain(@Param("phoneNum") String phoneNum);

}
