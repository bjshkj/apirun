package io.apirun.base.mapper.ext;

import io.apirun.base.domain.NodeRegisterInfo;
import io.apirun.base.domain.NodeRegisterInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtNodeRegisterInfoMapper {

    List<NodeRegisterInfo> listNodeInfo();

    List<NodeRegisterInfo> listNodeByStatus(@Param("nodeStatus") String nodeStatus);

    List<NodeRegisterInfo> listNodeByStatusList(@Param("nodeStatus") List<String> nodeStatus);

    int udpateNodeStatus(NodeRegisterInfo info);
}