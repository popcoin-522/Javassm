package com.enterprise.document.model;

import java.sql.Timestamp;

/**
 * 借阅记录实体类
 * 对应数据库中的borrowing_records表
 */
public class BorrowingRecord {
    
    private Long id;                    // 主键ID
    private String documentCode;        // 文档编号
    private String userId;              // 工号/学号
    private String userName;            // 用户姓名
    private Timestamp borrowTime;       // 借阅时间
    private Timestamp returnTime;       // 归还时间
    private String status;              // 借阅状态：借阅中、已归还
    
    // 默认构造方法
    public BorrowingRecord() {
    }
    
    // 带参数的构造方法（用于创建新的借阅记录）
    public BorrowingRecord(String documentCode, String userId, String userName) {
        this.documentCode = documentCode;
        this.userId = userId;
        this.userName = userName;
        this.status = "借阅中"; // 默认状态
    }
    
    // 完整构造方法
    public BorrowingRecord(Long id, String documentCode, String userId, String userName,
                          Timestamp borrowTime, Timestamp returnTime, String status) {
        this.id = id;
        this.documentCode = documentCode;
        this.userId = userId;
        this.userName = userName;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.status = status;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Timestamp getBorrowTime() {
        return borrowTime;
    }
    
    public void setBorrowTime(Timestamp borrowTime) {
        this.borrowTime = borrowTime;
    }
    
    public Timestamp getReturnTime() {
        return returnTime;
    }
    
    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "id=" + id +
                ", documentCode='" + documentCode + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", borrowTime=" + borrowTime +
                ", returnTime=" + returnTime +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BorrowingRecord that = (BorrowingRecord) o;
        
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (documentCode != null ? !documentCode.equals(that.documentCode) : that.documentCode != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (borrowTime != null ? !borrowTime.equals(that.borrowTime) : that.borrowTime != null) return false;
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (documentCode != null ? documentCode.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (borrowTime != null ? borrowTime.hashCode() : 0);
        return result;
    }
}