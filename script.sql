drop table if exists jobs;

create table jobs(
	id integer not null auto_increment primary key,
	worker_id integer,
	work_duration integer not null,
	start_time timestamp not null,
	started_time timestamp,
	Completed_time timestamp,
	last_status_time timestamp not null default current_timestamp,
	status enum('Ready', 'InProgress', 'Completed', 'Error') not null
);

insert into jobs (id, worker_id, work_duration, start_time, started_time, Completed_time, last_status_time, status) 
	values (100, null, 60, '2018-07-27 11-33-00', '2018-07-27 10-59-56', null, '2018-07-27 11-03-32', 'InProgress');
insert into jobs (id, worker_id, work_duration, start_time, started_time, Completed_time, last_status_time, status) 
	values (101, null, 67, '2018-07-27 11-12-00', '2018-07-27 11-00-00', null, '2018-07-27 11-12-33', 'InProgress');
insert into jobs (id, worker_id, work_duration, start_time, started_time, Completed_time, last_status_time, status) 
	values (102, null, 43, '2018-07-27 12-33-00', '2018-07-27 12-03-22', '2018-07-27 12-37-55', '2018-07-27 12-36-00', 'Completed');
insert into jobs (id, worker_id, work_duration, start_time, started_time, Completed_time, last_status_time, status) 
	values (103, null, 87, '2018-07-27 11-33-00', null, null, null, 'Ready');
insert into jobs (id, worker_id, work_duration, start_time, started_time, Completed_time, last_status_time, status) 
	values (104, null, 91, '2018-07-27 01-23-00', null, null, null, 'Ready');

commit;
	
select * from jobs where (started_time is null and status = 'Ready') OR 
	(last_status_time + interval '60' second > current_time and status = 'InProgress') order by start_time;

update jobs set worker_id = 25, started_time = current_time, status = 'InProgress' where id = 104 and started_time is null OR started_time + last_status_time < current_time;

select * from jobs order by start_time;

select id, worker_id, work_duration, start_time, started_time, completed_time, last_status_time, status from jobs where (started_time is null and status = 'Ready') OR (timestampdiff(second, last_status_time, current_time) +10 < 0 and status = 'InProgress') order by start_time;

update jobs set worker_id = 34, started_time = current_time, status = 'InProgress' where id = 2 and started_time is null OR last_status_time + interval '10' second < current_time;

select timestampdiff(second, current_timestamp , current_timestamp)+5;

select current_timestamp, current_timestamp +10;

select timestampdiff(second, current_time, last_status_time) > 31 from jobs;


select id, worker_id, work_duration, start_time, started_time, completed_time, last_status_time, status 
from jobs 
where (started_time is null and status = 'Ready') 
OR (timestampdiff(second, last_status_time, current_timestamp) > 31 and status = 'InProgress') 
order by start_time;

 rollback;

 select current_time, started_time, started_time + interval 10 second from jobs where id = 1;

select id, worker_id, work_duration, start_time, started_time, completed_time, last_status_time, status from jobs where (started_time is null and status = 'Ready') OR (last_status_time + interval 10 second > current_time and status = 'InProgress') order by start_time;
