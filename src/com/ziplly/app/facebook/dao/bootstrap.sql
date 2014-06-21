


insert into interest (name) values ("Outdoor");
insert into interest (name) values ("Indoor");
insert into interest (name) values ("Sports");
insert into interest (name) values ("Cooking");
insert into interest (name) values ("Hiking");
insert into interest (name) values ("Running");
insert into interest (name) values ("Biking");
insert into interest (name) values ("Electronics");
insert into interest (name) values ("Movies");

insert into subscription_plan (description,fee,name,time_created,plan_type, status, tweets_allowed, coupons_allowed) values ('Connect with your customers', 3, 'Basic plan', now(), "BASIC","ENABLED", 3, 0);
insert into subscription_plan (description,fee,name,time_created,plan_type, status, tweets_allowed, coupons_allowed) values ('Publish upto 6 coupons/month', 25, 'Pro plan', now(),"PREMIUM", "ENABLED", 6, 6);
insert into subscription_plan (description,fee,name,time_created,plan_type, status, tweets_allowed, coupons_allowed) values ("Publish upto 100 coupons/month", 50, "Premium plan", now(), "PRO", "ENABLED", 100, 100);

insert into transaction values (3,1402135359,1402135359, 4, 'ACTIVE', 'fake2', 'ACTIVE', 1);
insert into subscription values (2, 'ACTIVE', 1402135359000, 1402135359000, 1, 1, 2);

create user 'zipllyadmin'@'localhost' identified by 'Sherica12';
grant all privileges on zipllydb.* to 'zipllyadmin'@'localhost';
flush privileges