package com.ziplly.app.server.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ziplly.app.model.OrderStatus;

@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;  
  
  @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<OrderDetails> orderDetails = new HashSet<OrderDetails>();
  
  @Column(name = "status", nullable = false)
  private String status;
  
  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "transaction_id", nullable = false)
  private Transaction transaction;

  public Long getId() {
    return id;
  }

  public void setId(Long orderId) {
    this.id = orderId;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }

  public OrderStatus getStatus() {
    return OrderStatus.valueOf(status);
  }

  public void setStatus(OrderStatus status) {
    this.status = status.name();
  }

  public Set<OrderDetails> getOrderDetails() {
    return orderDetails;
  }

  public void setOrderDetails(Set<OrderDetails> orderDetails) {
    this.orderDetails = orderDetails;
  }
  
  public void addOrderDetails(OrderDetails details) {
    this.orderDetails.add(details);
  }
  
  @Override
  public String toString() {
    return "[id = "+id+" status = "+status+" transaction_id = "+transaction.getTransactionId()+"]";
  }
}
