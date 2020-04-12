package com.liu.api;

import com.liu.entity.po.MemberPO;
import com.liu.entity.ResultEntity;
import com.liu.entity.vo.ProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("database-provider")
public interface DataBaseOperationRemoteService {

    @RequestMapping("/retrieve/loign/acct/count")
    ResultEntity<Integer> retrieveLoignAcctCount(@RequestParam("loginacct") String loginacct);

    //一定要写@RequestBody注解，如果没写数据就传不过来
    @RequestMapping("/save/member/remote")
    ResultEntity<String> saveMemberRemote(@RequestBody MemberPO memberPO);

    @RequestMapping("/retrieve/member/by/login/acct")
    ResultEntity<MemberPO> retrieveMemberByLoginAcct(@RequestParam("loginacct") String loginacct);

    @RequestMapping("/save/project/remote/{memberId}")
    ResultEntity<String> saveProjectRemote(
            @RequestBody ProjectVO projectVO, @PathVariable("memberId") String memberId);
}
