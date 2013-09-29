package com.ziplly.app.dao;

import java.util.List;

import com.literati.app.shared.Category;
import com.literati.app.shared.CategoryDetails;

public interface CategoryDAO {
	Category findById(Long id);
	List<Category> findByName(List<Category> categories);
	boolean save(Category category);
	public List<Category> getAll();
	public List<CategoryDetails> getPopularCategoryWithAccounts();
	Category findByType(String type); 
}
