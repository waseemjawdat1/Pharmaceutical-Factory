package application;

public class RecipeRequirement {
	public RawMaterial material;
	public int requiredQuantity;

	public RecipeRequirement(RawMaterial material, int requiredQuantity) {
		this.material = material;
		this.requiredQuantity = requiredQuantity;
	}

	public RawMaterial getMaterial() {
		return material;
	}

	public void setMaterial(RawMaterial material) {
		this.material = material;
	}

	public int getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(int requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}
	
}
