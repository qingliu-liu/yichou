package com.liu.service.impl;

import com.liu.entity.po.MemberPO;
import com.liu.entity.po.MemberPOExample;
import com.liu.mapper.MemberPOMapper;
import com.liu.service.api.MemberService;
import com.liu.util.CrowdUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    @Resource
    private MemberPOMapper memberPOMapper;

    @Override
    public int getLoginAcctCount(String loginacct) {
        MemberPOExample example = new MemberPOExample();
        example.createCriteria().andLoginacctEqualTo(loginacct);

        return (int) memberPOMapper.countByExample(example);
    }

    @Override
    @Transactional(readOnly=false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveMemberPO(MemberPO memberPO) {
        memberPOMapper.insert(memberPO);
    }

    @Override
    public MemberPO getMemberByLoginacct(String loginacct) {
        MemberPOExample example = new MemberPOExample();
        example.createCriteria().andLoginacctEqualTo(loginacct);
        List<MemberPO> memberPOS = memberPOMapper.selectByExample(example);
        if(CrowdUtils.collectionEffectiveCheck(memberPOS))
            return memberPOS.get(0);
        return null;
    }
}
