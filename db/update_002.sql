create table employee
(
    id                 serial primary key          not null,
    first_name         varchar(500),
    last_name          varchar(500),
    inn                int,
    date_of_employment timestamp without time zone not null default now()
);

create table employee_persons
(
    employee_id int references employee(id),
    person_id int references person(id)
);
