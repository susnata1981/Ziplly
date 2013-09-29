package com.ziplly.app.dao;

import static com.ziplly.app.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.literati.app.shared.Category;
import com.literati.app.shared.CategoryDetails;
import com.ziplly.app.dao.query.QueryBuilder.Builder;

public class CategoryDAOImpl implements CategoryDAO {
	private Logger logger = Logger.getLogger("CateogryDAOImpl");
	private final DataStore ds = new DataStoreImpl();

	@Override
	public Category findById(Long id) {
		Category category = ofy().load().type(Category.class).id(id).get();
		return category;
	}

	@Override
	public Category findByType(String type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		Category category = ofy().load().type(Category.class)
				.filter("categoryType =", type.toLowerCase()).first().get();
		if (category == null) {
			System.out.println("Category: "+type+" doesn't exist.");
			category = new Category();
			category.setCategory(type.trim().toLowerCase());
			save(category);
			assert category.getId() != null;
			System.out.println("Created category:"+category.getId());
		}
		return category;
	}

	@Override
	public boolean save(Category category) {
		List<Category> existingCateogry = ofy().load().type(Category.class).filter("categoryType", 
				category.getCategoryType().trim().toLowerCase()).list();
		if (existingCateogry.size()>1) {
			logger.log(Level.SEVERE, "Multiple category entries for "+category.getCategoryType());
			throw new RuntimeException("Multiple category entries for "+category.getCategoryType());
		}
		// no need to create it if it already exists
		if (existingCateogry.size() == 1) {
			return true;
		}
		Key<Category> now = ofy().save().entity(category).now();
		return now != null;
	}

	@Override
	public List<Category> findByName(List<Category> categories) {
		List<Category> result = new ArrayList<Category>();
		for(Category c : categories) {
			Category category = findByType(c.getCategoryType());
			result.add(category);
		}
		return result;
	}

	@Override
	public List<Category> getAll() {
		Builder<Category> builder = Builder.newBuilder();
		builder.setEntity(Category.class);
		List<Category> categories = ds.getList(builder);
		return categories;
	}
	
	public List<CategoryDetails> getPopularCategoryWithAccounts() {
		return ds.getPopularCategoryWithAccounts();
	}
}
