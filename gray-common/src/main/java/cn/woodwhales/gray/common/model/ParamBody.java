package cn.woodwhales.gray.common.model;

import lombok.Data;

/**
 * @author woodwhales on 2022-12-28 20:24
 */
@Data
public class ParamBody<T> {

    private String requestNo;

    private T data;

}
