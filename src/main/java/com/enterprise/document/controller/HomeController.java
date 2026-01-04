package com.enterprise.document.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 主页控制器
 * 处理系统主页和功能模块导航
 */
@Controller
@RequestMapping("/")
public class HomeController {

    /**
     * 显示系统主页
     * 提供功能模块导航入口
     * 
     * @param model Spring MVC模型对象
     * @return 主页视图名称
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "企业文档管理系统");
        model.addAttribute("welcomeMessage", "欢迎使用企业文档管理系统");
        return "home";
    }

    /**
     * 显示关于页面
     * 
     * @param model Spring MVC模型对象
     * @return 关于页面视图名称
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "关于系统");
        model.addAttribute("systemInfo", "企业文档管理系统 v1.0");
        model.addAttribute("description", "为中小企业或高校项目组设计的轻量级Web文档管理平台");
        return "about";
    }
}