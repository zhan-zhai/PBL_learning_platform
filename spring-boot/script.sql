create table course
(
    c_id        int auto_increment,
    t_id        varchar(20)  null,
    c_name      varchar(50)  null,
    point       int          null,
    status      int          null,
    image_URL   varchar(100) null,
    description varchar(200) null,
    constraint course_c_id_uindex
        unique (c_id)
);

create index course_user_u_id_fk
    on course (t_id);

alter table course
    add primary key (c_id);

create table project
(
    p_id                int auto_increment,
    c_id                int          null,
    p_name              varchar(50)  null,
    grading_status      tinyint(1)   null,
    teacher_grade_ratio double       null,
    self_grade_ratio    double       null,
    mutual_grade_ratio  double       null,
    description         varchar(200) null,
    constraint project_p_id_uindex
        unique (p_id)
);

create index project_course_c_id_fk
    on project (c_id);

alter table project
    add primary key (p_id);

create table assignment
(
    a_id          int auto_increment,
    p_id          int          not null,
    a_name        varchar(50)  null,
    importance    int          null,
    a_start_date  date         null,
    a_end_date    date         null,
    a_description varchar(200) null,
    primary key (a_id, p_id),
    constraint assignment_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade
);

create table grade_system
(
    item_id     int          not null,
    p_id        int          not null,
    description varchar(200) null,
    max_grade   int          null,
    primary key (p_id, item_id),
    constraint grade_system_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade
);

create index grade_system_item_id_p_id_index
    on grade_system (item_id, p_id);

create table user
(
    u_id        varchar(20)          not null,
    type        varchar(20)          null,
    u_name      varchar(50)          null,
    gender      varchar(10)          null,
    password    varchar(50)          null,
    description varchar(200)         null,
    image       varchar(100)         null,
    status      tinyint(1) default 1 null,
    constraint user_u_id_uindex
        unique (u_id)
);

alter table user
    add primary key (u_id);

create table discussion
(
    d_id    int auto_increment,
    p_id    int          null,
    u_id    varchar(20)  null,
    time    date         null,
    content varchar(200) null,
    constraint discussion_d_id_uindex
        unique (d_id),
    constraint discussion_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade,
    constraint discussion_user_u_id_fk
        foreign key (u_id) references user (u_id)
            on delete cascade
);

alter table discussion
    add primary key (d_id);

create table evaluation
(
    p_id         int         not null,
    active_s_id  varchar(20) not null,
    passive_s_id varchar(20) not null,
    grade        double      null,
    primary key (p_id, active_s_id, passive_s_id),
    constraint evaluation_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade,
    constraint evaluation_user_u_id_fk
        foreign key (active_s_id) references user (u_id)
            on delete cascade,
    constraint evaluation_user_u_id_fk_2
        foreign key (passive_s_id) references user (u_id)
            on delete cascade
);

create table file
(
    f_id        int auto_increment,
    p_id        int          not null,
    u_id        varchar(20)  null,
    f_name      varchar(50)  null,
    file_URL    varchar(100) null,
    description varchar(200) null,
    primary key (f_id, p_id),
    constraint file_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade,
    constraint file_user_u_id_fk
        foreign key (u_id) references user (u_id)
            on delete cascade
);

create table reply
(
    r_id    int auto_increment,
    d_id    int          null,
    u_id    varchar(20)  null,
    time    date         null,
    content varchar(200) null,
    constraint reply_r_id_uindex
        unique (r_id),
    constraint reply_discussion_d_id_fk
        foreign key (d_id) references discussion (d_id)
            on delete cascade,
    constraint reply_user_u_id_fk
        foreign key (u_id) references user (u_id)
            on delete cascade
);

alter table reply
    add primary key (r_id);

create table student_course
(
    u_id varchar(20) not null,
    c_id int         not null,
    primary key (u_id, c_id),
    constraint student_course_course_c_id_fk
        foreign key (c_id) references course (c_id)
            on delete cascade,
    constraint student_course_user_u_id_fk
        foreign key (u_id) references user (u_id)
            on delete cascade
);

create table student_project
(
    u_id            varchar(20) not null,
    p_id            int         not null,
    is_group_leader tinyint(1)  null,
    self_grade      double      null,
    mutual_grade    double      null,
    teacher_grade   double      null,
    primary key (u_id, p_id),
    constraint student_project_project_p_id_fk
        foreign key (p_id) references project (p_id)
            on delete cascade,
    constraint student_project_user_u_id_fk
        foreign key (u_id) references user (u_id)
            on delete cascade
);

create table student_assignment
(
    a_id   int         not null,
    p_id   int         not null,
    u_id   varchar(20) not null,
    status tinyint(1)  null,
    urge   tinyint(1)  null,
    primary key (a_id, p_id, u_id),
    constraint student_assignment_assignment_a_id_p_id_fk
        foreign key (a_id, p_id) references assignment (a_id, p_id)
            on delete cascade,
    constraint student_assignment_student_project_u_id_p_id_fk
        foreign key (u_id, p_id) references student_project (u_id, p_id)
            on delete cascade
);

create table student_grade
(
    item_id int         not null,
    u_id    varchar(20) not null,
    p_id    int         not null,
    grade   double      null,
    primary key (item_id, u_id, p_id),
    constraint student_grade_grade_system_item_id_p_id_fk
        foreign key (item_id, p_id) references grade_system (item_id, p_id)
            on delete cascade,
    constraint student_grade_student_project_u_id_p_id_fk
        foreign key (u_id, p_id) references student_project (u_id, p_id)
            on delete cascade
);


