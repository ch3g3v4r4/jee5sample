package com.example.sample.model.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Stateful
public class CartBean implements Cart {

	private String username;
	private List<String> items;

	/* (non-Javadoc)
	 * @see com.example.sample.model.business.Cart#beginShopping(java.lang.String)
	 */
	public void beginShopping(String username) {
		this.username = username;
		this.items = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see com.example.sample.model.business.Cart#addItem(java.lang.String)
	 */
	public void addItem(String item) {
		this.items.add(item);
	}

	/* (non-Javadoc)
	 * @see com.example.sample.model.business.Cart#finishShopping()
	 */
	@Remove
	public void finishShopping() {
		System.out.println(this.items);
	}

}
