package com.liu.service.impl;

import com.liu.entity.po.*;
import com.liu.entity.vo.MemberConfirmInfoVO;
import com.liu.entity.vo.MemberLauchInfoVO;
import com.liu.entity.vo.ProjectVO;
import com.liu.entity.vo.ReturnVO;
import com.liu.mapper.*;
import com.liu.service.api.ProjectService;
import com.liu.util.CrowdUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
//@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private MemberConfirmInfoPOMapper memberConfirmInfoPOMapper;

    @Resource
    private MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;

    @Resource
    private ProjectItemPicPOMapper projectItemPicPOMapper;

    @Resource
    private ProjectPOMapper projectPOMapper;

    @Resource
    private ReturnPOMapper returnPOMapper;

    @Resource
    private TagPOMapper tagPOMapper;

    @Resource
    private TypePOMapper typePOMapper;

    @Override
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void saveProject(ProjectVO projectVO, String memberId) {
        //1.保存ProjectVO
        ProjectPO projectPO = new ProjectPO();
        BeanUtils.copyProperties(projectVO,projectPO);
        projectPO.setMemberid(Integer.parseInt(memberId));
        projectPOMapper.insert(projectPO);

        //2.获取保存ProjectPO得到的自增主键,需要在projectPOmapper.xml文件中设置useGeneratedKeys="true" keyProperty="id"
        Integer projectId = projectPO.getId();

        //3.保存typeIdList
        List<Integer> typeIdList = projectVO.getTypeIdList();
        if(CrowdUtils.collectionEffectiveCheck(typeIdList)){
            typePOMapper.insertRelationshipBatch(projectId,typeIdList);
        }

        //4.保存tagIdList
        List<Integer> tagIdList = projectVO.getTagIdList();
        if (CrowdUtils.collectionEffectiveCheck(tagIdList)) {
            tagPOMapper.insertRelationshipBatch(projectId,tagIdList);
        }

        //5.保存detailPicturePathList
        //①从vo对象中获取detailPicturePathList
        List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
        if(CrowdUtils.collectionEffectiveCheck(detailPicturePathList)){
            //②创建空的list集合，用来存储ProjectItemPicPO对象
            List<ProjectItemPicPO> projectItemPicPOList = new ArrayList<>();
            //③遍历detailPicturePathList
            for (String detailPath:detailPicturePathList) {
                //④创建ProjectItemPicPO对象
                ProjectItemPicPO projectItemPicPO = new ProjectItemPicPO(null,projectId,detailPath);
                projectItemPicPOList.add(projectItemPicPO);
            }
            //⑤根据projectItemPicPOList执行批量保存
            projectItemPicPOMapper.insertBatch(projectItemPicPOList);
        }

        //6.保存memberLauchInfoPO
        MemberLauchInfoVO memberLauchInfoVO = projectVO.getMemberLauchInfoVO();
        if(memberLauchInfoVO != null){
            MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
            BeanUtils.copyProperties(memberLauchInfoVO,memberLaunchInfoPO);
            memberLaunchInfoPO.setMemberid(Integer.parseInt(memberId));
            memberLaunchInfoPOMapper.insert(memberLaunchInfoPO);
        }


        //7.根据ReturnVO的list保存ReturnPO
        List<ReturnVO> returnVOList = projectVO.getReturnVOList();
        if(CrowdUtils.collectionEffectiveCheck(returnVOList)){
            List<ReturnPO> returnPOList =new ArrayList<>();
            for (ReturnVO returnVO:returnVOList) {
                ReturnPO returnPO = new ReturnPO();
                BeanUtils.copyProperties(returnVO,returnPO);  //属性复制
                returnPO.setProjectid(projectId);
                returnPOList.add(returnPO);
            }
            returnPOMapper.insertBatch(returnPOList);
        }


        //8.memberConfirmInfoPO
        MemberConfirmInfoVO memberConfirmInfoVO = projectVO.getMemberConfirmInfoVO();
        if(memberConfirmInfoVO != null){
            MemberConfirmInfoPO memberConfirmInfoPO = new MemberConfirmInfoPO(null,Integer.parseInt(memberId),memberConfirmInfoVO.getPaynum(),memberConfirmInfoVO.getCardnum());
            memberConfirmInfoPOMapper.insert(memberConfirmInfoPO);
        }


    }
}
