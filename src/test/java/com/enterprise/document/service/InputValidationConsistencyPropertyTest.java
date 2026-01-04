package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 7: 输入验证一致性**
 * **验证: 需求 7.1, 7.2, 7.3**
 * 
 * 属性测试：对于任何用户输入，系统应该验证字符串非空且长度符合规定
 * （文档编号/工号≤20字符，姓名≤10字符）
 */
public class InputValidationConsistencyPropertyTest {
    
    private ValidationService validationService;
    private Random random;
    
    @BeforeMethod
    public void setUp() {
        validationService = new ValidationService();
        random = new Random();
    }
    
    /**
     * 属性测试：文档编号验证一致性
     * 对于任何长度≤20的非空字符串，验证应该通过
     * 对于任何长度>20的字符串或空字符串，验证应该失败
     */
    @Test(invocationCount = 100)
    public void testDocumentCodeValidationConsistency() {
        // 生成随机长度的字符串
        int length = random.nextInt(50); // 0到49的长度
        String input = generateRandomString(length);
        
        try {
            validationService.validateDocumentCode(input);
            // 如果验证通过，输入应该是非空且长度≤20
            assertTrue(input != null && !input.trim().isEmpty() && input.length() <= 20,
                    "验证通过的文档编号应该非空且长度≤20，但实际输入为: '" + input + "', 长度: " + input.length());
        } catch (ValidationException e) {
            // 如果验证失败，输入应该是空的或长度>20
            assertTrue(input == null || input.trim().isEmpty() || input.length() > 20,
                    "验证失败的文档编号应该为空或长度>20，但实际输入为: '" + input + "', 长度: " + (input != null ? input.length() : 0));
        }
    }
    
    /**
     * 属性测试：工号/学号验证一致性
     * 对于任何长度≤20的非空字符串，验证应该通过
     * 对于任何长度>20的字符串或空字符串，验证应该失败
     */
    @Test(invocationCount = 100)
    public void testUserIdValidationConsistency() {
        // 生成随机长度的字符串
        int length = random.nextInt(50); // 0到49的长度
        String input = generateRandomString(length);
        
        try {
            validationService.validateUserId(input);
            // 如果验证通过，输入应该是非空且长度≤20
            assertTrue(input != null && !input.trim().isEmpty() && input.length() <= 20,
                    "验证通过的工号/学号应该非空且长度≤20，但实际输入为: '" + input + "', 长度: " + input.length());
        } catch (ValidationException e) {
            // 如果验证失败，输入应该是空的或长度>20
            assertTrue(input == null || input.trim().isEmpty() || input.length() > 20,
                    "验证失败的工号/学号应该为空或长度>20，但实际输入为: '" + input + "', 长度: " + (input != null ? input.length() : 0));
        }
    }
    
    /**
     * 属性测试：姓名验证一致性
     * 对于任何长度≤10的非空有效字符串，验证应该通过
     * 对于任何长度>10的字符串、空字符串或包含无效字符的字符串，验证应该失败
     */
    @Test(invocationCount = 100)
    public void testUserNameValidationConsistency() {
        // 生成随机长度和类型的字符串
        int length = random.nextInt(30); // 0到29的长度
        String input = generateRandomNameString(length);
        
        try {
            validationService.validateUserName(input);
            // 如果验证通过，输入应该是非空、长度≤10且只包含中英文数字
            assertTrue(input != null && !input.trim().isEmpty() && input.length() <= 10,
                    "验证通过的姓名应该非空且长度≤10，但实际输入为: '" + input + "', 长度: " + input.length());
            assertTrue(isValidNameCharacters(input),
                    "验证通过的姓名应该只包含中英文数字，但实际输入为: '" + input + "'");
        } catch (ValidationException e) {
            // 如果验证失败，输入应该是空的、长度>10或包含无效字符
            boolean shouldFail = input == null || input.trim().isEmpty() || 
                               input.length() > 10 || !isValidNameCharacters(input);
            assertTrue(shouldFail,
                    "验证失败的姓名应该为空、长度>10或包含无效字符，但实际输入为: '" + input + "', 长度: " + 
                    (input != null ? input.length() : 0) + ", 字符有效性: " + isValidNameCharacters(input));
        }
    }
    
    /**
     * 边界测试：测试边界值的一致性
     */
    @Test(invocationCount = 50)
    public void testBoundaryValueConsistency() {
        // 测试文档编号和工号的20字符边界
        String exactly20Chars = generateRandomString(20);
        String exactly21Chars = generateRandomString(21);
        
        try {
            validationService.validateDocumentCode(exactly20Chars);
            // 20字符应该通过
        } catch (ValidationException e) {
            fail("20字符的文档编号应该验证通过，但失败了: " + exactly20Chars);
        }
        
        try {
            validationService.validateDocumentCode(exactly21Chars);
            fail("21字符的文档编号应该验证失败，但通过了: " + exactly21Chars);
        } catch (ValidationException e) {
            // 21字符应该失败
        }
        
        // 测试姓名的10字符边界
        String exactly10CharsName = generateValidNameString(10);
        String exactly11CharsName = generateValidNameString(11);
        
        try {
            validationService.validateUserName(exactly10CharsName);
            // 10字符应该通过
        } catch (ValidationException e) {
            fail("10字符的姓名应该验证通过，但失败了: " + exactly10CharsName);
        }
        
        try {
            validationService.validateUserName(exactly11CharsName);
            fail("11字符的姓名应该验证失败，但通过了: " + exactly11CharsName);
        } catch (ValidationException e) {
            // 11字符应该失败
        }
    }
    
    /**
     * 生成随机字符串
     */
    private String generateRandomString(int length) {
        if (length == 0) {
            return random.nextBoolean() ? "" : null; // 随机返回空字符串或null
        }
        
        StringBuilder sb = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789中文测试";
        
        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < 0.1) { // 10%概率添加空格
                sb.append(' ');
            } else {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 生成随机姓名字符串（可能包含无效字符）
     */
    private String generateRandomNameString(int length) {
        if (length == 0) {
            return random.nextBoolean() ? "" : null;
        }
        
        StringBuilder sb = new StringBuilder();
        // 包含有效和无效字符
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789中文测试";
        String invalidChars = " @#$%^&*()_+-=[]{}|;':\",./<>?";
        String allChars = validChars + invalidChars;
        
        for (int i = 0; i < length; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成有效的姓名字符串
     */
    private String generateValidNameString(int length) {
        if (length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789中文测试";
        
        for (int i = 0; i < length; i++) {
            sb.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 检查字符串是否只包含有效的姓名字符
     */
    private boolean isValidNameCharacters(String input) {
        if (input == null) {
            return false;
        }
        return input.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
    }
}