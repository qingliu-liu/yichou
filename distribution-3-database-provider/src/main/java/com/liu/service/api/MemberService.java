package com.liu.service.api;

import com.liu.entity.po.MemberPO;

public interface MemberService {
    int getLoginAcctCount(String loginacct);

    void saveMemberPO(MemberPO memberPO);

    MemberPO getMemberByLoginacct(String loginacct);
}
