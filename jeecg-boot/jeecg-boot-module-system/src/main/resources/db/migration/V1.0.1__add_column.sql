-- Flyway 数据库版本管理 https://flywaydb.org/documentation/migrations#overview

alter table sys_user
    add large_order_limit decimal(8, 2) null comment '大额订单限额' after order_fixed_fee;
alter table sys_user
    add large_order_fee decimal(8, 2) null comment '大额订单手续费' after large_order_limit;