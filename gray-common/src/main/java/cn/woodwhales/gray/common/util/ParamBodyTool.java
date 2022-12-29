package cn.woodwhales.gray.common.util;

import cn.woodwhales.gray.common.model.ParamBody;
import cn.woodwhales.gray.common.model.dto.ServerChain;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author woodwhales on 2022-12-29 16:44
 */
public class ParamBodyTool {

    public static ParamBody<ServerChain> build(ParamBody<ServerChain> param) {
        ServerChain serverChain = param.getData();
        if(Objects.isNull(serverChain)) {
            serverChain = new ServerChain();
            serverChain.setServerInfoList(new ArrayList<>());
        }

        if(Objects.isNull(serverChain.getServerInfoList())) {
            serverChain.setServerInfoList(new ArrayList<>());
        }
        serverChain.getServerInfoList().add(ServerInfoTool.build());
        param.setData(serverChain);
        return param;
    }

}
