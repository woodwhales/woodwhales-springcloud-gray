package cn.woodwhales.gray.common.model.dto;

import lombok.Data;

/**
 * @author woodwhales on 2022-12-29 16:53
 */
@Data
public class ServerInfo {
    private String acceptHeader;
    private String serverName;
    private Integer serverPort;
    private String dateTime;
}
