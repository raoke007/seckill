-- 数据库初始化脚本

-- 1. 创建数据库
create database seckill;

-- 2. 使用数据库
use seckill;

-- 3. 创建相关表
-- 1）秒杀商品表
create table seckill (

  `seckill_id` bigint not null auto_increment comment '商品id',
  `seckill_name` varchar(120) not null comment '商品名称',
  `seckill_number` int not null comment '商品库存',
  `start_time` timestamp not null comment '秒杀开始时间',
  `end_time` timestamp not null comment '秒杀结束时间',
  `create_time` timestamp not null default current_timestamp comment '创建时间',
  primary key (seckill_id),
  key idx_start_time (start_time),
  key idx_end_time (end_time),
  key idx_create_time (create_time)

) engine=InnoDB auto_increment=1000 default charset=utf8 comment='秒杀商品表';

-- 2）. 插入数据
insert into
  seckill (seckill_name, seckill_number, start_time, end_time)
values
  ('2000秒杀iPhone10', 20, '2018-09-11 00:00:00', '2018-09-13 00:00:00'),
  ('1000秒杀p20', 50, '2018-09-11 00:00:00', '2018-09-13 00:00:00'),
  ('800秒杀小米8', 80, '2018-09-11 00:00:00', '2018-09-13 00:00:00'),
  ('500秒杀红米note2', 20, '2018-09-11 00:00:00', '2018-09-13 00:00:00');


-- 3）秒杀成功明细表
create table success_seckilled (

  `seckill_id` bigint not null comment '商品id',
  `user_phone` bigint not null comment '用户手机号',
  `state` tinyint not null default 0 comment '状态 0-秒杀成功 1-已付款 2-已发货',
  `create_time` timestamp not null default current_timestamp comment '创建时间',
  primary key (seckill_id, user_phone),
  key idx_create_time (create_time)

) engine=InnoDB default charset=utf8 comment='秒杀成功明细表';