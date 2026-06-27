package com.example.ruoyi.vo;

import lombok.Data;

/**
 * 用户导出VO
 */
@Data
public class UserExportVO {
    private Long userId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String status;
    private Long deptId;
    private String deptName;
    private String createTime;
}