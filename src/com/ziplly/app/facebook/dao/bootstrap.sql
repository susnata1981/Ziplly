insert into postal_code (type,code) values ("regular", 98199);
insert into postal_code (type,code) values ("regular", 98198);
insert into postal_code (type,code) values ("regular", 98197);

insert into neighborhood (type,city,name,state,postalcode_id) values ("regular", "seattle", "Magnolia","WA", 1);
insert into neighborhood (type,city,name,state,postalcode_id) values ("regular", "seattle", "Interbay","WA", 1);
insert into neighborhood (type,city,name,state,postalcode_id) values ("regular", "seattle", "Queen Ann","WA", 2);
insert into neighborhood (type,city,name,state,postalcode_id) values ("regular", "seattle", "Ballard","WA", 3);


insert into subscription_plan (description,fee,name,timeCreated,tweetsAllowed) values ('Send upto 3 messages a month for free', 3, 'Basic plan', now(), 3);
insert into subscription_plan (description,fee,name,timeCreated,tweetsAllowed) values ('Send upto 5 messages a month', 5, 'Pro plan', now(), 5);
insert into subscription_plan (description,fee,name,timeCreated,tweetsAllowed) values ("Send upto 15 messages a month", 15, "Premium plan", now(), 15);
