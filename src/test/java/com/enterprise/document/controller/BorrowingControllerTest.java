package com.enterprise.document.controller;

import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
import com.enterprise.document.service.BorrowingService;
import com.enterprise.document.service.DocumentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BorrowingController Web层集成测试
 * 使用MockMvc测试所有控制器方法、页面渲染和表单提交
 * 需求: 8.1, 8.2, 8.4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/spring-mvc.xml"
})
@WebAppConfiguration
@Transactional
public class BorrowingControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private DocumentService documentService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * 测试借阅管理主页面渲染
     * 需求: 8.1 - 显示Web主页面，包含功能模块
     */
    @Test
    public void testBorrowingHome_ShouldRenderCorrectly() throws Exception {
        mockMvc.perform(get("/borrowing"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowing/index"))
                .andExpect(model().attribute("title", "借阅管理"))
                .andExpect(model().attributeExists("documents"));
    }
    
    /**
     * 测试借阅表单页面渲染
     * 需求: 3.1 - 提供有效的工号/学号、姓名和文档编号进行借阅
     */
    @Test
    public void testShowBorrowForm_ShouldRenderCorrectly() throws Exception {
        mockMvc.perform(get("/borrowing/borrow"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowing/borrow"))
                .andExpect(model().attribute("title", "借阅文档"))
                .andExpect(model().attributeExists("documents"));
    }
    
    /**
     * 测试文档借阅表单提交 - 有效输入
     * 需求: 3.1, 3.2 - 检查文档状态并创建借阅记录
     */
    @Test
    public void testBorrowDocument_ValidInput_ShouldRedirectWithSuccess() throws Exception {
        // 添加可借阅文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(post("/borrowing/borrow")
                .param("documentCode", "DOC001")
                .param("userId", "U001")
                .param("userName", "张三"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowing"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 验证借阅记录已创建
        List<BorrowingRecord> records = borrowingService.getBorrowingRecordsByDocument("DOC001");
        assertEquals(1, records.size());
        assertEquals("U001", records.get(0).getUserId());
        assertEquals("张三", records.get(0).getUserName());
        
        // 验证文档状态已更新
        Document document = documentService.getDocumentByCode("DOC001");
        assertEquals("已借出", document.getStatus());
    }
    
    /**
     * 测试文档借阅表单提交 - 无效输入
     * 需求: 7.2 - 验证工号/学号为非空字符串
     */
    @Test
    public void testBorrowDocument_InvalidInput_ShouldRedirectWithError() throws Exception {
        // 添加可借阅文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(post("/borrowing/borrow")
                .param("documentCode", "DOC001")
                .param("userId", "")  // 空用户ID
                .param("userName", "张三"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowing/borrow"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    /**
     * 测试文档借阅 - 已借出文档
     * 需求: 3.3 - 拒绝借阅并提示"文档已借出"
     */
    @Test
    public void testBorrowDocument_AlreadyBorrowed_ShouldRedirectWithError() throws Exception {
        // 添加文档并借出
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        
        mockMvc.perform(post("/borrowing/borrow")
                .param("documentCode", "DOC001")
                .param("userId", "U002")
                .param("userName", "李四"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowing/borrow"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    /**
     * 测试归还表单页面渲染
     * 需求: 4.1 - 提供工号/学号、姓名和文档编号进行归还
     */
    @Test
    public void testShowReturnForm_ShouldRenderCorrectly() throws Exception {
        mockMvc.perform(get("/borrowing/return"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowing/return"))
                .andExpect(model().attribute("title", "归还文档"))
                .andExpect(model().attributeExists("activeRecords"));
    }
    
    /**
     * 测试文档归还表单提交 - 有效输入
     * 需求: 4.1, 4.2 - 验证借阅记录并更新状态
     */
    @Test
    public void testReturnDocument_ValidInput_ShouldRedirectWithSuccess() throws Exception {
        // 添加文档并借出
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        
        mockMvc.perform(post("/borrowing/return")
                .param("documentCode", "DOC001")
                .param("userId", "U001")
                .param("userName", "张三"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowing"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 验证文档状态已恢复
        Document document = documentService.getDocumentByCode("DOC001");
        assertEquals("可下载", document.getStatus());
        
        // 验证借阅记录已更新归还时间
        List<BorrowingRecord> records = borrowingService.getBorrowingRecordsByDocument("DOC001");
        assertEquals(1, records.size());
        assertNotNull(records.get(0).getReturnTime());
    }
    
    /**
     * 测试文档归还 - 无此借阅记录
     * 需求: 4.3 - 拒绝归还并提示"无此借阅记录"
     */
    @Test
    public void testReturnDocument_NoRecord_ShouldRedirectWithError() throws Exception {
        // 添加文档但不借出
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(post("/borrowing/return")
                .param("documentCode", "DOC001")
                .param("userId", "U001")
                .param("userName", "张三"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/borrowing/return"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    /**
     * 测试借阅记录页面渲染 - 空记录
     * 需求: 5.1 - 显示所有借阅记录
     */
    @Test
    public void testViewBorrowingRecords_EmptyList_ShouldShowMessage() throws Exception {
        mockMvc.perform(get("/borrowing/records"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowing/records"))
                .andExpect(model().attribute("title", "借阅记录"))
                .andExpect(model().attribute("records", hasSize(0)))
                .andExpect(model().attribute("message", "暂无借阅记录"));
    }
    
    /**
     * 测试借阅记录页面渲染 - 有记录
     * 需求: 5.1 - 显示所有借阅记录的完整信息
     */
    @Test
    public void testViewBorrowingRecords_WithRecords_ShouldShowRecords() throws Exception {
        // 添加文档并创建借阅记录
        documentService.addDocument("DOC001", "测试文档1", "技术文档");
        documentService.addDocument("DOC002", "测试文档2", "业务文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        borrowingService.borrowDocument("DOC002", "U002", "李四");
        
        mockMvc.perform(get("/borrowing/records"))
                .andExpect(status().isOk())
                .andExpect(view().name("borrowing/records"))
                .andExpect(model().attribute("title", "借阅记录"))
                .andExpect(model().attribute("records", hasSize(2)))
                .andExpect(model().attributeDoesNotExist("message"));
    }
    
    /**
     * 测试按文档编号查询借阅记录API
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testGetBorrowingRecordsByDocument_ShouldReturnJson() throws Exception {
        // 添加文档并创建借阅记录
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        
        mockMvc.perform(get("/borrowing/api/records/DOC001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
    
    /**
     * 测试按用户ID查询借阅记录API
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testGetBorrowingRecordsByUser_ShouldReturnJson() throws Exception {
        // 添加文档并创建借阅记录
        documentService.addDocument("DOC001", "测试文档1", "技术文档");
        documentService.addDocument("DOC002", "测试文档2", "业务文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        borrowingService.borrowDocument("DOC002", "U001", "张三");
        
        mockMvc.perform(get("/borrowing/api/user/U001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
    
    /**
     * 测试查询未归还借阅记录API
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testGetActiveBorrowingRecords_ShouldReturnJson() throws Exception {
        // 添加文档并创建借阅记录
        documentService.addDocument("DOC001", "测试文档1", "技术文档");
        documentService.addDocument("DOC002", "测试文档2", "业务文档");
        borrowingService.borrowDocument("DOC001", "U001", "张三");
        borrowingService.borrowDocument("DOC002", "U002", "李四");
        
        // 归还一个文档
        borrowingService.returnDocument("DOC001", "U001", "张三");
        
        mockMvc.perform(get("/borrowing/api/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
}