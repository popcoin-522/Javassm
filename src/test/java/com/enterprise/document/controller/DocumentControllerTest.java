package com.enterprise.document.controller;

import com.enterprise.document.model.Document;
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
 * DocumentController Web层集成测试
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
public class DocumentControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private DocumentService documentService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * 测试文档列表页面渲染 - 空列表情况
     * 需求: 1.1 - 显示所有文档信息
     */
    @Test
    public void testListDocuments_EmptyList_ShouldShowMessage() throws Exception {
        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/list"))
                .andExpect(model().attribute("title", "文档列表"))
                .andExpect(model().attribute("documents", hasSize(0)))
                .andExpect(model().attribute("message", "暂无文档"));
    }
    
    /**
     * 测试文档列表页面渲染 - 有文档情况
     * 需求: 1.1 - 显示所有文档信息
     */
    @Test
    public void testListDocuments_WithDocuments_ShouldShowDocuments() throws Exception {
        // 添加测试文档
        documentService.addDocument("DOC001", "测试文档1", "技术文档");
        documentService.addDocument("DOC002", "测试文档2", "业务文档");
        
        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/list"))
                .andExpect(model().attribute("title", "文档列表"))
                .andExpect(model().attribute("documents", hasSize(2)))
                .andExpect(model().attributeDoesNotExist("message"));
    }
    
    /**
     * 测试文档管理页面渲染
     * 需求: 8.1 - 显示Web主页面，包含功能模块
     */
    @Test
    public void testManageDocuments_ShouldRenderCorrectly() throws Exception {
        mockMvc.perform(get("/documents/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/manage"))
                .andExpect(model().attribute("title", "文档管理"))
                .andExpect(model().attributeExists("documents"));
    }
    
    /**
     * 测试添加文档表单页面渲染
     * 需求: 2.1 - 添加新文档
     */
    @Test
    public void testShowAddForm_ShouldRenderCorrectly() throws Exception {
        mockMvc.perform(get("/documents/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/add"))
                .andExpect(model().attribute("title", "添加文档"))
                .andExpect(model().attributeExists("document"));
    }
    
    /**
     * 测试添加文档表单提交 - 有效输入
     * 需求: 2.1 - 创建包含编号、名称、类型的文档记录
     */
    @Test
    public void testAddDocument_ValidInput_ShouldRedirectWithSuccess() throws Exception {
        mockMvc.perform(post("/documents/add")
                .param("documentCode", "DOC001")
                .param("documentName", "测试文档")
                .param("documentType", "技术文档"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents/manage"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 验证文档已创建
        Document created = documentService.getDocumentByCode("DOC001");
        assertNotNull(created);
        assertEquals("DOC001", created.getDocumentCode());
        assertEquals("测试文档", created.getDocumentName());
        assertEquals("可下载", created.getStatus());
    }
    
    /**
     * 测试添加文档表单提交 - 无效输入
     * 需求: 7.1 - 验证编号为非空字符串
     */
    @Test
    public void testAddDocument_InvalidInput_ShouldRedirectWithError() throws Exception {
        mockMvc.perform(post("/documents/add")
                .param("documentCode", "")  // 空编号
                .param("documentName", "测试文档")
                .param("documentType", "技术文档"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents/add"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    
    /**
     * 测试修改文档表单页面渲染
     * 需求: 2.2 - 修改文档信息
     */
    @Test
    public void testShowEditForm_ExistingDocument_ShouldRenderCorrectly() throws Exception {
        // 先添加文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(get("/documents/edit/DOC001"))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/edit"))
                .andExpect(model().attribute("title", "修改文档"))
                .andExpect(model().attributeExists("document"));
    }
    
    /**
     * 测试修改文档表单页面渲染 - 不存在的文档
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testShowEditForm_NonExistingDocument_ShouldShowError() throws Exception {
        mockMvc.perform(get("/documents/edit/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "文档不存在: NONEXISTENT"));
    }
    
    /**
     * 测试修改文档表单提交 - 有效输入
     * 需求: 2.2 - 更新指定文档的名称、类型或状态
     */
    @Test
    public void testUpdateDocument_ValidInput_ShouldRedirectWithSuccess() throws Exception {
        // 先添加文档
        documentService.addDocument("DOC001", "原始文档", "原始类型");
        
        mockMvc.perform(post("/documents/edit/DOC001")
                .param("documentName", "更新后的文档")
                .param("documentType", "更新后的类型")
                .param("status", "已借出"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents/manage"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 验证文档已更新
        Document updated = documentService.getDocumentByCode("DOC001");
        assertEquals("更新后的文档", updated.getDocumentName());
        assertEquals("更新后的类型", updated.getDocumentType());
        assertEquals("已借出", updated.getStatus());
    }
    
    /**
     * 测试删除文档 - 有效的归档文档
     * 需求: 2.3 - 仅删除状态为"归档"且无关联借阅记录的文档
     */
    @Test
    public void testDeleteDocument_ValidArchivedDocument_ShouldRedirectWithSuccess() throws Exception {
        // 添加并归档文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        documentService.updateDocument("DOC001", null, null, "归档");
        
        mockMvc.perform(post("/documents/delete/DOC001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents/manage"))
                .andExpect(flash().attributeExists("successMessage"));
        
        // 验证文档已删除
        Document deleted = documentService.getDocumentByCode("DOC001");
        assertNull(deleted);
    }
    
    /**
     * 测试删除文档 - 非归档文档
     * 需求: 2.4 - 拒绝删除并提示"只能删除归档状态的文档"
     */
    @Test
    public void testDeleteDocument_NonArchivedDocument_ShouldRedirectWithError() throws Exception {
        // 添加可下载文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(post("/documents/delete/DOC001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents/manage"))
                .andExpect(flash().attributeExists("errorMessage"));
        
        // 验证文档未被删除
        Document notDeleted = documentService.getDocumentByCode("DOC001");
        assertNotNull(notDeleted);
    }
    
    /**
     * 测试文档API接口
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testGetDocumentApi_ExistingDocument_ShouldReturnJson() throws Exception {
        // 添加文档
        documentService.addDocument("DOC001", "测试文档", "技术文档");
        
        mockMvc.perform(get("/documents/api/DOC001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
    
    /**
     * 测试文档API接口 - 不存在的文档
     * 需求: 8.4 - 显示操作结果并更新页面内容
     */
    @Test
    public void testGetDocumentApi_NonExistingDocument_ShouldReturnNull() throws Exception {
        mockMvc.perform(get("/documents/api/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}