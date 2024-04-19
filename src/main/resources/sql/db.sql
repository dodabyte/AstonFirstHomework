drop table if exists teachers cascade;
drop table if exists disciplines cascade;
drop table if exists teacher_discipline cascade;
drop table if exists groups cascade;
drop table if exists students cascade;
drop table if exists semester_performance cascade;

create table if not exists teachers (id serial primary key,
                                     last_name varchar not null,
                                     first_name varchar not null,
                                     patronymic varchar not null);
create table if not exists disciplines (id serial primary key,
                                        name varchar not null unique,
                                        teacher_id bigint unique,
                                        foreign key (teacher_id) references teachers (id));
create table if not exists teacher_discipline (id serial primary key,
                                               teacher_id bigint unique,
                                               discipline_id bigint unique,
                                               foreign key (teacher_id) references teachers (id),
                                               foreign key (discipline_id) references disciplines (id),
                                               constraint teacher_discipline_link unique (teacher_id, discipline_id));
create table if not exists groups (id serial primary key,
                                   name varchar not null unique,
                                   course int not null,
                                   semester int not null);
create table if not exists students (id serial primary key,
                                     last_name varchar not null,
                                     first_name varchar not null,
                                     patronymic varchar not null,
                                     group_id bigint,
                                     foreign key (group_id) references groups (id));
create table if not exists semester_performance (id serial primary key,
                                                 student_id bigint,
                                                 discipline_id bigint,
                                                 mark int,
                                                 foreign key (student_id) references students (id),
                                                 foreign key (discipline_id) references disciplines (id));

insert into teachers (last_name, first_name, patronymic) values
        ('Генадьев', 'Генадий', 'Генадьевич'),
        ('Григорьев', 'Григорий', 'Григорьевич'),
        ('Шляпко', 'Шляпа', 'Шляпович'),
        ('Зубенко', 'Михаил', 'Петрович');

insert into disciplines (name, teacher_id) values
        ('Программирование', 1),
        ('Программная инженерия', 2),
        ('Управление программными проектами', 3),
        ('Разработка систем баз данных', 4);

insert into teacher_discipline (teacher_id, discipline_id) values
        (1, 1),
        (2, 2),
        (3, 3),
        (4, 4);

insert into groups (name, course, semester) values
        ('АВТ-043', 4, 8),
        ('АВТ-142', 3, 6),
        ('АВТ-241', 2, 4),
        ('АВТ-340', 1, 2);

insert into students (last_name, first_name, patronymic, group_id) values
        ('Ударник', 'Гриша', 'Электронович', 1),
        ('Светыч', 'Любовь', 'Петровна', 1),
        ('Халапеньо', 'Дмитрий', 'Олегович', 1),
        ('Азаматов', 'Азамат', 'Азаматович', 2),
        ('Святой', 'Святослав', 'Святославович', 2),
        ('Зубр', 'Тигр', 'Кошкович', 3),
        ('Светыч', 'Григорий', 'Петрович', 3),
        ('Честная', 'Ирина', 'Николаевна', 3),
        ('Ступор', 'Николай', 'Васильевич', 4),
        ('Отличник', 'Вадим', 'Вадимович', 4);

insert into semester_performance (student_id, discipline_id, mark) values
        (1, 1, 85),
        (1, 2, 94),
        (1, 3, 64),
        (2, 1, 75),
        (2, 2, 86),
        (3, 4, 89),
        (3, 2, 63),
        (4, 1, 57),
        (4, 3, 55),
        (5, 4, 96),
        (6, 2, 45),
        (6, 4, 75),
        (7, 1, 90),
        (7, 2, 100),
        (8, 1, 85),
        (8, 2, 87),
        (8, 3, 90),
        (8, 4, 79),
        (9, 1, 100),
        (10, 4, 99);