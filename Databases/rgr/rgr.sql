-- вывести вакансии и колчиство откликов у вакансий имеющих 2 и более отклика
select 
	v.vacancy_id,
	v.vacancy_name,
	count(r.responce_id)
from vacancies v
left join responces r  on v.vacancy_id = r.vacancy_id  
group by v.vacancy_id
having count(r.responce_id ) >= 2;
-- вывести людей, ищущих работу, которые откликнулись на 2 и более вакансии, среднюю ожидаемую зп и количество откликов
select 
	js.name,
	count(r.responce_id),
	round(avg(v.salary_min + v.salary_max)/ 2, 2)
from job_seekers js
join responces r on js.seeker_id = r.seeker_id
join vacancies v on r.vacancy_id = v.vacancy_id
group by js.name
having count(r.responce_id) >= 2;