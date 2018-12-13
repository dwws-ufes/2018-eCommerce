package com.ecommerce.login.repository;

import com.ecommerce.cart.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("cartRepository")
public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Autowired
	Cart findByEmail(String email);
}