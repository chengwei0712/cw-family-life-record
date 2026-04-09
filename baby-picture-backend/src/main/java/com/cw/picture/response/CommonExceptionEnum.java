package com.cw.picture.response;

import lombok.Getter;

/**
 * 在途错误码枚举类
 *
 * @author chengwei
 * @since 2025-07-21
 * @version 1.0
 */
@Getter
public enum CommonExceptionEnum {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    REPEAT_WARN(219,"正常业务返回"),

    UPDATE_WARN(220,"变更提醒"),

    RESPONSE_221_WHETHER_FORCE_UPDATE(221,"是否强制更新"),

    NO_LOGIN(401, "暂未登录或token已经过期"),

    MINI_APP_OPENID_INVALID(402, "无效的openid"),

    NO_AUTH(403, "没有访问权限"),

    LOGIN_NEED_SMS_CODE(411, "PC端账号登录需要短信验证码登录"),

    INTERFACE_CALL_FLOW_REACH_LIMIT(429, "请求过于频繁触发服务限流，请稍后再试"),

    MOBILE_BIND_OPENID_DISABLE(499, "登录微信与之前绑定的微信不一致,请使用收款微信登录"),

    FAILED(500, "服务正忙，请稍后再试"),

    RESPONSE_RESULT_EMPTY(501, "返回结果为空"),

    HTTP_MEDIA_TYPE_NOT_SUPPORTED(502, "http media type not supported"),

    REQUEST_LIMIT(600, "请求过于频繁，请稍后再试"),

    REQUEST_MISMETHOD(601, "请求方法不正确"),

    PARAM_VALIDATE_FAILED(700, "请求参数校验失败"),

    PARAM_TYPEMISMATCH_FAILED(701, "参数类型匹配失败"),

    PARAM_RUNNING_TEMPLATE_OVERRIDE(702, "同名的运行管理板已经存在，请问您是覆盖还是新增？"),

    PARAM_VEHICLE_HAVE_TASK(703, "车辆已经存在任务，是否合并任务？"),

    PARAM_IMPORTDATA_EMPTY(704, "导入数据为空，请确认"),

    PARAM_IMPORFILE_EMPTY(705, "请上传导入文件"),

    TABLE_RECORD_REMOVE_FAILED(706, "数据删除失败"),

    ACCOUNT_PASSWORD_WRONG_REACH_LIMIT(710, "该账号登录密码错误次数超过最大允许次数，请明日再试"),

    ACCOUNT_OR_PASSWORD_WRONG(711, "用户名或密码错误"),

    ACCOUNT_NO_USE(712, "账号已停用"),

    ACCOUNT_HAVE_EXPIRE(713, "账号已过有效期"),

    ACCOUNT_NO_AMOUNT(714, "账号已欠费"),

    INTERFACE_NO_USAGE(715, "接口已停用"),

    INTERFACE_HAVE_EXPIRE(716, "接口已过期"),

    RESPONSE_NO_DATA(800, "暂无数据"),

    VEHICLE_NO_EXIST_IN_ZJ(801, "该车为非营运车辆"),

    VEHICLE_CALL_NO_REACH_LIMIT(802, "车辆调用数量已达上限"),

    VEHICLE_CALL_TIMES_REACH_LIMIT(803, "该车调用次数已达上限"),

    INTERFACE_CALL_TIMES_REACH_LIMIT(804, "接口调用数量已达上限"),

    RESPONSE_901_NEED_AUTH_ID_CARD(901, "体验权益到期,请实名认证"),

    RESPONSE_902_BIND_NEW_VEHICLE(902, "体验权益到期,请绑定新车"),

    RESPONSE_903_BUY_VIP(903, "体验权益到期,请购买会员"),

    NO_MEMBER_PREMISSION(904, "请购买会员"),

    IMPROT_ERROR(5001,"导入数据匹配不完全"),

    PARAM_IMPORFILE_DATA_ERR(10010, "导入文件内容部分错误"),

    UN_RECOGINIZE_EFFECTIVE_VIN(10020,"当前操作未能识别出有效VIN码"),

    NOT_TARGET_4S_STORE(10030,"不是目标4S店"),

    JIAOYUN_VIN_SCAN_FAILED(10040,"VIN识别失败"),

    JIAOYUN_STANDBY_STAFF_UNBIND_FAILED(10050,"人员解绑失败"),

    VWMS_WX_APP_FAILD(10060,"仓储计费微信小程序异常"),

    DATA_DOWNLOAD_CENTER(10070,"下载超限，请去【系统管理-数据导出管理】下载"),

    VTMS_IMPORT_MATCH_FAILED(11010,"导入数据匹配不完全"),

    NOT_ALL_LOADED(11020,"当前页面还有待装车数据，是否确认不装");

    public final Integer code;

    public final String message;

    CommonExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CommonExceptionEnum getEnumByCode(int code) {
        for (CommonExceptionEnum value : CommonExceptionEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public static CommonExceptionEnum getEnumByDesc(String desc) {
        for (CommonExceptionEnum value : CommonExceptionEnum.values()) {
            if (value.message.equals(desc)) {
                return value;
            }
        }
        return null;
    }
}
