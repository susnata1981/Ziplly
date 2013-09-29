package com.ziplly.app.dao;

import static com.ziplly.app.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.objectify.Key;
import com.literati.app.shared.Account;
import com.literati.app.shared.AccountCategory;
import com.literati.app.shared.Category;
import com.literati.app.shared.CategoryDetails;
import com.literati.app.shared.CategoryType;
import com.literati.app.shared.Predicate;
import com.ziplly.app.dao.query.QueryBuilder;
import com.ziplly.app.dao.query.QueryBuilder.Builder;

public class FakeDataStoreImpl implements DataStore {
	private List<Account> accounts = Lists.newArrayList();
	private String [] firstNames = {
			"Jack","Tom","Harry","Erica","Shaan","Mike","Matt"
	};
	private String [] lastNames = {
		"Kent","Metzger","Basak","Roy","Robbin","Potter"
	};
	
	private List<Category> categories = Lists.newArrayList();
	private List<AccountCategoryDO> aCategoryList = Lists.newArrayList();
	
	private static FakeDataStoreImpl INSTANCE = new FakeDataStoreImpl();
	private AccountCategoryDAO acDao = new AccountCategoryDAOImpl();

	public static FakeDataStoreImpl getInstance() {
		return INSTANCE;
	}
	
	private FakeDataStoreImpl() {
		setup();
//		createAccounts();
	}
	
	private void setup() {
		for(CategoryType ct : CategoryType.values()) {
			Category c = new Category();
			c.setCategoryType(ct);
			categories.add(c);
		}
		
		Random rnd = new Random();
		for(String fn: firstNames) {
			for(String ln : lastNames) {
				Account a = new Account();
				a.setFirstName(fn);
				a.setLastName(ln);
				a.setEmail(fn+"."+ln+"@gmail.com");
				a.setIntroduction("Hi this is"+firstNames+". Check me out!");
				a.setImageUrl("http://media.cagle.com/81/2013/08/27/136610_600.jpg");
				a.setUrl("https://www.facebook.com/profile.php?id=10044120");
				a.setZip(98100+(int)(Math.random()*100));
				accounts.add(a);
				
				List<AccountCategory> acList = Lists.newArrayList();
				int size = rnd.nextInt(4);
				for(int i=0;i<size;i++) {
					AccountCategory ac = new AccountCategory();
					ac.setAccount(a);
					ac.setCategory(categories.get(rnd.nextInt(categories.size())));
					ac.setCategory(categories.get(rnd.nextInt(categories.size())));
					acList.add(ac);
				}

				try {
					boolean save = acDao.save(acList);
					if (!save) {
						throw new RuntimeException("Failed to create AC list");
					}
				} catch (RuntimeException re) {
					// sink accountcategory already exists;
				}
			}
		}
	}
	
	/*
	 * Used to bootstrap database
	 */
	private void createAccounts() {
		ofy().save().entities(categories).now();
		ofy().save().entities(accounts).now();
		Random rn = new Random();
		for(Account a: accounts) {
			int nextInt = rn.nextInt(categories.size());
			Category category = categories.get(nextInt);
			AccountCategoryDO ac = new AccountCategoryDO();
			System.out.println("AID="+a.getId());
			ac.setAccount(Key.create(Account.class, a.getId()));
			ac.setCategory(Key.create(Category.class,category.getId()));
			ofy().save().entity(ac).now();
			aCategoryList.add(ac);
		}
		//ofy().save().entities(aCategoryList).now();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Builder<T> builder) {
		//Random rn = new Random();
		QueryBuilder<T> query = builder.build();
		if (query.getEntity().equals(Account.class)) {
			return (List<T>)accounts;
		} else if (query.getEntity().equals(AccountCategoryDO.class)) {
			Predicate predicate = builder.getPredicates().get(0);
			Account a = (Account) predicate.getParent();
			return  (List<T>) getCategory(a.getId());
		} 
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Builder<T> builder) {
		QueryBuilder<T> query = builder.build();
		Random rn = new Random();
		if (query.getEntity().equals(Account.class)) {
			return (T) accounts.get(rn.nextInt(accounts.size()));
		} else if (query.getEntity().equals(AccountCategoryDO.class)) {
			Predicate predicate = builder.getPredicates().get(0);
			Account a = (Account) predicate.getParent();
			return (T) getCategory(a.getId()).get(0);
		}
		return null;
	}
	
	List<AccountCategoryDO> getCategory(long accountId) {
		List<AccountCategoryDO> result = Lists.newArrayList();
		for(AccountCategoryDO ac : aCategoryList) {
			if (ac.getFakeAccount().getId() == accountId) {
				result.add(ac);
			}
		}
		return result;
	}

	@Override
	public List<CategoryDetails> getPopularCategoryWithAccounts() {
		Map<Category,List<Account>> result = Maps.newHashMap();
		for(AccountCategoryDO acdo : aCategoryList){
			Category c = acdo.getFakeCategory();
			if (!result.containsKey(c)) {
				result.put(c,new ArrayList<Account>());
			}
			result.get(c).add(acdo.getFakeAccount());
		}
		List<CategoryDetails> categoryDetailsList = Lists.newArrayList();
		for(Category c: result.keySet()) {
			CategoryDetails cd = new CategoryDetails();
			cd.category = c;
			cd.accounts.addAll(result.get(c));
			categoryDetailsList.add(cd);
		}
		return categoryDetailsList;
	}
	
	public static void main(String[] args) {
		FakeDataStoreImpl fdsi = new FakeDataStoreImpl();
		fdsi.createAccounts();
	}
}
