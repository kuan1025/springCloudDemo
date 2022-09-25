create database `cloud_user`;
create table `tb_user`(
`id` int auto_increment primary key,
`username` varchar(10),
`address` varchar(100)
);
use `cloud_user`;
insert into `tb_user`(`username`,`address`) values('Ellie','台北市北投區');
insert into `tb_user`(`username`,`address`) values('Shun','台南市白河區');
insert into `tb_user`(`username`,`address`) values('Andy','高雄市鳳山區');
insert into `tb_user`(`username`,`address`) values('Sam','台中市北屯區');
insert into `tb_user`(`username`,`address`) values('Chen','新北市新莊區');
insert into `tb_user`(`username`,`address`) values('yoyo','台北市北投區');
create database `cloud_order`;
use `cloud_order`;
create table `tb_order`(
`id` int auto_increment primary key,
`user_id` int not null, 
`name` varchar(100),
`price` int,
`num` int
)auto_increment = 100 ;
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(1,'iphone 14 pro',30000,1);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(2,'Mac m2',130000,3);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(3,'i watch',10000,1);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(4,'ipad',23000,4);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(5,'switch',8000,6);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(6,'Mac m1',80000,6);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(2,'饗食天堂禮券',130000,30);
insert into `tb_order`(`user_id`,`name`,`price`,`num`) values(3,'蘋果',100,2);

