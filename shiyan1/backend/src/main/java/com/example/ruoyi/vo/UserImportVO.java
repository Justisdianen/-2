package com.example.ruoyi.vo;

import lombok.Data;

/**
 * 用户导入VO
 */
@Data
public class UserImportVO {
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String status;
    private String password;
    private Long deptId;
}