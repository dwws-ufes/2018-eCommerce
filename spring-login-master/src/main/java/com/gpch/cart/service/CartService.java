package com.gpch.cart.service;

import com.gpch.login.repository.CartRepository;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;
import com.gpch.cart.*;
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
