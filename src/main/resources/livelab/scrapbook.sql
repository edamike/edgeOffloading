select count(distinct uid) as anzahl, SUBSTR( DATEADD('SECOND', "time", DATE '1970-01-01'), 0, 13) from celltower group by SUBSTR( DATEADD('SECOND', "time", DATE '1970-01-01'), 0, 13) order by anzahl desc;

select distinct uid, towerid from celltower where SUBSTR( DATEADD('SECOND', "time", DATE '1970-01-01'), 0, 15) = '2010-04-09 00:5';