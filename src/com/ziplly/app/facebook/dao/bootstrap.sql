insert into neighborhood (city, name, state, parent_neighborhood_id) values ("seattle", "Magnolia","WA", null);


insert into neighborhood(name, city, state, type) values ("North Seattle", "Seattle", "WA", "XXX");
insert into neighborhood(name, city, state, type) values ("West Woodland", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("Sunset Hill", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("Adams", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("Whittier Heights", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("Loyal Heights", "Seattle","WA", "X");


insert into neighborhood(name, city, state, type) values ("North Seattle", "Seattle", "WA", "XXX");
insert into neighborhood(name, city, state, type) values ("Magnolia", "Seattle", "WA", "XX");
insert into neighborhood(name, city, state, type) values ("SouthEast Magnolia", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("BrairCliff", "Seattle", "WA", "X");
insert into neighborhood(name, city, state, type) values ("Lawton Heights", "Seattle", "WA", "X");

insert into neighborhood_postalcode values(2,98199);
insert into neighborhood_postalcode values(3,98199);
insert into neighborhood_postalcode values(4,98199);
insert into neighborhood_postalcode values(12,98117);
insert into neighborhood_postalcode values(13,98117);


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

