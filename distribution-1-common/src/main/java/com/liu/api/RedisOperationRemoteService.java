package com.liu.api;

import com.liu.entity.ResultEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "redis-provider")
public interface RedisOperationRemoteService {
    /**
     * 将字符串类型的键值对保存到redis时调用的远程方法
     * @param normalKey
     * @param normalValue
     * @param timeoutMinute   超时时间，单位分钟
     * @return
     */
    @RequestMapping("/save/normal/string/key/value")
    ResultEntity<String> saveNormalStringKeyValue(@RequestParam("normalKey") String normalKey,
                                                  @RequestParam("normalValue") String normalValue,
                                                  @RequestParam("timeoutMinute") Integer timeoutMinute);

    /**
     * 根据key查询对应的的value时调用的远程方法
     * @param normalKey
     * @return
     */
    @RequestMapping("/retrieve/string/value/by/string/key")
    ResultEntity<String> retrieveStringValueByStringKey(@RequestParam("normalKey") String normalKey);

    /**
     * 根据key删除对应的的value时调用的远程方法
     * @param key
     * @return
     */
    @RequestMapping("/redis/remove/by/key")
    ResultEntity<String> removeByKey(@RequestParam("key") String key);
}
