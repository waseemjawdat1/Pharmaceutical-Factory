package application;

public class ViewOrderDetails {
	private int productID;
	private String productName;
	private int quantity;
	private double price;

	public ViewOrderDetails(int productID, String productName, int quantity, double price) {
		this.productID = productID;
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
