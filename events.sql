************************** Below Average **************************
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
where BIO.EDA < (select AVG(EDA_08_10_2018.EDA) from EDA_08_10_2018)
order by random()

************************** Above Average **************************
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
where BIO.EDA > (select AVG(EDA_08_10_2018.EDA) from EDA_08_10_2018)
order by random()

************************** Maximum **************************
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
order by BIO.EDA desc

************************** Minimum **************************
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
order by BIO.EDA asc

************************** All Rows **************************
SELECT  *
FROM    (
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp as BioTime
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
where BIO.EDA < (select AVG(EDA_08_10_2018.EDA) from EDA_08_10_2018)
order by random()
limit 5)
UNION 
SELECT  *
FROM    ( 
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
where BIO.EDA > (select AVG(EDA_08_10_2018.EDA) from EDA_08_10_2018)
order by random()
limit 5)
union
SELECT  *
FROM    ( 
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
order by BIO.EDA desc
limit 5)
union
SELECT  *
FROM    ( 
select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO.EDA, BIO.TimeStamp
from EDA_08_10_2018 BIO
inner join IMAGES_08_10_2018 IMG on IMG.TimeStamp = BIO.TimeStamp
order by BIO.EDA asc
limit 5)