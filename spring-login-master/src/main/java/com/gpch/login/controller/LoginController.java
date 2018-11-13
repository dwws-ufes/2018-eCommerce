package com.gpch.login.controller;

import javax.validation.Valid;

import com.gpch.cart.*;
import com.gpch.cart.service.CartService;
import com.gpch.login.model.User;
import com.gpch.login.repository.UserRepository;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    
    
    @Autowired
    private CartService cartService;
    

    @RequestMapping(value={"/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
    @RequestMapping(value={"/product_summary"}, method = RequestMethod.GET)
    public ModelAndView product_summary(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bootstrap-shop/product_summary");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
        int quantidade = Integer.parseInt(cart.getQuantity());
        modelAndView.addObject("quantity", quantidade);
        return modelAndView;
        
    }
    
    
    @RequestMapping(value={"/card"}, method = RequestMethod.GET)
    public ModelAndView cart(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bootstrap-shop/index");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
        int quantidade = Integer.parseInt(cart.getQuantity());
        quantidade++;
        cart.setQuantity(String.valueOf(quantidade));
        cartService.saveCart(cart);
        modelAndView.addObject("userName", user.getName());
        modelAndView.addObject("quantity", quantidade);
        return modelAndView;
        
    }
    
    @RequestMapping(value={"/deleteRegistration"}, method = RequestMethod.GET)
    public ModelAndView deleteRegistration()
    {
        ModelAndView modelAndView = new ModelAndView();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        userService.deleteByEmail(user.getEmail());
        modelAndView.addObject("successMessage", "User has been deleted successfully");
        modelAndView.setViewName("bootstrap-shop/index");
        return modelAndView;
    }
    @RequestMapping(value={"/"}, method = RequestMethod.GET)
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bootstrap-shop/index");
        try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByEmail(auth.getName());
	        Cart cart = cartService.findUserByEmail(user.getEmail());
	        modelAndView.addObject("userName", user.getName());
	        modelAndView.addObject("quantity", cart.getQuantity());
        }catch(Exception e) {
        	
        }
        return modelAndView;
    }
    

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    
    @RequestMapping(value = "/incrementQuantity", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView incrementQuantity(ModelAndView modelAndView) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
        cart.setQuantity(String.valueOf(Integer.parseInt(cart.getQuantity())+ 1));
        int quantidade = Integer.parseInt(cart.getQuantity());
        cart.setPrice(String.valueOf(Integer.parseInt(cart.getQuantity())*quantidade ));
        cartService.saveCart(cart);
        modelAndView.addObject("quantity", quantidade);
        modelAndView.addObject("price", cart.getPrice());
        modelAndView.setViewName("bootstrap-shop/product_summary");
        return modelAndView;
    }
    @RequestMapping(value = "/decrementQuantity", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView decrementQuantity(ModelAndView modelAndView) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
        cart.setQuantity(String.valueOf(Integer.parseInt(cart.getQuantity())- 1));
        int quantidade = Integer.parseInt(cart.getQuantity());
        cart.setPrice(String.valueOf(Integer.parseInt(cart.getQuantity())*quantidade ));
        cartService.saveCart(cart);
        modelAndView.addObject("quantity", quantidade);
        modelAndView.addObject("price", cart.getPrice());
        modelAndView.setViewName("bootstrap-shop/product_summary");
        return modelAndView;
    }
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
        	Cart cart = new Cart();
        	cart.setEmail(user.getEmail());
        	cart.setPrice("0");
        	cart.setProductName("Camera");
        	cart.setQuantity("0");
        	System.out.println(cart.getId());
        	System.out.println(cart.getEmail());
        	System.out.println(cart.getPrice());
        	System.out.println(cart.getProductName());
        	System.out.println(cart.getQuantity());
        	cartService.saveCart(cart);
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.addObject("cart", new Cart());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }


    @RequestMapping(value = "/registrationUpdate", method = RequestMethod.POST)
    public ModelAndView registrationUpdate(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registrationUpdate");
        } else {
        	User userUpdate = userService.findUserByEmail(user.getEmail());
        	userUpdate.setName(user.getName());
        	userUpdate.setLastName(user.getLastName());
        	userUpdate.setPassword(user.getPassword());
            userService.saveUser(userUpdate);
            modelAndView.addObject("successMessage", "User has been updated successfully");
            modelAndView.addObject("user", userUpdate);
            modelAndView.setViewName("registrationUpdate");

        }
        return modelAndView;
    }

    
    @RequestMapping(value="/registrationUpdate", method = RequestMethod.GET)
    public ModelAndView registrationUpdate(){
    	ModelAndView modelAndView = new ModelAndView();
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registrationUpdate");
        return modelAndView;
    }
   
    
    
    @RequestMapping(value="/faq", method = RequestMethod.GET)
    public String faq(){
    	return "/bootstrap-shop/faq";
    }
    @RequestMapping(value="/products", method = RequestMethod.GET)
    public String products(){
    	return "/bootstrap-shop/products";
    }
    
    @RequestMapping(value="/product_details", method = RequestMethod.GET)
    public ModelAndView product_details() {
    	ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bootstrap-shop/product_details");
        return modelAndView;
    }
    
    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")" + cart.getProductName());
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


}
