package com.vonage.vnet.param;

import java.util.Set;

import com.vonage.vnet.entity.Product;

public class ShopForm {
private Set<Integer> selectedProductsId;
public Set<Integer> getSelectedProductsId() {
	return selectedProductsId;
}
public void setSelectedProductsId(Set<Integer> selectedProductsId) {
	this.selectedProductsId = selectedProductsId;
}
public ShopForm() {
}

}
