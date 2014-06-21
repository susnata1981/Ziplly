package com.ziplly.app.server.model.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.google.common.collect.Lists;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessCategory;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.overlay.SubscriptionDTO;

@Entity
@DiscriminatorValue("business")
public class BusinessAccount extends Account {
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private String website;

	@Column(name = "business_type")
	private String businessType;

	private String category;

	@OneToOne(cascade = CascadeType.ALL)
	private BusinessProperties properties;

	@OneToMany(mappedBy = "account")
	@Fetch(FetchMode.JOIN)
	private Set<Subscription> subscriptions = new HashSet<Subscription>();

	public BusinessAccount() {
	}

	public BusinessAccount(BusinessAccountDTO account) {
		super(account);
		this.name = account.getName();
		this.phone = account.getPhone();
		this.website = account.getWebsite();
		this.businessType = BusinessType.COMMERCIAL.name();
		this.category = account.getCategory().name();
		this.properties = new BusinessProperties(account.getProperties());
		
		for(SubscriptionDTO subscription : account.getTransactions()) {
			this.subscriptions.add(new Subscription(subscription));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Collection<Subscription> getSubscriptions() {
	  ArrayList<Subscription> sortedSubscription = Lists.newArrayList(subscriptions);
	  Collections.sort(sortedSubscription, new Comparator<Subscription>() {

      @Override
      public int compare(Subscription o1, Subscription o2) {
        return (int) (o2.getTimeCreated().compareTo(o1.getTimeCreated()));
      }
	    
	  });
		return sortedSubscription;
	}

	public void setSubscriptions(Set<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public BusinessProperties getProperties() {
		return properties;
	}

	public void setProperties(BusinessProperties properties) {
		this.properties = properties;
	}

	public BusinessType getBusinessType() {
		return BusinessType.valueOf(businessType);
	}

	public void setBusinessType(BusinessType type) {
		this.businessType = type.name();
	}

	public BusinessCategory getCategory() {
		return BusinessCategory.valueOf(category);
	}

	public void setCategory(BusinessCategory category) {
		this.category = category.name();
	}
	
	public Subscription getLatestSubscription() {
	  if (subscriptions.size() == 0) {
	    return null;
	  }
	  
	  List<Subscription> subscriptionList = Lists.newArrayList(subscriptions);
    Collections.sort(subscriptionList, new Comparator<Subscription>() {

      @Override
      public int compare(Subscription o1, Subscription o2) {
        return (int) (o2.getTimeCreated().compareTo(o1.getTimeCreated()));
      }
    });
    
    return subscriptionList.get(0);
  }
}
