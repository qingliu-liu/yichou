package com.liu.controller;

import com.alibaba.fastjson.JSON;
import com.liu.api.DataBaseOperationRemoteService;
import com.liu.api.RedisOperationRemoteService;
import com.liu.entity.ResultEntity;
import com.liu.entity.vo.MemberConfirmInfoVO;
import com.liu.entity.vo.ProjectVO;
import com.liu.entity.vo.ReturnVO;
import com.liu.util.CrowdConstant;
import com.liu.util.CrowdUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ProjectController {

    @Autowired
    private RedisOperationRemoteService redisOperationRemoteService;

    @Autowired
    private DataBaseOperationRemoteService dataBaseOperationRemoteService;

    @RequestMapping("/project/manager/save/whole/project")
    public ResultEntity<String> saveWholeProject(
            @RequestParam("memberSignToken") String memberSignToken,
            @RequestParam("projectTempToken") String projectTempToken){

        //检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }
        String memberId = resultEntity.getData();
        if(memberId == null){
            return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
        }

        //project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }

        //从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //将JSON字符串还原成ProjectVO对象
        ProjectVO projectVO = JSON.parseObject(projectVOJSON,ProjectVO.class);

        //执行保存
        ResultEntity<String> resultEntityForSave = dataBaseOperationRemoteService.saveProjectRemote(projectVO, memberId);
        if(ResultEntity.FAILED.equals(resultEntityForSave.getResult())){
            return resultEntityForSave;
        }

        //删除redis中的临时数据
        return redisOperationRemoteService.removeByKey(projectTempToken);
    }

    @RequestMapping("/project/manager/save/confirm/info")
    public ResultEntity<String> saveConfirmInfo(@RequestBody MemberConfirmInfoVO memberConfirmInfoVO){
        //获取memberSignToken
        String memberSignToken = memberConfirmInfoVO.getMemberSignToken();
        //检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }

        //从projectVOFront中获取projectTempToken
        String projectTempToken = memberConfirmInfoVO.getProjectTempToken();
        //project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }

        //从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //将JSON字符串还原成ProjectVO对象
        ProjectVO projectVOBehind = JSON.parseObject(projectVOJSON,ProjectVO.class);

        projectVOBehind.setMemberConfirmInfoVO(memberConfirmInfoVO);
        //重新对ProjectVO对象进行JSON转换
        String jsonString = JSON.toJSONString(projectVOBehind);

        //将JSON字符串重新存入Redis
        return redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);
    }

    @RequestMapping("/project/manager/save/return/info")
    public ResultEntity<String> saveReturnInfo(@RequestBody ReturnVO returnVO ){
        //获取memberSignToken
        String memberSignToken = returnVO.getMemberSignToken();
        //检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }

        //从projectVOFront中获取projectTempToken
        String projectTempToken = returnVO.getProjectTempToken();
        //project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }

        //从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //将JSON字符串还原成ProjectVO对象
        ProjectVO projectVOBehind = JSON.parseObject(projectVOJSON,ProjectVO.class);
        //获取旧的回报信息的集合
        List<ReturnVO> returnVOList = projectVOBehind.getReturnVOList();

        //判断returnVOList是否有数据
        if(!CrowdUtils.collectionEffectiveCheck(returnVOList)){
            //初始化
            returnVOList = new ArrayList<>();
            projectVOBehind.setReturnVOList(returnVOList);
        }
        //将当前的回报信息存入list
        returnVOList.add(returnVO);

        //重新对ProjectVO对象进行JSON转换
        String jsonString = JSON.toJSONString(projectVOBehind);

        //将JSON字符串重新存入Redis
        return redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);
    }

    @RequestMapping("/project/manager/save/project/info")
    public ResultEntity<String> saveProjectInfo(
            @RequestBody ProjectVO projectVO){
        //获取memberSignToken
        String memberSignToken = projectVO.getMemberSignToken();
        //检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }

        //从projectVOFront中获取projectTempToken
        String projectTempToken = projectVO.getProjectTempToken();
        //project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }

        //从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //将JSON字符串还原成ProjectVO对象
        ProjectVO projectVOBehind = JSON.parseObject(projectVOJSON,ProjectVO.class);

        projectVO.setHeaderPicturePath(projectVOBehind.getHeaderPicturePath());
        projectVO.setDetailPicturePathList(projectVOBehind.getDetailPicturePathList());

        //将projectVOFront对象中的属性复制到projectVOBehind中
        BeanUtils.copyProperties(projectVO,projectVOBehind);

        //将ProjectVO对象转换为JSON字符串
        String jsonString = JSON.toJSONString(projectVOBehind);

        //将JSON字符串重新存入Redis
        return redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);
    }

    @RequestMapping("/project/manager/save/detail/picture/path/list")
    public ResultEntity<String> saveDetailPicturePathList(
            @RequestParam("memberSignToken") String memberSignToken,
            @RequestParam("projectTempToken") String projectTempToken,
            @RequestParam("detailPicturePathList") List<String> detailPicturePathList
    ){
        //1.检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }

        //2.project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }
        //3.从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //4. 将JSON字符串还原成ProjectVO对象
        ProjectVO projectVO = JSON.parseObject(projectVOJSON,ProjectVO.class);

        //5.将图片路径存入ProjectVO对象
        projectVO.setDetailPicturePathList(detailPicturePathList);

        //6.将ProjectVO对象转换为JSON字符串
        String jsonString = JSON.toJSONString(projectVO);

        //7.将JSON字符串重新存入Redis
        return redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);
    }

    @RequestMapping("/project/manager/save/head/picture/path")
    public ResultEntity<String> saveHeadPicturePath(
            @RequestParam("memberSignToken") String memberSignToken,
            @RequestParam("projectTempToken") String projectTempToken,
            @RequestParam("headerPicturePath") String headerPicturePath
    ){
        //1.检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }

        //2.project-manager工程访问Redis查询ProjectVO对象
        ResultEntity<String> stringResultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(projectTempToken);
        if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
            return ResultEntity.failed(stringResultEntity.getMessage());
        }
        //3.从Redis查询到JSON字符串
        String projectVOJSON= stringResultEntity.getData();

        //4. 将JSON字符串还原成ProjectVO对象
        ProjectVO projectVO = JSON.parseObject(projectVOJSON,ProjectVO.class);

        //5.将图片路径存入ProjectVO对象
        projectVO.setHeaderPicturePath(headerPicturePath);

        //6.将ProjectVO对象转换为JSON字符串
        String jsonString = JSON.toJSONString(projectVO);

        //7.将JSON字符串重新存入Redis
        return redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);
    }

    @RequestMapping("/project/manager/initCreation")
    public ResultEntity<ProjectVO> initCreation(@RequestParam("memberSignToken") String memberSignToken){
        //1.检查memberSignToken是否有效，就是检查是否登录
        ResultEntity<String> resultEntity = redisOperationRemoteService.retrieveStringValueByStringKey(memberSignToken);
        if(ResultEntity.FAILED.equals(resultEntity.getResult())){
            return ResultEntity.failed(resultEntity.getMessage());
        }
        String memberId = resultEntity.getData();
        if(memberId == null){
            return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
        }

        //2.创建一个空的project对象备用
        ProjectVO projectVO = new ProjectVO();
        //3.将memberSignToken存入projectVO对象
        projectVO.setMemberSignToken(memberSignToken);
        //4.将projectTempToken存入projectVO对象
        String projectTempToken = CrowdUtils.generateRedisKeyByPrefix(CrowdConstant.REDIS_PROJECT_TEMP_TOKEN_PREFIX);
        projectVO.setProjectTempToken(projectTempToken);
        //5.将projectVO转换为json
        String jsonString = JSON.toJSONString(projectVO);
        //6.将projectTempToken和jsonString作为key value存入redis，不设置过期时间
        redisOperationRemoteService.saveNormalStringKeyValue(projectTempToken,jsonString,-1);

        return ResultEntity.successWithData(projectVO);
    }

}
