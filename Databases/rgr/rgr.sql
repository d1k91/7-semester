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

-- вывести информацию о будущих собеседованиях, информацию о соискателе, вакансии и компании
select 
	js.name,
	v.vacancy_name,
	e.company_name,
	i.interview_date,
	i.result
from interviews i 
join responces r on i.responce_id = r.responce_id 
join job_seekers js on r.seeker_id = js.seeker_id 
join vacancies v on r.vacancy_id = v.vacancy_id 
join employers e on v.employer_id = e.employer_id 
where i.interview_date > current_timestamp;


-- найти компании, у которых были успешные собеседования, вывести количество собеседований, кол-во уникальных соискателей, и среднюю ЗП по вакансиям

select
	e.company_name as "Компании",
	count(distinct i.interview_id) as "Успешных собеседований",
	count(distinct js.seeker_id ) as "Кол-во соискателей",
	round(avg((v.salary_min + v.salary_max )/2), 2) as "Средняя ЗП по вакансиям"
from employers e
join vacancies v on e.employer_id = v.employer_id 
join responces r on v.vacancy_id = r.vacancy_id
join job_seekers js on r.seeker_id = js.seeker_id 
join interviews i on r.responce_id = i.responce_id
where i.result = 'Прошло'
group by e.company_name;


  
  
-- вывести отклики на вакансии с минимальной зп выше средней минимальной зп по всем вакансиям, а также информацию о соикателях и вакансиях
create view high_salary_responses as
select 
    r.responce_id,
    r.responce_date,
    j.name as seeker_name,
    j.email as seeker_email,
    v.vacancy_name,
    v.salary_min,
    v.salary_max
from 
    responces r
join 
    job_seekers j on r.seeker_id = j.seeker_id
join 
    vacancies v on r.vacancy_id = v.vacancy_id
where 
    v.vacancy_id in (select vacancy_id from vacancies where salary_min > (select avg(salary_min) from vacancies));
  
  select * from high_salary_responses;

-- Вывести вакансии с большой конкуренцией 
 create view high_competition_vacancies as
	select
	    v.vacancy_id,
	    v.vacancy_name,
	    e.company_name,
	    v.salary_min,
	    v.salary_max,
	    (v.salary_max - v.salary_min) AS sal_range,
	    count(r.responce_id) as responces
	FROM vacancies v
	JOIN employers e ON v.employer_id = e.employer_id
	join responces r on v.vacancy_id = r.vacancy_id
	WHERE v.vacancy_id IN (
	    SELECT vacancy_id 
	    FROM responces
	    GROUP BY vacancy_id 
	    HAVING COUNT(*) > (SELECT AVG(cnt) FROM (
	        SELECT COUNT(*) as cnt FROM responces GROUP BY vacancy_id
	    ) AS avg_responses)
	)
	group by v.vacancy_id, e.company_name;
 
select * from high_competition_vacancies;

