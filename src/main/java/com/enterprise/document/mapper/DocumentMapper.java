package com.enterprise.document.mapper;

import com.enterprise.document.model.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档数据访问接口
 * 提供文档相关的数据库操作方法
 */
@Mapper
public interface DocumentMapper {
    
    /**
     * 统计文档总数
     * @return 文档总数
     */
    int countDocuments();
    
    /**
     * 查询所有文档，按编号排序
     * @return 文档列表
     */
    List<Document> findAll();
    
    /**
     * 根据文档编号查询文档
     * @param documentCode 文档编号
     * @return 文档对象，如果不存在返回null
     */
    Document findByCode(@Param("documentCode") String documentCode);
    
    /**
     * 插入新文档
     * @param document 文档对象
     * @return 影响的行数
     */
    int insert(Document document);
    
    /**
     * 更新文档信息
     * @param document 文档对象
     * @return 影响的行数
     */
    int update(Document document);
    
    /**
     * 根据文档编号删除文档
     * @param documentCode 文档编号
     * @return 影响的行数
     */
    int deleteByCode(@Param("documentCode") String documentCode);
    
    /**
     * 分页查询文档
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文档列表
     */
    List<Document> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据状态查询文档
     * @param status 文档状态
     * @return 文档列表
     */
    List<Document> findByStatus(@Param("status") String status);
}