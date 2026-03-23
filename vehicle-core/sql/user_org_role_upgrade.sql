-- 用户模块升级：登录扩展 + 组织管理 + RBAC + 数据权限
-- 数据库：ivos

USE `ivos`;

-- 1) 扩展 user 表（若列已存在请忽略对应语句）
ALTER TABLE `user`
    ADD COLUMN `org_id` BIGINT NULL COMMENT '组织ID' AFTER `parent_id`,
    ADD COLUMN `role_code` VARCHAR(64) NULL COMMENT '角色编码' AFTER `org_id`,
    ADD COLUMN `enterprise_id` BIGINT NULL COMMENT '企业ID' AFTER `role_code`,
    ADD COLUMN `company_id` BIGINT NULL COMMENT '子公司ID' AFTER `enterprise_id`,
    ADD COLUMN `dept_id` BIGINT NULL COMMENT '部门ID' AFTER `company_id`;

CREATE INDEX `idx_user_org_id` ON `user` (`org_id`);
CREATE INDEX `idx_user_role_code` ON `user` (`role_code`);
CREATE INDEX `idx_user_enterprise` ON `user` (`enterprise_id`);
CREATE INDEX `idx_user_company` ON `user` (`company_id`);
CREATE INDEX `idx_user_dept` ON `user` (`dept_id`);

-- 2) 组织表：三级（总部/子公司/部门）
CREATE TABLE IF NOT EXISTS `sys_org` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `org_name` VARCHAR(100) NOT NULL,
  `org_type` VARCHAR(20) NOT NULL COMMENT 'HQ/COMPANY/DEPT',
  `parent_id` BIGINT NOT NULL DEFAULT 0,
  `org_level` INT NOT NULL COMMENT '1-总部 2-子公司 3-部门',
  `enterprise_id` BIGINT NULL,
  `company_id` BIGINT NULL,
  `leader_user_id` BIGINT NULL,
  `sort` INT NOT NULL DEFAULT 10,
  `status` VARCHAR(2) NOT NULL DEFAULT '1',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_org_parent` ON `sys_org` (`parent_id`);
CREATE INDEX `idx_org_enterprise` ON `sys_org` (`enterprise_id`);
CREATE INDEX `idx_org_company` ON `sys_org` (`company_id`);

-- 3) 角色权限表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(64) NOT NULL,
  `role_name` VARCHAR(100) NOT NULL,
  `menu_perms` TEXT NULL COMMENT '菜单权限编码，逗号分隔',
  `data_scope` VARCHAR(20) NOT NULL DEFAULT 'SELF' COMMENT 'ALL/ENTERPRISE/COMPANY/DEPT/SELF',
  `status` VARCHAR(2) NOT NULL DEFAULT '1',
  `remark` VARCHAR(255) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) 初始角色（可重复执行）
INSERT INTO `sys_role` (`role_code`, `role_name`, `menu_perms`, `data_scope`, `status`, `remark`, `create_time`)
SELECT 'ROLE_DISPATCHER', '车管调度员',
       'dashboard:view,vehicle:view,vehicle:maintain:view,geofence:view,user:manage,org:manage,role:manage,application:view,audit:view,dict:manage',
       'ALL', '1', '全量系统权限', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code` = 'ROLE_DISPATCHER');

INSERT INTO `sys_role` (`role_code`, `role_name`, `menu_perms`, `data_scope`, `status`, `remark`, `create_time`)
SELECT 'ROLE_MANAGER', '业务主管',
       'application:view,audit:view',
       'COMPANY', '1', '审批与业务可见范围：同子公司', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code` = 'ROLE_MANAGER');

INSERT INTO `sys_role` (`role_code`, `role_name`, `menu_perms`, `data_scope`, `status`, `remark`, `create_time`)
SELECT 'ROLE_EMPLOYEE', '基层员工',
       'application:view',
       'SELF', '1', '仅本人数据', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code` = 'ROLE_EMPLOYEE');

-- 5) 给已存在用户补一个默认角色（按 level）
UPDATE `user`
SET `role_code` = CASE
    WHEN `level` = '99' THEN 'ROLE_DISPATCHER'
    WHEN `level` IN ('20','30','40','50') THEN 'ROLE_MANAGER'
    ELSE 'ROLE_EMPLOYEE'
END
WHERE `role_code` IS NULL OR `role_code` = '';
