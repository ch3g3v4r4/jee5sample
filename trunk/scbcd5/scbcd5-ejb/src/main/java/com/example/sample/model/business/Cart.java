package com.example.sample.model.business;

import javax.ejb.Remove;

public interface Cart {

	public abstract void beginShopping(String username);

	public abstract void addItem(String item);

	public abstract void finishShopping();

}