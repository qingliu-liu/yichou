package com.liu.controller;

import com.liu.api.DataBaseOperationRemoteService;
import com.liu.api.RedisOperationRemoteService;
import com.liu.entity.po.MemberPO;
import com.liu.entity.vo.MemberSignSuccessVO;
import com.liu.entity.vo.MemberVO;
import com.liu.entity.ResultEntity;
import com.liu.util.CrowdConstant;
import com.liu.util.CrowdUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
public class MemberController {

    @Resource
    private RedisOperationRemoteService redisService;

    @Autowired
    private DataBaseOperationRemoteService dataBaseService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    //spring会根据@Value注解中的表达式读取yml或properties配置文件给成员变量设置对应的值
    @Value("${liu.short.message.appCode}")
    private String appcode;

    //退出登录
    @RequestMapping("/member/logout")
    public ResultEntity<String> logout(@RequestParam("token") String token){
        return redisService.removeByKey(token);
    }

    @RequestMapping("/member/manager/login")
    public ResultEntity<MemberSignSuccessVO> login(@RequestParam("loginacct") String loginacct ,
                                        @RequestParam("userpswd") String userpswd){
        //1.根据登录账号查询数据库获取MemberPO对象
        ResultEntity<MemberPO> memberPOResultEntity = dataBaseService.retrieveMemberByLoginAcct(loginacct);
        if(ResultEntity.FAILED.equals(memberPOResultEntity.getResult()))
            return  ResultEntity.failed(memberPOResultEntity.getMessage());
        //2.获取MemberPO的对象
        MemberPO memberPO = memberPOResultEntity.getData();
        if(memberPO == null){
            return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_FALIED);
        }
        //3.若不为空，获取从数据库查询得到的密码
        String userpswdDatabase = memberPO.getUserpswd();
        //4.比较密码
        boolean matchResult = passwordEncoder.matches(userpswd,userpswdDatabase);
        if(!matchResult){
            return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_FALIED);
        }
        //5.密码一致，生成token
        String token = CrowdUtils.generateToken();
        //6.从MemberPO对象获取memberId
        String memberId = memberPO.getId()+"";
        //7.将token和memberId存入redis
        ResultEntity<String> saveTokenResultEntity = redisService.saveNormalStringKeyValue(token,memberId,-1);
        if(ResultEntity.FAILED.equals(saveTokenResultEntity.getResult())){
            return  ResultEntity.failed(saveTokenResultEntity.getMessage());
        }
        //8.封装MemberSignSuccessVO对象
        MemberSignSuccessVO memberSignSuccessVO = new MemberSignSuccessVO();
        BeanUtils.copyProperties(memberPO,memberSignSuccessVO);
        memberSignSuccessVO.setToken(token);
        //9.返回结果
        return ResultEntity.successWithData(memberSignSuccessVO);
    }

    @RequestMapping("/member/manager/register")
    public ResultEntity<String> register(@RequestBody MemberVO memberVO){
        //1.检查验证码是否有效
        String randomCode = memberVO.getRandomCode();
        if(!CrowdUtils.strEffectiveCheck(randomCode)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_INVALID);
        }
        //2.检查手机号是否有效
        String phoneNum = memberVO.getPhoneNum();
        if(!CrowdUtils.strEffectiveCheck(phoneNum)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_PHONE_NUM_INVALID);
        }
        //3.拼接随机验证码的key
        String randomCodeKey = CrowdConstant.REDIS_RANDOM_CODE_PREFIX + phoneNum;
        //4.远程调用redis-provider的方法获取对应的验证码值
        ResultEntity<String> resultEntity = redisService.retrieveStringValueByStringKey(randomCodeKey);
        if(ResultEntity.FAILED.equals(resultEntity.getResult()))
            return  resultEntity;

        String randomCodeRedis = resultEntity.getData();
        //5.没有查询到值
        if(randomCodeRedis == null){
            return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
        }
        //进行比较
        if(!Objects.equals(randomCode,randomCodeRedis)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_MATCH);
        }
        //7.从redis中能够删除当前key对应的验证码
        ResultEntity<String> resultEntityRemoveByKey = redisService.removeByKey(randomCodeKey);
        if(ResultEntity.FAILED.equals(resultEntityRemoveByKey.getResult())){
            return resultEntityRemoveByKey;
        }
        //8.远程调用database-provider方法检查登录账号是否被占用
        String loginacct = memberVO.getLoginacct();
        ResultEntity<Integer> integerResultEntity = dataBaseService.retrieveLoignAcctCount(loginacct);
        if(ResultEntity.FAILED.equals(integerResultEntity.getResult())){
            return ResultEntity.failed(integerResultEntity.getMessage());
        }
        Integer count = integerResultEntity.getData();
        if(count > 0)
            //9.已经被占用，返回失败的消息
            return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        //10.密码加密
        String userpswd = memberVO.getUserpswd();
        String encode = passwordEncoder.encode(userpswd);
        memberVO.setUserpswd(encode);
        //11.远程调用database-provider方法执行保存操作
        MemberPO memberPO = new MemberPO();
        //调用spring提供的工具类直接完成属性值的注入
        BeanUtils.copyProperties(memberVO,memberPO);

        return dataBaseService.saveMemberRemote(memberPO);
    }

    //发送短信
    @RequestMapping("/member/manager/send/code")
    public ResultEntity<String> sendCode(@RequestParam("phoneNum") String phoneNum){

        if(!CrowdUtils.strEffectiveCheck(phoneNum)){
            return  ResultEntity.failed(CrowdConstant.MESSAGE_PHONE_NUM_INVALID);
        }
        //生成验证码
        int length = 4;
        String randomCode = CrowdUtils.randomCode(length);
        //保存到redis
        Integer timeoutMinute = 15;
        String normalKey = CrowdConstant.REDIS_RANDOM_CODE_PREFIX + phoneNum;
        ResultEntity<String> resultEntity = redisService.saveNormalStringKeyValue(normalKey, randomCode, timeoutMinute);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return resultEntity;
        }

        //发送验证码到用户的手机
        //String appcode = "270aae69805c48cfbf95bd7b1b44b657";
        try {
            CrowdUtils.sendShortMessage(appcode,randomCode,phoneNum);
            return ResultEntity.successNoData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }
}
