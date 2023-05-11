package io.apirun.base.mapper;

import io.apirun.base.domain.NodeRegisterInfo;
import io.apirun.base.domain.NodeRegisterInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NodeRegisterInfoMapper {
    long countByExample(NodeRegisterInfoExample example);

    int deleteByExample(NodeRegisterInfoExample example);

    int deleteByPrimaryKey(String nodeId);

    int insert(NodeRegisterInfo record);

    int insertSelective(NodeRegisterInfo record);

    List<NodeRegisterInfo> selectByExample(NodeRegisterInfoExample example);

    NodeRegisterInfo selectByPrimaryKey(String nodeId);

    int updateByExampleSelective(@Param("record") NodeRegisterInfo record, @Param("example") NodeRegisterInfoExample example);

    int updateByExample(@Param("record") NodeRegisterInfo record, @Param("example") NodeRegisterInfoExample example);

    int updateByPrimaryKeySelective(NodeRegisterInfo record);

    int updateByPrimaryKey(NodeRegisterInfo record);
}