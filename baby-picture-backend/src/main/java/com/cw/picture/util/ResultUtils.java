package com.cw.picture.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果工具类
 *
 * @author chengwei
 */
public class ResultUtils {

    private static final String CODE = "code";
    private static final String MSG = "msg";
    private static final String DATA = "data";

    /**
     * 成功返回结果（带数据）
     * @param data 返回数据
     * @return ResponseEntity
     */
    public static ResponseEntity<Map<String, Object>> success(Object data) {
        return result(HttpStatus.OK.value(), "操作成功", data);
    }

    /**
     * 成功返回结果（带消息和数据）
     * @param msg 返回消息
     * @param data 返回数据
     * @return ResponseEntity
     */
    public static ResponseEntity<Map<String, Object>> success(String msg, Object data) {
        return result(HttpStatus.OK.value(), msg, data);
    }

    /**
     * 失败返回结果
     * @param code 错误码
     * @param msg 错误消息
     * @return ResponseEntity
     */
    public static ResponseEntity<Map<String, Object>> error(int code, String msg) {
        return result(code, msg, null);
    }

    /**
     * 失败返回结果（带数据）
     * @param code 错误码
     * @param msg 错误消息
     * @param data 返回数据
     * @return ResponseEntity
     */
    public static ResponseEntity<Map<String, Object>> error(int code, String msg, Object data) {
        return result(code, msg, data);
    }

    /**
     * 自定义返回结果
     * @param code 状态码
     * @param msg 返回消息
     * @param data 返回数据
     * @return ResponseEntity
     */
    private static ResponseEntity<Map<String, Object>> result(int code, String msg, Object data) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(CODE, code);
        resultMap.put(MSG, msg);
        resultMap.put(DATA, data);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
}
