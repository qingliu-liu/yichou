package com.liu.controller;

import com.liu.entity.po.MemberPO;
import com.liu.entity.ResultEntity;
import com.liu.service.api.MemberService;
import com.liu.util.CrowdConstant;
import com.liu.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/retrieve/member/by/login/acct")
    public ResultEntity<MemberPO> retrieveMemberByLoginAcct(@RequestParam("loginacct") String loginacct){

        try {
            MemberPO memberPO = memberService.getMemberByLoginacct(loginacct);
            return  ResultEntity.successWithData(memberPO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/retrieve/loign/acct/count")
    public ResultEntity<Integer> retrieveLoignAcctCount(@RequestParam("loginacct") String loginacct){

        if(!CrowdUtils.strEffectiveCheck(loginacct)){
            return ResultEntity.failed(CrowdConstant.MESSAGE_LOGINACCT_INVALID);
        }

        try {
            int count = memberService.getLoginAcctCount(loginacct);
            return ResultEntity.successWithData(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    //一定要写@RequestBody注解，如果没写数据就传不过来
    @RequestMapping("/save/member/remote")
    public ResultEntity<String> saveMemberRemote(@RequestBody MemberPO memberPO){
        try {
            // 执行保存
            memberService.saveMemberPO(memberPO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }

        return ResultEntity.successNoData();
    }

}
