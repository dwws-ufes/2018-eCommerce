package com.ecommerce.cart.service;

import com.ecommerce.login.repository.CartRepository;
import com.ecommerce.login.repository.RoleRepository;
import com.ecommerce.login.repository.UserRepository;
import com.ecommerce.cart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service("cartService")
public class CartService {
	@Autowired
    private CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
    	this.cartRepository = cartRepository;
    }

    public Cart findUserByEmail(String email) {
        return cartRepository.findByEmail(email);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

}
