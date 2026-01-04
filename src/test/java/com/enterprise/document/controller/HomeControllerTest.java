package com.enterprise.document.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HomeController Web层集成测试
 * 使用MockMvc测试控制器方法、页面渲染和数据传递
 * 需求: 8.1, 8.2, 8.4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/spring-mvc.xml"
})
@WebAppConfiguration
public class HomeControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * 测试主页面渲染和数据传递
     * 需求: 8.1 - 显示Web主页面，包含功能模块
     */
    @Test
    public void testHomePage_ShouldRenderWithCorrectData() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("title", "企业文档管理系统"))
                .andExpect(model().attribute("welcomeMessage", "欢迎使用企业文档管理系统"));
    }
    
    /**
     * 测试/home路径的主页面渲染
     * 需求: 8.1 - 显示Web主页面，包含功能模块
     */
    @Test
    public void testHomePageAlternatePath_ShouldRenderWithCorrectData() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("title", "企业文档管理系统"))
                .andExpect(model().attribute("welcomeMessage", "欢迎使用企业文档管理系统"));
    }
    
    /**
     * 测试关于页面渲染和数据传递
     * 需求: 8.2 - 提供明确的操作结果反馈
     */
    @Test
    public void testAboutPage_ShouldRenderWithCorrectData() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attribute("title", "关于系统"))
                .andExpect(model().attribute("systemInfo", "企业文档管理系统 v1.0"))
                .andExpect(model().attribute("description", "为中小企业或高校项目组设计的轻量级Web文档管理平台"));
    }
}