package com.liu.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PortalHandler {

    @RequestMapping("/index.html")
    public String shoePortalPage(){
        //加载真实数据，到页面显示
        return "portal-page";
    }
}
