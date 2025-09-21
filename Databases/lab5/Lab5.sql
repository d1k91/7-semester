--Выполнить задания 1-3 с использованием оператора JOIN

--1 Вывести информацию о всех заказах. Добавить для каждого заказа его продавца,
--если таковой имеется, или NULL, если продавец неизвестен.
select o.*, s.name from ord o left join sal s on s.snum =o.snum;
--2 Напишите запрос, который выведет номер и объем (количество продукта в нем)
--заказа, название и город продукта только для заказов, в которых продукт имеет вес
--не менее 500
select o.onum, o.amt, p.name, p.city from ord o
join prod p on o.pnum = p.pnum
where p.weight > 500;
--3 Вывести список имен всех покупателей. Для покупателей, имена которых
--начинаются с букв A или I, присоединить количество их заказов.
select c.name, 
	case 
		when c.name like 'A%' or c.name like 'I%' then count(o.onum)
		else null
	end
from cust c
left join ord o on c.cnum = o.cnum
group by c.name; 


--Выполнить задание 4 с использованием CROSS JOIN
--4 Вывести все пары номеров продавцов. Исключить комбинации продавцов с
--самими собой, а также дубликаты строк, выводящие пары в обратном порядке.
select s1.snum, s2.snum from sal s1
cross join sal s2
where s1.snum < s2.snum;


--Выполнить задание 5 с использованием NATURAL JOIN
--5 Вывести результат естественного соединение таблиц ord и cust. По каким полям
--соединились таблицы?
select * from ord natural join cust;


--Выполнить задания 6-7 с использованием UNION / EXPECT / INTERSECT
--6 Вывести список номеров товаров, которые были в заказах от продавца 3001
--и/или производились не в Москве.
select distinct pnum from ord
where snum = 3001
union 
select distinct pnum from prod
where city != 'Moscow';
--7 Вывести список первых букв, с которых начинались имена продавцов, но не
--начинались названия продуктов.
select distinct substring(name from 1 for 1) from sal
except
select distinct substring(name from 1 for 1) from prod;

--Выполнить задание 8 с использованием подзапроса
--8 Вывести имена и общий объем (сумму количеств товаров) заказов всех
--продавцов, находящихся в городах, где суммарный рейтинг покупателей этого
--города больше 400
select s.name,
	(select sum(o.amt) from ord o where o.snum = s.snum)
from sal s
where s.city in 
	(select c.city from cust c 
	group by c.city 
	having sum(c.rating) > 400);
