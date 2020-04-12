package com.liu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {
    /*相当于下面两种配置
    *  //<mvc:view-controller path="" view-name = "">
    @RequestMapping("/to/login/page.html")
    public String toLoginPage(){
        return "member-login";
    }*/
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String urlPath = "/to/login/page.html";
        String viewName = "member-login";
        registry.addViewController(urlPath).setViewName(viewName);

        urlPath = "/member/to/member/center/page.html";
        viewName = "member-center";
        registry.addViewController(urlPath).setViewName(viewName);

        urlPath = "/project/to/agree/page.html";
        viewName = "project-1-start";
        registry.addViewController(urlPath).setViewName(viewName);


    }
}
