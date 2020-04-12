package com.liu.controller;

import com.liu.entity.ResultEntity;
import com.liu.util.CrowdConstant;
import com.liu.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedisOperationController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将字符串类型的键值对保存到redis时调用的远程方法
     * @param normalKey
     * @param normalValue
     * @param timeoutMinute   超时时间，单位分钟  -1表示无过期时间，整数表示过期时间，0和null不接收
     * @return
     */
    @RequestMapping("/save/normal/string/key/value")
    ResultEntity<String> saveNormalStringKeyValue(
            @RequestParam("normalKey") String normalKey,
            @RequestParam("normalValue") String normalValue,
            @RequestParam("timeoutMinute") Integer timeoutMinute){

        //对输入的数据进行验证
        if(!CrowdUtils.strEffectiveCheck(normalKey) || !CrowdUtils.strEffectiveCheck(normalValue)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_REDIS_KEY_OR_VALUE_INVALID);
        }
        //获取字符串操作器对象
        ValueOperations<String, String> operation = stringRedisTemplate.opsForValue();

        //判断timeoutMinute的值
        if(timeoutMinute == null || timeoutMinute == 0){
            return ResultEntity.failed(CrowdConstant.MESSAGE_REDIS_KEY_TIME_OUT_INVALID);
        }
        //判断timeoutMinute是否为不设置过期时间
        if(timeoutMinute == -1){
            //按照不设置过期时间执行保存操作
            try {
                operation.set(normalKey,normalValue);
            } catch (Exception e) {
                e.printStackTrace();
                return  ResultEntity.failed(e.getMessage());
            }
            //返回结果
            return ResultEntity.successNoData();
        }

        //按照设置过期时间执行保存操作
        try {
            operation.set(normalKey,normalValue,timeoutMinute, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultEntity.failed(e.getMessage());
        }

        return ResultEntity.successNoData();
    }

    /**
     * 根据key查询对应的的value时调用的远程方法
     * @param normalKey
     * @return
     */
    @RequestMapping("/retrieve/string/value/by/string/key")
    ResultEntity<String> retrieveStringValueByStringKey(@RequestParam("normalKey") String normalKey){
        //对输入的数据进行验证
        if(!CrowdUtils.strEffectiveCheck(normalKey)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_REDIS_KEY_OR_VALUE_INVALID);
        }

        try {
            String value = stringRedisTemplate.opsForValue().get(normalKey);
            return ResultEntity.successWithData(value);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultEntity.failed(e.getMessage());
        }

    }

    /**
     * 根据key删除对应的的value时调用的远程方法
     * @param key
     * @return
     */
    @RequestMapping("/redis/remove/by/key")
    ResultEntity<String> removeByKey(@RequestParam("key") String key){

        //对输入的数据进行验证
        if(!CrowdUtils.strEffectiveCheck(key)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_REDIS_KEY_OR_VALUE_INVALID);
        }

        try {
            stringRedisTemplate.delete(key);
            return ResultEntity.successNoData();
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultEntity.failed(e.getMessage());
        }
    }


}
