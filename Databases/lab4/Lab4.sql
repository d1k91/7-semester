--1 Вывести покупателей, которые имеют минимальный рейтинг среди всех
--покупателей.
select cnum, name, rating from cust where rating = (select min(rating) from cust);
--2 Для каждого товара вывести общее количество заказов с этим товаром, только
--если это количество заказов больше, чем у товара с номером 1001
select pnum, count(*) from ord 
group by pnum
having count(*) > (select count(*) from ord where pnum = 1001);

--3 Выбрать покупателей, которые покупали продукты с номером 1004 у продавцов,
--которые когда-либо продавали продукт с номером 1002
select distinct cnum from ord
where pnum = 1004
and snum in (select distinct snum from ord where pnum = 1002);
--4 Вывести названия всех продавцов, которые никогда не работали с покупателями
--из Новосибирска.
select name from sal
where snum not in(
	select distinct snum from ord 
	where cnum in (
		select cnum from cust where city = 'Novosibirsk'
		)
);
--5 Вывести товары, вес которых больше веса хотя бы одного товара из города
--Новосибирск.
select name from prod
where weight > any (
	select weight from prod
	where city = 'Novosibirsk'
);
--6 Вывести товары, которые никогда не продавались продавцами из города, в
--котором произведен этот товар.
select name from prod
where pnum not in(
	select distinct pnum from ord o
	where o.snum in(
		select snum from sal s 
		where city = (
			select city from prod
			where pnum = o.pnum
		)
	)
);
--7 Вывести всю информацию о продавцах с дополнительным столбцом «округ
--продавца» со следующими вариантами значений: «северо-западный», если город
--Санкт-Петербург, «центральный», если город Москва, «сибирский», если город
--Новосибирск, или «другой» для остальных городов.
select *, case 
	when city = 'Saint Petersburg' then 'северо-западный'
	when city = 'Moscow' then 'центральный'
	when city = 'Novosibirsk' then 'сибирский'
	else 'другой'
end as district
from sal;

--8 Вывести из таблицы заказов уникальные номера продуктов и показатель
--популярности каждого из них на основе количества заказов: «популярный» — 4+
--заказа, «умеренный» — 2-3 заказа, «непопулярный» — 1 заказ.
select pnum, case 
	when count(*) >= 4 then 'популярный'
	when count(*) in (2,3) then 'умеренный'
	when count(*) = 1 then 'непопулярный'
end as popularity
from ord group by pnum;
