create table job_seekers (
	seeker_id serial primary key,
	name varchar(50) not null,
	birth_date date check (extract(year from age(birth_date)) >= 18),
	email varchar(50) check (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),	
	phone varchar(15) check (phone ~ '^8[0-9]{10}|\+7[0-9]{10}')
);
create table employers (
	employer_id serial primary key,
	company_name varchar(50) not null unique,
	address varchar(100) not null,
	contact_person varchar(50) not null,
	phone varchar(15) check (phone ~ '^8[0-9]{10}|\+7[0-9]{10}'),
	email varchar(50) check (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);
create table vacancies (
	vacancy_id serial primary key,
	vacancy_name varchar(50) not null,
	employer_id int not null,
	description text not null,
	salary_min decimal(10,2) not null,
	salary_max decimal(10,2) not null,
	foreign key (employer_id) references employers(employer_id) on delete cascade,
	constraint check_salary check (salary_min > 0 and salary_max >= salary_min)
);
create table responces(
	responce_id serial primary key,
	seeker_id int not null,
	vacancy_id int not null,
	responce_date timestamp not null default current_timestamp,
	foreign key (seeker_id) references job_seekers(seeker_id) on delete cascade,
	foreign key (vacancy_id) references vacancies(vacancy_id) on delete cascade
);
create table interviews (
	interview_id serial primary key,
	responce_id int not null,
	interview_date timestamp not null,
	result varchar(50),
	foreign key (responce_id) references responces(responce_id) on delete cascade,
	constraint check_result check (result in ('Назначено', 'Прошло', 'Не явился', 'Отменено', 'Перенесено', null))
);

insert into job_seekers(name, birth_date, email, phone) values 
	('Глинский Вадим', '2004-12-19', 'v.glinskiy@icloud.com', '89134848661'),
	('Огарков Кирилл', '2004-11-17', 'klimpopo@icloud.com', '+79529020904'),
	('Баженов Эдуард', '2004-08-22', 'sinful@gmail.com', '81234567890'),
	('Смолевская Елизавета', '2004-11-25', 'lizasml@gmail.com', '80987654321'),
	('Соловьева Ирина', '2004-05-01', 'iirisha_va@gmail.com', '+71234567890'),
	('Шелегина Ольга', '2002-06-21', 'shelegina@gmail.com', '+709987654321');

insert into employers(company_name, address, contact_person, phone, email) values
	('ООО "ТехноПро"','г. Москва, ул. Ленина, д. 1', 'Алексей Волков', '+74951234567', 'hr@technopro.ru'),
    ('АО "СтройГарант"','г. Москва, пр. Мира, д. 15', 'Ольга Козлова', '+74957654321', 'career@stroigarant.ru' ),
    ('ИП "СервисПлюс"','г. Москва, ул. Пушкина, д. 10', 'Дмитрий Иванов', '+74955554433', 'info@serviceplus.ru'),
    ('АО "Инфософт"','г. Новосибирск, ул. Крылова, 31', 'Михаил Пясковский', '+73832112727', 'info@is1c.ru' ),
	('АО ФинансГрупп', 'г. Новосибирск, пр. Карла Маркса, д. 42', 'Анна Петрова', '83831239876', 'hr@financegroup.ru'),
	('ИП КонсалтПрофи', 'г. Казань, ул. Баумана, д. 15', 'Артем Васильев', '88432107788', 'info@consultprofi.ru');

insert into vacancies (vacancy_name, employer_id, description, salary_min, salary_max) VALUES 
    ('Разработчик Python', 1, 'Разработка backend-приложений на Python и Django', 80000.00, 150000.00),
    ('Менеджер проектов', 2, 'Управление строительными проектами', 70000.00, 120000.00),
    ('Маркетолог', 3, 'Разработка маркетинговых стратегий', 50000.00, 90000.00),
	('Программист 1С', 4, 'Франчайзинговая сеть «ИнфоСофт» входит в ТОП-10 партнеров фирмы 1С и в ТОП-50 рейтинга работодателей России по версии HH.RU.', 30000.00, 100000.00),
	('Системный администратор', 5, 'Обслуживание IT-инфраструктуры компании, техническая поддержка', 55000.00, 95000.00),
	('HR менеджер', 6, 'Подбор персонала, проведение собеседований', 48000.00, 85000.00),
	('Аналитик данных', 1, 'Технопром - это многопрофильная ИТ и ИБ компания, относящаяся к крупному бизнесу.', 10000.00, 95000.00);


insert into responces (seeker_id, vacancy_id) VALUES 
    (1, 1), 
    (2, 2),
    (3, 3),
    (4, 4),
	(5, 5), 
	(6, 6),
	(1, 4),
	(2, 4),
	(3, 6);

INSERT INTO interviews (responce_id, interview_date, result) VALUES 
    (1, '2025-10-25 14:00:00', 'Перенесено'),
    (2, '2025-10-13 10:30:00', 'Отменено'),
    (3, '2025-10-11 16:15:00', 'Прошло'),
    (4, '2025-09-12 12:30:00', 'Прошло'),
    (5, '2025-10-27 11:00:00', 'Назначено'),
    (6, '2025-10-23 11:00:00', 'Назначено');
	
	
	
	
	
	
	
	
	
	
	
	
	