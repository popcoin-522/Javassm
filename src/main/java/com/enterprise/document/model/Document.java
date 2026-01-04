package com.enterprise.document.model;

import java.sql.Timestamp;

/**
 * 文档实体类
 * 对应数据库中的documents表
 */
public class Document {
    
    private Long id;                    // 主键ID
    private String documentCode;        // 文档编号
    private String documentName;        // 文档名称
    private String documentType;        // 文档类型
    private String status;              // 文档状态：可下载、已借出、归档
    private Timestamp createdTime;      // 创建时间
    private Timestamp updatedTime;      // 更新时间
    
    // 默认构造方法
    public Document() {
    }
    
    // 带参数的构造方法
    public Document(String documentCode, String documentName, String documentType) {
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.documentType = documentType;
        this.status = "可下载"; // 默认状态
    }
    
    // 完整构造方法
    public Document(Long id, String documentCode, String documentName, String documentType, 
                   String status, Timestamp createdTime, Timestamp updatedTime) {
        this.id = id;
        this.documentCode = documentCode;
        this.documentName = documentName;
        this.documentType = documentType;
        this.status = status;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDocumentCode() {
        return documentCode;
    }
    
    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
    
    public Timestamp getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", documentCode='" + documentCode + '\'' +
                ", documentName='" + documentName + '\'' +
                ", documentType='" + documentType + '\'' +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Document document = (Document) o;
        
        if (documentCode != null ? !documentCode.equals(document.documentCode) : document.documentCode != null)
            return false;
        
        return true;
    }
    
    @Override
    public int hashCode() {
        return documentCode != null ? documentCode.hashCode() : 0;
    }
}