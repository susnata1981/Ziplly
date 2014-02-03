insert into postal_code (type,code) values ("regular", 98199);
insert into postal_code (type,code) values ("regular", 98198);
insert into postal_code (type,code) values ("regular", 98197);

insert into neighborhood (type,city,name,state, postalcode_id) values ("regular", "seattle", "Magnolia","WA", 1);
insert into neighborhood (type,city,name,state, postalcode_id) values ("regular", "seattle", "Interbay","WA", 1);
insert into neighborhood (type,city,name,state, postalcode_id) values ("regular", "seattle", "Queen Ann","WA", 2);
insert into neighborhood (type,city,name,state, postalcode_id) values ("regular", "seattle", "Ballard","WA", 3);


insert into subscription_plan (description,fee,name,time_created,status, tweets_allowed) values ('Send upto 3 messages a month for free', 3, 'Basic plan', now(),"DISABLED", 3);
insert into subscription_plan (description,fee,name,time_created,status, tweets_allowed) values ('Send upto 5 messages a month', 5, 'Pro plan', now(), "DISABLED", 5);
insert into subscription_plan (description,fee,name,time_created,status, tweets_allowed) values ("Send upto 15 messages a month", 15, "Premium plan", now(), "DISABLED",15);


drop table if exists account
drop table if exists account_interest
drop table if exists account_notification
drop table if exists account_registration
drop table if exists business_properties
drop table if exists comment
drop table if exists conversation
drop table if exists conversation_messages
drop table if exists hashtag
drop table if exists interest
drop table if exists love
drop table if exists neighborhood
drop table if exists notification_settings
drop table if exists password_recovery
drop table if exists pending_invitations
drop table if exists postal_code
drop table if exists privacy_settings
drop table if exists session
drop table if exists spam
drop table if exists subscription_plan
drop table if exists transaction
drop table if exists tweet
drop table if exists tweet_hashtag

create table account (type varchar(31) not null, account_id bigint not null auto_increment, time_created datetime, time_updated datetime, access_token varchar(255), city varchar(255), email varchar(255), facebook_id varchar(255), image_url varchar(255), last_login datetime, password varchar(255), role integer, state varchar(255), status integer, uid bigint, profile_url varchar(255), zip integer not null, first_name varchar(255), gender varchar(255), introduction varchar(255), last_name varchar(255), occupation varchar(255), businessType integer, category integer, name varchar(255), phone varchar(255), street1 varchar(255), street2 varchar(255), website varchar(255), neighborhood_id bigint, properties_id bigint, primary key (account_id));
create table account_interest (account_id bigint not null, interest_id bigint not null, primary key (account_id, interest_id));
create table account_notification (notification_id bigint not null auto_increment, time_created datetime, time_updated datetime, read_status varchar(255), record_status integer, notification_type varchar(255), conversation_id bigint, recipient_id bigint, sender_account_id bigint, tweet_id bigint, primary key (notification_id));
create table account_registration (id bigint not null auto_increment, time_created datetime, time_updated datetime, accountType integer, businessType integer, code bigint not null, email varchar(255), status integer, primary key (id));
create table business_properties (id bigint not null auto_increment, time_created datetime, time_updated datetime, accepts_credit_card BOOLEAN DEFAULT false not null, cuisine integer, friday_end_time varchar(255), friday_start_time varchar(255), good_for_kids BOOLEAN DEFAULT false not null, holidays varchar(255), monday_end_time varchar(255), monday_start_time varchar(255), parking_facility varchar(255), price_range integer, saturday_end_time varchar(255), saturday_start_time varchar(255), sunday_end_time varchar(255), sunday_start_time varchar(255), thursday_end_time varchar(255), thursday_start_time varchar(255), tuesday_end_time varchar(255), tuesday_start_time varchar(255), wednesday_end_time varchar(255), wednesday_start_time varchar(255), wifi_available BOOLEAN DEFAULT false not null, primary key (id));
create table comment (comment_id integer not null auto_increment, content varchar(255), time_created datetime, time_updated datetime, account_id bigint, tweet_id bigint, primary key (comment_id));
create table conversation (id bigint not null auto_increment, time_created datetime, time_updated datetime, status varchar(255), subject varchar(255), receiver_account_id bigint, sender_account_id bigint, primary key (id));
create table conversation_messages (conversation_id bigint not null, message longtext, receiver_id bigint, sender_id bigint, timeCreated datetime);
create table hashtag (id bigint not null auto_increment, time_created datetime, time_updated datetime, tag varchar(255), primary key (id));
create table interest (interest_id bigint not null auto_increment, name varchar(255), timeCreated datetime, primary key (interest_id));
create table love (likeId bigint not null auto_increment, time_created datetime, time_updated datetime, account_id bigint, comment_id integer, tweet_id bigint, primary key (likeId));
create table neighborhood (type varchar(31) not null, neighborhood_id bigint not null auto_increment, time_created datetime, time_updated datetime, city varchar(255), name varchar(255), state varchar(255), postalcode_id bigint, primary key (neighborhood_id));
create table notification_settings (notificationId bigint not null auto_increment, action integer, time_created datetime, type varchar(255), account_id bigint, primary key (notificationId));
create table password_recovery (id bigint not null auto_increment, email varchar(255), hash varchar(255), status integer, timeCreated datetime, primary key (id));
create table pending_invitations (id bigint not null auto_increment, time_created datetime, time_updated datetime, email varchar(255), zip integer not null, primary key (id));
create table postal_code (type varchar(31) not null, id bigint not null auto_increment, time_created datetime, time_updated datetime, code varchar(255), primary key (id));
create table privacy_settings (id bigint not null auto_increment, time_created datetime, time_updated datetime, section integer, setting integer, account_id bigint, primary key (id));
create table session (id bigint not null auto_increment, expired_at datetime, time_created datetime, uid bigint, account_id bigint, primary key (id));
create table spam (id bigint not null auto_increment, time_created datetime, time_updated datetime, status integer, reporter_account_id bigint, tweet_tweet_id bigint, primary key (id));
create table subscription_plan (subscription_id bigint not null auto_increment, time_created datetime, time_updated datetime, description varchar(255), fee double precision, name varchar(255), tweets_allowed integer, primary key (subscription_id));
create table transaction (transaction_id bigint not null auto_increment, time_created datetime, time_updated datetime, amount decimal(19,2), currencyCode varchar(255), status integer, subscription_id bigint, account_id bigint, primary key (transaction_id));
create table tweet (tweet_id bigint not null auto_increment, time_created datetime, time_updated datetime, content longtext, image_url varchar(255), image_id bigint, status integer, type integer, sender_id bigint, primary key (tweet_id));
create table tweet_hashtag (tweet_id bigint not null, id bigint not null, primary key (tweet_id, id));


alter table account add constraint UK_q0uja26qgu1atulenwup9rxyr unique (email);
alter table account add index FK_2ckkihhya4jo1rj0we76d57x4 (neighborhood_id), add constraint FK_2ckkihhya4jo1rj0we76d57x4 foreign key (neighborhood_id) references neighborhood (neighborhood_id);
alter table account add index FK_qyb237lvgnkyv43pcpisnioa3 (properties_id), add constraint FK_qyb237lvgnkyv43pcpisnioa3 foreign key (properties_id) references business_properties (id);
alter table account_interest add index FK_h8nf8m9f1u00b42xhwp4kneuv (interest_id), add constraint FK_h8nf8m9f1u00b42xhwp4kneuv foreign key (interest_id) references interest (interest_id);
alter table account_interest add index FK_nrp3gx972gmm69t55s88yaoqd (account_id), add constraint FK_nrp3gx972gmm69t55s88yaoqd foreign key (account_id) references account (account_id);
alter table account_notification add index FK_1c1kyl10eq93c5farpexbd5hg (conversation_id), add constraint FK_1c1kyl10eq93c5farpexbd5hg foreign key (conversation_id) references conversation (id);
alter table account_notification add index FK_7qc3uojsjdtey2n03890jcwah (recipient_id), add constraint FK_7qc3uojsjdtey2n03890jcwah foreign key (recipient_id) references account (account_id);
alter table account_notification add index FK_p0k5hosgwb2hf02hir7pfa1kq (sender_account_id), add constraint FK_p0k5hosgwb2hf02hir7pfa1kq foreign key (sender_account_id) references account (account_id);
alter table account_notification add index FK_2oi0og7ohgsqqx19niso945g2 (tweet_id), add constraint FK_2oi0og7ohgsqqx19niso945g2 foreign key (tweet_id) references tweet (tweet_id);
alter table comment add index FK_3dy6fw1oofi5tmw6tincvv28w (account_id), add constraint FK_3dy6fw1oofi5tmw6tincvv28w foreign key (account_id) references account (account_id);
alter table comment add index FK_gvdu2agjs48na22lslpcqa2yr (tweet_id), add constraint FK_gvdu2agjs48na22lslpcqa2yr foreign key (tweet_id) references tweet (tweet_id);
alter table conversation add index FK_1uuugm11mk88lf9on52sqjfgy (receiver_account_id), add constraint FK_1uuugm11mk88lf9on52sqjfgy foreign key (receiver_account_id) references account (account_id);
alter table conversation add index FK_ssfweraaaoyg8htut76pe1ww8 (sender_account_id), add constraint FK_ssfweraaaoyg8htut76pe1ww8 foreign key (sender_account_id) references account (account_id);
alter table conversation_messages add index FK_e9asnx7r4qtfxedp0ocok417w (receiver_id), add constraint FK_e9asnx7r4qtfxedp0ocok417w foreign key (receiver_id) references account (account_id);
alter table conversation_messages add index FK_nc88n12oakcqsbjhi8ee0xf9o (sender_id), add constraint FK_nc88n12oakcqsbjhi8ee0xf9o foreign key (sender_id) references account (account_id);
alter table conversation_messages add index FK_31y6efdkh4a2cr6v7kyo7ug69 (conversation_id), add constraint FK_31y6efdkh4a2cr6v7kyo7ug69 foreign key (conversation_id) references conversation (id)
alter table love add index FK_7dqaabtoe9dd46pt5g26kdcx5 (account_id), add constraint FK_7dqaabtoe9dd46pt5g26kdcx5 foreign key (account_id) references account (account_id)
alter table love add index FK_ojogf9n8xtupm9lq0x2kvdkvq (comment_id), add constraint FK_ojogf9n8xtupm9lq0x2kvdkvq foreign key (comment_id) references comment (comment_id)
alter table love add index FK_379db3vewwkt23unnojmeefq3 (tweet_id), add constraint FK_379db3vewwkt23unnojmeefq3 foreign key (tweet_id) references tweet (tweet_id)
alter table neighborhood add index FK_9ju30heo4gj9kacgvs0hbdaph (postalcode_id), add constraint FK_9ju30heo4gj9kacgvs0hbdaph foreign key (postalcode_id) references postal_code (id)
alter table notification_settings add index FK_sc2avceqthw9fek1exneftyte (account_id), add constraint FK_sc2avceqthw9fek1exneftyte foreign key (account_id) references account (account_id)
alter table privacy_settings add index FK_mif59nid4nsmd1wb4gdf63nl2 (account_id), add constraint FK_mif59nid4nsmd1wb4gdf63nl2 foreign key (account_id) references account (account_id)
alter table privacy_settings add index FK_mif59nid4nsmd1wb4gdf63nl2 (account_id), add constraint FK_mif59nid4nsmd1wb4gdf63nl2 foreign key (account_id) references account (account_id)
alter table spam add index FK_g65f3biciy66pcpauy18y7gys (reporter_account_id), add constraint FK_g65f3biciy66pcpauy18y7gys foreign key (reporter_account_id) references account (account_id)
alter table spam add index FK_ggiat31u2w3f20rt6v2ix8vkl (tweet_tweet_id), add constraint FK_ggiat31u2w3f20rt6v2ix8vkl foreign key (tweet_tweet_id) references tweet (tweet_id)
alter table transaction add index FK_tqnyif7kix8ud7lfl27w7lh06 (subscription_id), add constraint FK_tqnyif7kix8ud7lfl27w7lh06 foreign key (subscription_id) references subscription_plan (subscription_id)
alter table transaction add index FK_syue16450hrqk910w0if4e778 (account_id), add constraint FK_syue16450hrqk910w0if4e778 foreign key (account_id) references account (account_id)
alter table transaction add index FK_syue16450hrqk910w0if4e778 (account_id), add constraint FK_syue16450hrqk910w0if4e778 foreign key (account_id) references account (account_id)
alter table tweet add index FK_svwfc7s5tpwmxjtuy2al9u6u2 (sender_id), add constraint FK_svwfc7s5tpwmxjtuy2al9u6u2 foreign key (sender_id) references account (account_id)
alter table tweet_hashtag add index FK_nkbyjrr4bnr4fyjce45golmmj (id), add constraint FK_nkbyjrr4bnr4fyjce45golmmj foreign key (id) references hashtag (id)
alter table tweet_hashtag add index FK_edgxxcnw9t44ye5rs7u9uosed (tweet_id), add constraint FK_edgxxcnw9t44ye5rs7u9uosed foreign key (tweet_id) references tweet (tweet_id)
select account0_.account_id as account_2_0_, account0_.time_created as time_cre3_0_, account0_.time_updated as time_upd4_0_, account0_.access_token as access_t5_0_, account0_.city as city6_0_, account0_.email as email7_0_, account0_.facebook_id as facebook8_0_, account0_.image_url as image_ur9_0_, account0_.last_login as last_lo10_0_, account0_.neighborhood_id as neighbo30_0_, account0_.password as passwor11_0_, account0_.role as role12_0_, account0_.state as state13_0_, account0_.status as status14_0_, account0_.uid as uid15_0_, account0_.profile_url as profile16_0_, account0_.zip as zip17_0_, account0_.first_name as first_n18_0_, account0_.gender as gender19_0_, account0_.introduction as introdu20_0_, account0_.last_name as last_na21_0_, account0_.occupation as occupat22_0_, account0_.businessType as busines23_0_, account0_.category as categor24_0_, account0_.name as name25_0_, account0_.phone as phone26_0_, account0_.properties_id as propert31_0_, account0_.street1 as street27_0_, account0_.street2 as street28_0_, account0_.website as website29_0_, account0_.type as type1_0_ from account account0_ where account0_.email=?
