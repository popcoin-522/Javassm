package com.enterprise.document.mapper;

import com.enterprise.document.model.BorrowingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录数据访问接口
 * 提供借阅记录相关的数据库操作方法
 */
@Mapper
public interface BorrowingRecordMapper {
    
    /**
     * 统计借阅记录总数
     * @return 借阅记录总数
     */
    int countRecords();
    
    /**
     * 查询所有借阅记录，按借阅时间倒序排列
     * @return 借阅记录列表
     */
    List<BorrowingRecord> findAll();
    
    /**
     * 根据文档编号查询借阅记录
     * @param documentCode 文档编号
     * @return 借阅记录列表
     */
    List<BorrowingRecord> findByDocumentCode(@Param("documentCode") String documentCode);
    
    /**
     * 根据用户ID查询借阅记录
     * @param userId 工号/学号
     * @return 借阅记录列表
     */
    List<BorrowingRecord> findByUserId(@Param("userId") String userId);
    
    /**
     * 根据借阅状态查询记录
     * @param status 借阅状态
     * @return 借阅记录列表
     */
    List<BorrowingRecord> findByStatus(@Param("status") String status);
    
    /**
     * 查询特定用户对特定文档的未归还借阅记录
     * @param documentCode 文档编号
     * @param userId 工号/学号
     * @param userName 用户姓名
     * @return 借阅记录，如果不存在返回null
     */
    BorrowingRecord findActiveBorrowingRecord(@Param("documentCode") String documentCode, 
                                            @Param("userId") String userId, 
                                            @Param("userName") String userName);
    
    /**
     * 插入新的借阅记录
     * @param record 借阅记录对象
     * @return 影响的行数
     */
    int insert(BorrowingRecord record);
    
    /**
     * 更新借阅记录（主要用于归还操作）
     * @param record 借阅记录对象
     * @return 影响的行数
     */
    int update(BorrowingRecord record);
    
    /**
     * 检查文档是否有关联的借阅记录
     * @param documentCode 文档编号
     * @return 记录数量
     */
    int countByDocumentCode(@Param("documentCode") String documentCode);
    
    /**
     * 分页查询借阅记录
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 借阅记录列表
     */
    List<BorrowingRecord> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
}