package com.enterprise.document.model;

/**
 * 用户信息类
 * 用于封装用户的基本信息
 */
public class User {
    
    private String userId;      // 工号/学号
    private String userName;    // 用户姓名
    
    // 默认构造方法
    public User() {
    }
    
    // 带参数的构造方法
    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
    
    // Getter和Setter方法
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
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        
        if (userId != null ? !userId.equals(user.userId) : user.userId != null) return false;
        
        return true;
    }
    
    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}