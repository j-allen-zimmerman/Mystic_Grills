package model;


public class Order {
	private String orderDate;
	private String orderStatus;
	private int orderId;
	private int orderUser;
	private double orderTotal;
	
	public Order(int orderId,int orderUser, String orderStatus, String orderDate,  double orderTotal) {
		super();
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.orderUser = orderUser;
		this.orderTotal = orderTotal;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getOrderUser() {
		return orderUser;
	}

	public void setOrderUser(int orderUser) {
		this.orderUser = orderUser;
	}

	public double getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}

}