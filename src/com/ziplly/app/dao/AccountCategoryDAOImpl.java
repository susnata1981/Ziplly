package com.ziplly.app.dao;

import static com.ziplly.app.dao.OfyService.ofy;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.literati.app.shared.Account;
import com.literati.app.shared.AccountCategory;
import com.literati.app.shared.Category;
import com.literati.app.shared.Field;
import com.literati.app.shared.Operator;
import com.literati.app.shared.Predicate;
import com.ziplly.app.dao.query.QueryBuilder.Builder;

public class AccountCategoryDAOImpl implements AccountCategoryDAO {

	private final DataStore ds = new DataStoreImpl();
	
	@Override
	public List<Account> getAccountByCategoryId(Long categoryId) {
		Builder<AccountCategoryDO> builder = Builder.newBuilder();
		builder.setEntity(AccountCategoryDO.class).setPredicate(
				new Predicate(Field.CATEGORY_KEY, Operator.NONE, categoryId));
		List<AccountCategoryDO> acList = ds.getList(builder);
		List<Account> result = Lists.newArrayList();
		for(AccountCategoryDO ac : acList) {
			result.add(ac.getAccount());
		}
		return result;
	}

	@Override
	public boolean save(AccountCategory ac) {
		Account account = ac.getAccount();
		assert(account!=null);
		AccountCategoryDO accountCategory = ofy().load().type(AccountCategoryDO.class)
				.ancestor(Key.create(Account.class,account.getId()))
				.filter("category", Key.create(Category.class,ac.getCategory().getId()))
				.first().get();

		if (accountCategory == null) {
			AccountCategoryDO acdo = new AccountCategoryDO();
			acdo.setAccount(Key.create(Account.class,ac.getAccount().getId()));
			acdo.setCategory(Key.create(Category.class,ac.getCategory().getId()));
			Key<AccountCategoryDO> now = ofy().save().entity(acdo).now();
			return now != null;
		} 
		
		throw new RuntimeException("AccountCategory already exists!");
	}

	@Override
	public List<Category> getCategories(Account account) {
		Builder<AccountCategoryDO> builder = Builder.newBuilder();
		builder.setEntity(AccountCategoryDO.class).setPredicate(
				new Predicate(Account.class,Operator.ANCESTOR, account));
		List<AccountCategoryDO> acList = ds.getList(builder);
		List<Category> result = Lists.newArrayList();
		for(AccountCategoryDO ac : acList) {
			result.add(ac.getCategory());
		}
		return result;
	}

	@Override
	public boolean save(List<AccountCategory> accountCategories) {
		int count = 0;
		if (accountCategories.size() == 0) {
			return true;
		}
//		Account account = accountCategories.get(0).getAccount();
//		deleteCategoriesForAccount(account);
		for(AccountCategory ac : accountCategories) {
			try {
				if (save(ac)) {
					count++;
				}
			} catch(RuntimeException re) {
				//sink AccountCategory already exists exception
				count++;
			}
		}
		return count == accountCategories.size();
	}
	
	// TODO transaction support
	public boolean deleteCategoriesForAccount(Account account) {
		List<AccountCategoryDO> result = ofy().load().type(AccountCategoryDO.class)
				.ancestor(Key.create(Account.class, account.getId())).list();
		ofy().delete().entities(result);
		return true;
	}

	@Override
	public List<Account> getAccountsByCategoryNames(List<String> categories) {
		if (categories == null) {
			throw new IllegalArgumentException();
		}
		
		if (categories.size() == 0) {
			return ImmutableList.of();
		}
		
		List<AccountCategoryDO> result = null;
		for(String categoryName : categories) {
			Category category = ofy().load().type(Category.class)
					.filter("categoryType", categoryName.trim().toLowerCase()).first().get();
			
			if (category == null) {
				// TODO log
				continue;
			}
			
			result = ofy().load().type(AccountCategoryDO.class)
					.filter("category", Key.create(Category.class, category.getId())).list();
		}
		
		if (result == null) {
			return ImmutableList.of();
		}
		List<Account> response = Lists.transform(result, new Function<AccountCategoryDO, Account>() {
			@Override
			@Nullable
			public Account apply(@Nullable AccountCategoryDO acDO) {
				return acDO.getAccount();
			}
		});
		
		return response;
	}
}
