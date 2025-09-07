--С помощью команды DELETE удалить из таблицы sal запись о продавце с
--номером 3007
--С помощью команды ALTER добавить в таблицу ord новый столбец даты
--заказа с именем ord_date и типом данных date.
--C помощью команды UPDATE установить дату всех заказов равной 1
--сентября 2025 года.
--C помощью команды UPDATE установить дату всех заказов для продукта с
--номером 1002 равной 31 декабря 2024 года.

--insert into my_schema.sal values (3006, 'Astra', 0.16, 'Innopolis');
--insert into my_schema.sal values (3007, 'RedSoft', 0.13, 'Moscow');
--
--delete from my_schema.sal where snum = 3007;
--
--alter table my_schema.ord add column ord_date date;
--
--update my_schema.ord set ord_date = '2025-09-01';
--
--update my_schema.ord set ord_date = '2024-12-31' where pnum = 1002;

-- Вариант 5
--1 Вывести все строки из таблицы Заказов, для которых номер продавца не
--равен 3005
--2 Вывести записи о продуктах с весом строго больше 500 со столбцами в
--следующем порядке: city, name, pnum, weight.
--3 Вывести без повторений номера всех продавцов, которые поставляли
--продукты с номерами не более 1001
--4 Вывести данные о всех продавцах с комиссионными менее или равным 0.13,
--если они не находятся в Екатеринбурге.
--5 Вывести тремя различными способами все заказы покупателей с номерами
--2005, 2005, 2007

select * from my_schema.ord where snum != 3005;

select city, name, pnum, weight from my_schema.prod where weight > 500;

select distinct snum from my_schema.ord where pnum <= 1001;

select * from my_schema.sal where comm <= 0.13 and city != 'Yekaterinburg';

select * from my_schema.ord where cnum in (2005, 2006, 2007);

select * from my_schema.ord where cnum = 2005 or cnum = 2006 or cnum = 2007;

select * from my_schema.ord where cnum between 2005 and 2007;




-- защита №14
select name, city from my_schema.prod where not(city = 'Obninsk' and weight >= 0);
select name, city from my_schema.prod where (city != 'Obninsk' or weight < 0);