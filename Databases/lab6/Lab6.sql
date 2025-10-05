--1 Вывести список существующих индексов в БД. Являются ли индексы
--кластеризованными? Какую структуру в памяти они имеют?

select * from pg_indexes where schemaname = 'my_schema';

--2 Создать индекс для ускорения сортировки таблицы покупателей по городу.
--Проверьте, используется ли созданный индекс при сортировке.
create index idx_cust_city on cust(city);

explain analyze
select * from cust order by city;
--3 Добавьте в таблицу покупателей 1000 новых записей, например, используя
--команду, формирующую последовательности:
--insert into cust
--values ( generate_series(1, 1000), 'test_name', 100, 'test_city' );
--Проверьте, используется ли теперь созданный индекс при сортировке?
explain analyze
select * from cust order by city;

--В заданиях 4-7 необходимо выполнить одинаковый запрос, сохраняя его
--содержимое в различных структурах. Полученную (сохраненную) выборку
--вывести на экран. В чем отличие этих структур?
--Вывести полную информацию о заказе, его продавце, его продукте и его
--покупателе, только если:
--количество товара (amt) в этом заказе больше, чем среднее по таблице,
--товар не из Санкт-Петербурга,
--продавец совершил не более 10 заказов за все время,
--рейтинг покупателя не ниже, чем хотя бы у одного покупателя из Москвы.

--4 Сохранить результат как новую таблицу и вывести ее содержимое.
create table lab6 as 
SELECT 
    o.onum,
    o.amt,
    p.pnum,
    p.name as p_name,
    p.city as p_city,
    p.weight,
    s.snum,
    s.name as s_name,
    s.comm,
    s.city as s_city,
    c.cnum,
    c.name as c_name,
    c.rating,
    c.city as c_city
FROM my_schema.ord o
JOIN my_schema.prod p ON o.pnum = p.pnum
JOIN my_schema.sal s ON o.snum = s.snum
JOIN my_schema.cust c ON o.cnum = c.cnum
WHERE o.amt > (SELECT AVG(amt) FROM my_schema.ord)
  AND p.city != 'Saint Petersburg'
  AND s.snum IN (SELECT snum FROM my_schema.ord GROUP BY snum having COUNT(*) <= 10)
  AND c.rating >= (select MIN(rating) from cust where city = 'Moscow');

select * from lab6;


--5 Сохранить результат как представление и вывести его содержимое.

create view lab6_view as
SELECT 
    o.onum,
    o.amt,
    p.pnum,
    p.name as p_name,
    p.city as p_city,
    p.weight,
    s.snum,
    s.name as s_name,
    s.comm,
    s.city as s_city,
    c.cnum,
    c.name as c_name,
    c.rating,
    c.city as c_city
FROM my_schema.ord o
JOIN my_schema.prod p ON o.pnum = p.pnum
JOIN my_schema.sal s ON o.snum = s.snum
JOIN my_schema.cust c ON o.cnum = c.cnum
WHERE o.amt > (SELECT AVG(amt) FROM my_schema.ord)
  AND p.city != 'Saint Petersburg'
  AND s.snum IN (SELECT snum FROM my_schema.ord GROUP BY snum having COUNT(*) <= 10)
  AND c.rating >= (select MIN(rating) from cust where city = 'Moscow');

select * from lab6_view;
--6 Сохранить результат как материализованное представление и вывести его
--содержимое.
create materialized view lab6_materialized_view as
SELECT 
    o.onum,
    o.amt,
    p.pnum,
    p.name as p_name,
    p.city as p_city,
    p.weight,
    s.snum,
    s.name as s_name,
    s.comm,
    s.city as s_city,
    c.cnum,
    c.name as c_name,
    c.rating,
    c.city as c_city
FROM my_schema.ord o
JOIN my_schema.prod p ON o.pnum = p.pnum
JOIN my_schema.sal s ON o.snum = s.snum
JOIN my_schema.cust c ON o.cnum = c.cnum
WHERE o.amt > (SELECT AVG(amt) FROM my_schema.ord)
  AND p.city != 'Saint Petersburg'
  AND s.snum IN (SELECT snum FROM my_schema.ord GROUP BY snum having COUNT(*) <= 10)
  AND c.rating >= (select MIN(rating) from cust where city = 'Moscow');

select * from lab6_materialized_view;
--7 Использовать для запроса блок оператора WITH и вывести результат.

with lab6_block_with as (
	SELECT 
    o.onum,
    o.amt,
    p.pnum,
    p.name as p_name,
    p.city as p_city,
    p.weight,
    s.snum,
    s.name as s_name,
    s.comm,
    s.city as s_city,
    c.cnum,
    c.name as c_name,
    c.rating,
    c.city as c_city
	FROM my_schema.ord o
	JOIN my_schema.prod p ON o.pnum = p.pnum
	JOIN my_schema.sal s ON o.snum = s.snum
	JOIN my_schema.cust c ON o.cnum = c.cnum
	WHERE o.amt > (SELECT AVG(amt) FROM my_schema.ord)
	  AND p.city != 'Saint Petersburg'
	  AND s.snum IN (SELECT snum FROM my_schema.ord GROUP BY snum having COUNT(*) <= 10)
	  AND c.rating >= (select MIN(rating) from cust where city = 'Moscow')
)
select * from lab6_block_with;