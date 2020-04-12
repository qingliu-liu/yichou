package com.liu.api;

import com.liu.entity.ResultEntity;
import com.liu.entity.vo.MemberSignSuccessVO;
import com.liu.entity.vo.MemberVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("member-manager")
public interface MemberOperationRemoteService {

    @RequestMapping("/member/logout")
    public ResultEntity<String> logout(@RequestParam("token") String token);

    @RequestMapping("/member/manager/login")
    public ResultEntity<MemberSignSuccessVO> login(@RequestParam("loginacct") String loginacct ,
                                                   @RequestParam("userpswd") String userpswd);

    @RequestMapping("/member/manager/register")
    public ResultEntity<String> register(@RequestBody MemberVO memberVO);

    @RequestMapping("/member/manager/send/code")
    public ResultEntity<String> sendCode(@RequestParam("phoneNum") String phoneNum);
}
