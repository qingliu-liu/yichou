package com.liu.util;

import java.util.HashMap;
import java.util.Map;

public class CrowdConstant {
    public static final String ATTR_NAME_MESSAGE = "MESSAGE";
    public static final String ATTR_NAME_LOGIN_ADMIN = "LOGIN-ADMIN";
    public static final String ATTR_NAME_LOGIN_MEMBER = "LOGIN-MEMBER";
    public static final String REDIS_MEMBER_TOKEN_PREFIX = "SIGNED_MEMBER_";
    public static final String REDIS_PROJECT_TEMP_TOKEN_PREFIX = "PROJECT_TEMP_TOKEN_";
    public static final String REDIS_RANDOM_CODE_PREFIX = "RANDOM_CODE_";
    public static final String MESSAGE_LOGIN_FALIED = "登陆账号或密码不正确！";
    public static final String MESSAGE_CODE_INVALID = "明文不是有效字符串，请核对后再操作！";
    public static final String MESSAGE_ACCESS_DENIED = "请登录！";
    public static final String MESSAGE_LOGIN_ACCT_ALREADY_IN_USE = "登录账号被占用，请更换账号注册！";
    public static final Map<String, String> EXCEPTION_MESSAGE_MAP = new HashMap<>();
    public static final String MESSAGE_RANDOM_CODE_LENGTH_INVALID = "验证码长度不合法！";
    public static final String MESSAGE_REDIS_KEY_OR_VALUE_INVALID = "待存入redis的key或redis无效！";
    public static final String MESSAGE_REDIS_KEY_TIME_OUT_INVALID = "不接收0或null值，请确认是否设置过期时间！";
    public static final String MESSAGE_PHONE_NUM_INVALID = "请输入正确手机号！";
    public static final String MESSAGE_LOGINACCT_INVALID = "登录字符串无效！";
    public static final String MESSAGE_CODE_NOT_MATCH = "验证码不匹配！";
    public static final String MESSAGE_CODE_NOT_EXISTS = "验证码不存在或已过期！";

    static {
        EXCEPTION_MESSAGE_MAP.put("java.lang.ArithmeticException", "系统在进行数学运算时发生错误");
        EXCEPTION_MESSAGE_MAP.put("java.lang.RuntimeException", "系统在运行时发生错误");
        EXCEPTION_MESSAGE_MAP.put("com.liu.exception.LoginException", "登录过程中运行错误");
    }



}
