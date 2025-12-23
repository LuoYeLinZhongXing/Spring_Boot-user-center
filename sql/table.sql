-- auto-generated definition
create table user
(
    id            bigint auto_increment comment '用户id'
        primary key,
    username      varchar(256)                 null comment '用户名',
    user_account  varchar(256)                 null comment '登录账号',
    avatar_url    varchar(1024)                not null comment '头像',
    gender        tinyint                      null comment '性别',
    user_password varchar(256)                 null comment '用户密码',
    phone         varchar(128)                 null comment '电话号码',
    email         varchar(256)                 null comment '邮箱',
    user_status   int      default 0           not null comment '用户状态:
0表示有效
1表示封号
2表示过期',
    create_time   datetime default (now())     not null comment '创建时间',
    update_time   datetime default (curtime()) not null comment '更新时间',
    is_delete     tinyint  default 0           not null comment '是否删除
0表示正常
1表示删除'
)
    comment '用户表';

