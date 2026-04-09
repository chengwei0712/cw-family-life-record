package com.cw.picture.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * 数据返回对象
 *
 * @author chengwei
 * @version 1.0
 * @since 2025/07/21 2:00
 */
@Data
@Schema(description= "数据返回对象")
public class CommonResponse<T> {

    /**
     * 状态码
     */
    @Schema(description= "状态码：200表示成功，非200，message提示错误信息")
    private Integer code;

    /**
     * 提示信息
     */
    @Schema(description= "提示信息")
    private String msg;


    /**
     * 数据封装
     */
    @Schema(description= "返回数据")
    private T data;


    protected CommonResponse() {
    }

    protected CommonResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        if (!CommonExceptionEnum.SUCCESS.code.equals(this.code) && StringUtils.isBlank(msg)) {
            this.msg= "服务异常，请稍后再试";
        }
    }

    protected CommonResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(CommonExceptionEnum.SUCCESS.code,
                CommonExceptionEnum.SUCCESS.message, data);
    }


    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>(CommonExceptionEnum.SUCCESS.code, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码T
     */
    public static <T> CommonResponse<T> failed(IResponseCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage());
    }


    /**
     * 失败返回结果
     *
     * @param errorCode 错误码T
     */
    public static <T> CommonResponse<T> failed(IResponseCode errorCode,T data) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(),data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static <T> CommonResponse<T> failed(IResponseCode errorCode, String message) {
        return new CommonResponse<>(errorCode.getCode(), message);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResponse<T> failed(String message) {
        return new CommonResponse<>(CommonExceptionEnum.FAILED.code, message);
    }

    public static <T> CommonResponse<T> warning(int code, String message, T data) {
        return new CommonResponse<>(code, message, data);
    }

    /**
     * 参数校验未通过，返回提示信息
     *
     * @param message 提示信息
     */
    public static <T> CommonResponse<T> paramValidateFailed(String message) {
        return new CommonResponse<>(CommonExceptionEnum.PARAM_VALIDATE_FAILED.code, message);
    }

    public static <T> CommonResponse<T> paramValidateFailed(String message, T t) {
        return new CommonResponse<>(CommonExceptionEnum.PARAM_VALIDATE_FAILED.code, message, t);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResponse<T> forbidden() {
        return new CommonResponse<>(CommonExceptionEnum.NO_AUTH.code, CommonExceptionEnum.NO_AUTH.message);
    }

    public boolean success() {
        return CommonExceptionEnum.SUCCESS.code.equals(this.code);
    }

}
