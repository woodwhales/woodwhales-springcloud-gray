package cn.woodwhales.gray.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author woodwhales on 2022-12-28 20:22
 */
@Data
@NoArgsConstructor
public class RespVo<T> {

    private Integer code;
    private String message;

    private T data;

    public RespVo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RespVo<T> success() {
        return new RespVo<>(0, "success", null);
    }

    public static <T> RespVo<T> success(T data) {
        return new RespVo<>(0, "success", data);
    }
}
