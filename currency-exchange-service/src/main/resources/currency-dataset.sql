--create table currency ( currency_numeric_code number primary key, currency varchar2(255) null, currency_alpha_code varchar2(255) null);
insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('Australian Dollar','AUD',36);
insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('New Zealand Dollar','NZD',554);
insert into currency (currency,currency_alpha_code,currency_numeric_code) values ('Kuwaiti Dinar','KWD',414);
commit;