package org.moonlight.idempotent.vo;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/2/2 14:47
 */
@EqualsAndHashCode(callSuper = false)
public class ResultVo extends HashMap<String, Object> {
    public ResultVo() {
        put("code", 200);
        put("msg", "success");
    }

    public ResultVo(Object obj) {
        put("code", 200);
        put("msg", "success");
        put("data", obj);
    }

    public static ResultVo ok() {
        return new ResultVo();
    }

    public static ResultVo ok(Object obj) {
        return new ResultVo(obj);
    }

    public static ResultVo error(String msg) { return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg); }

    public static ResultVo error(int code, String msg) {
        ResultVo result = new ResultVo();
        result.put("msg", msg);
        result.put("code", code);
        return result;
    }

    @Override
    public ResultVo put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static ResultVo error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知异常");
    }
}