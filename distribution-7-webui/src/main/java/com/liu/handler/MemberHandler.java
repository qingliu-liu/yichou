package com.liu.handler;

import com.liu.api.MemberOperationRemoteService;
import com.liu.entity.ResultEntity;
import com.liu.entity.vo.MemberSignSuccessVO;
import com.liu.entity.vo.MemberVO;
import com.liu.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class MemberHandler {

    @Autowired
    private MemberOperationRemoteService memberOperationRemoteService;

    @RequestMapping("/member/logout.html")
    public String MemberLogout(HttpSession session){
        // 1.从现有Session中获取已登录的Member
        MemberSignSuccessVO memberSignSuccessVO = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);

        // 2.如果memberSignSuccessVO为null，则已经退出，不必继续执行
        if(memberSignSuccessVO  == null) {
            return "redirect:/";
        }

        // 3.获取token值
        String token = memberSignSuccessVO.getToken();

        // 4.调用远程方法删除Redis中存储的token
        ResultEntity<String> resultEntity = memberOperationRemoteService.logout(token);

        // 5.如果调用远程方法失败，抛出异常
        String result = resultEntity.getResult();

        if(ResultEntity.FAILED.equals(result)) {
            throw new RuntimeException(resultEntity.getMessage());
        }

        // 6.释放当前Session
        session.invalidate();

        return "redirect:/index.html";
    }

    @RequestMapping("/member/do/login.html")
    public String doLogin(MemberVO memberVO, Model model, HttpSession session){
        String loginacct = memberVO.getLoginacct();
        String userpswd = memberVO.getUserpswd();

        //调用远程方法执行登录的操作
        ResultEntity<MemberSignSuccessVO> login = memberOperationRemoteService.login(loginacct, userpswd);

        //检查远程方法调用结果
        String result = login.getResult();
        if(ResultEntity.FAILED.equals(result)){
            String message = login.getMessage();
            model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,message);
            return "member-login";
        }
        //如果登录成功则获取MemberSignSuccessVO对象
        MemberSignSuccessVO memberSignSuccessVO= login.getData();
        //将memberSignSuccessVO存入session域
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,memberSignSuccessVO);

        return "redirect:/member/to/member/center/page.html";
    }
}
