DROP DATABASE IF EXISTS qq_db;
CREATE DATABASE IF NOT EXISTS qq_db;

USE qq_db;

SHOW TABLES;



CREATE TABLE IF NOT EXISTS user
(
    id                                 BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    qq_email                            VARCHAR(128) NOT NULL                                                           COMMENT '用户邮箱',
    qq_id                               VARCHAR(128) NOT NULL                                                           COMMENT '用户账号',
    username                            VARCHAR(256) NOT NULL                                                           COMMENT '用户名',
    password                            VARCHAR(128)                                                                    COMMENT '用户密码',
    avatar_url                          VARCHAR(512)                                                                    COMMENT '用户头像',
    gender                              TINYINT                                                                         COMMENT '用户性别：0=未知，1=男，2=女',
    introduction                        TEXT                                                                            COMMENT '用户简介',
    last_login_time                     DATETIME                                                                        COMMENT '最后在线时间',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_qq_email(qq_email),
    UNIQUE uk_qq_id(qq_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户表';



CREATE TABLE IF NOT EXISTS role
(
    id                                  BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    code                                VARCHAR(128) NOT NULL                                                           COMMENT '角色编码',
    name                                VARCHAR(128) NOT NULL                                                           COMMENT '角色名称',
    description                         VARCHAR(256)                                                                    COMMENT '角色描述',
    level                               INT                     DEFAULT 0                                               COMMENT '角色等级',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_code(code, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色表';



CREATE TABLE IF NOT EXISTS user_roles
(
    id                                 BIGINT PRIMARY KEY AUTO_INCREMENT                                                COMMENT '主键ID',

    user_id                            BIGINT NOT NULL                                                                  COMMENT '用户ID',
    role_id                            BIGINT NOT NULL                                                                  COMMENT '角色ID',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_user_id_role_id(user_id, role_id, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户角色关系表';



CREATE TABLE IF NOT EXISTS permission
(
    id                                  BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    code                                VARCHAR(128) NOT NULL                                                           COMMENT '权限编码',
    name                                VARCHAR(128) NOT NULL                                                           COMMENT '权限名称',
    type                                TINYINT                 DEFAULT 1                                               COMMENT '权限类型：1=API，2=WEB，3=BOT',
    description                         VARCHAR(256)                                                                    COMMENT '权限描述',
    sort_order                          INT                     DEFAULT 0                                               COMMENT '排序顺序',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_code(code, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '权限表';



CREATE TABLE IF NOT EXISTS role_permissions
(
    id                                  BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    role_id                             BIGINT NOT NULL                                                                 COMMENT '角色ID',
    permission_id                       BIGINT NOT NULL                                                                 COMMENT '权限ID',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_role_id_permission_id(role_id, permission_id, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色权限关系表';



CREATE TABLE IF NOT EXISTS minecraft_log_level
(
    id                                  BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    code                                VARCHAR(128) NOT NULL                                                           COMMENT '日志级别编码',
    name                                VARCHAR(128) NOT NULL                                                           COMMENT '日志级别名称',
    description                         VARCHAR(256)                                                                    COMMENT '日志级别描述',
    is_enabled                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '激活状态：0=未启用，1=已启用',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_code(code, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT 'MC日志级别表';



CREATE TABLE IF NOT EXISTS qq_group_permissions
(
    id                                  BIGINT PRIMARY KEY AUTO_INCREMENT                                               COMMENT '主键ID',

    qq_group_id                         VARCHAR(128) NOT NULL                                                           COMMENT 'QQ群号',
    permission_id                       BIGINT NOT NULL                                                                 COMMENT '权限ID',

    -- 状态字段
    status                              TINYINT NOT NULL        DEFAULT 1                                               COMMENT '数据状态：0=禁用，1=正常，2=锁定',
    is_deleted                          TINYINT NOT NULL        DEFAULT 0                                               COMMENT '逻辑删除：0=未删除，1=已删除',
    version                             INT NOT NULL            DEFAULT 0                                               COMMENT '乐观锁版本号',
    -- 审计字段
    create_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP                               COMMENT '创建时间',
    update_time                         DATETIME NOT NULL       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '更新时间',
    create_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '创建者ID（0表示系统）',
    update_by                           BIGINT NOT NULL         DEFAULT 0                                               COMMENT '修改者ID（0表示系统）',
    -- 唯一性保证
    unique_if_active                    TINYINT GENERATED ALWAYS AS (IF(is_deleted = 0, 1, NULL)) STORED                COMMENT '唯一性标记',

    UNIQUE uk_qq_group_id_permission_id(qq_group_id, permission_id, unique_if_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT 'QQ群权限关系表';
