package application;

public class ViewMaterialDetails {
	private int materialId;
	private String materialName;
	private int quantity;
	private double price;

	public ViewMaterialDetails(int productID, String productName, int quantity, double price) {
		this.materialId = productID;
		this.materialName = productName;
		this.quantity = quantity;
		this.price = price;
	}

	public int getMaterialId() {
		return materialId;
	}


	public String getMaterialName() {
		return materialName;
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
