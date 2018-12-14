package com.ecommerce.login.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.ecommerce.cart.*;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.login.model.User;
import com.ecommerce.login.repository.UserRepository;
import com.ecommerce.login.service.UserService;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	private static class products {
	   List<String> description;
	   List<String> name;
	}
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
    static products getDescriptions() {
    	products descriptionList = new products();
    	descriptionList.description = new ArrayList<String>();
    	descriptionList.name = new ArrayList<String>();
    	
    	String query = 	"PREFIX dbo: <http://dbpedia.org/ontology/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX dbp: <http://dbpedia.org/property/>\n" + 
				"select distinct ?label ?abstract where\n" + 
				"{\n" + 
				"?phone <http://dbpedia.org/property/type> <http://dbpedia.org/resource/Smartphone>.\n" + 
				"?phone rdfs:label ?label .\n" + 
				"?phone dbo:abstract ?abstract .\n" + 
				"?phone dbp:cpu ?cpu\n" + 
				"FILTER(regex(lcase(?label), \"iphone\"))\n" + 
				"FILTER(LANG(?abstract) = 'en' && LANG(?label) = 'en')\n" + 
				"} LIMIT 10";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

		ResultSet results = queryExecution.execSelect();

		while (results.hasNext()) {
			QuerySolution querySolution = results.next();
			descriptionList.description.add(querySolution.get("abstract").toString());
			descriptionList.name.add(querySolution.get("label").toString());

		}

		queryExecution.close();
		
		return descriptionList;

	}
    
  @RequestMapping(value={"/escreve/{nome}"}, method = RequestMethod.GET)
  @ResponseBody
  public void escreve(@PathVariable String nome, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
  	
  	System.out.println(nome);
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      User user = userService.findUserByEmail(auth.getName());
      Cart cart = cartService.findUserByEmail(user.getEmail());
		response.setContentType("text/xml");
		
		 //Lista de todas as localidades cadastradas
		//List<Localidade> localidades = localidadeService.listar();
//		
		Model model = ModelFactory.createDefaultModel();
//		
		String myNS = "http://localhost:8080/Ecommerce/data/produtos/";
//		

		String smartNS = "http://dbpedia.org/ontology/Smartphone/";
		model.setNsPrefix("Smartphone", smartNS);
		Resource smartIphone = ResourceFactory.createResource(smartNS + "iphone");
//		
//		// Pos
		String posNS = "http://dbpedia.org/ontology/price";
		model.setNsPrefix("pos", posNS);
		Property suggested = ResourceFactory.createProperty(posNS + "suggested");
		Property description = ResourceFactory.createProperty(posNS + "description");
		Property productName = ResourceFactory.createProperty(posNS + "productName");
//		
//		// Currency amount
		int i=0;
		
		String produto;
		String desc;
		products descriptions = getDescriptions();
		for(i=0;i<descriptions.description.size();i++) {
			desc = (descriptions.description.get(i));
			produto = (descriptions.name.get(i));
			model.createResource(myNS + nome + i)
			.addProperty(RDF.type, smartIphone)
			//.addProperty(RDFS.label, nome)
			.addProperty(suggested, nome)
			.addProperty(description, desc)
			.addProperty(productName, produto);
		}

		
		try (PrintWriter out = response.getWriter()) {
			model.write(out, "RDF/XML");
		}
  	System.out.println("entrou");
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
        modelAndView.addObject("id", "nomeProduto");
        products descriptions = getDescriptions();
        cart.setDescription(descriptions.description.get(0));
        cart.setName(descriptions.name.get(0));
        modelAndView.addObject("description", descriptions.description);
        modelAndView.addObject("productName", descriptions.name);
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
        modelAndView.addObject("id", "nomeProduto");
        products descriptions = getCameraDescription();
        modelAndView.addObject("description", descriptions.description);
        modelAndView.addObject("productName", descriptions.name);
        return modelAndView;
    }

    static products getCameraDescription() {
    	products descriptionList = new products();
    	descriptionList.description = new ArrayList<String>();
    	descriptionList.name = new ArrayList<String>();
    	
    	String query = 	"PREFIX dbo: <http://dbpedia.org/ontology/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX dbp: <http://dbpedia.org/property/>\n" + 
				"select distinct ?label ?abstract where\n" + 
				"{\n" + 
				"?camera <http://purl.org/linguistics/gold/hypernym> <http://dbpedia.org/resource/Camera> .\n" + 
				"?camera rdfs:label ?label .\n" + 
				"?camera dbo:abstract ?abstract .\n" +
				"FILTER(LANG(?abstract) = 'en' && LANG(?label) = 'en')\n" + 
				"FILTER(regex(lcase(?label), \"s9500\"))\n" + 
				"} LIMIT 10";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

		ResultSet results = queryExecution.execSelect();

		while (results.hasNext()) {
			QuerySolution querySolution = results.next();
			descriptionList.description.add(querySolution.get("abstract").toString());
			descriptionList.name.add(querySolution.get("label").toString());
		}
        queryExecution.close();
		
		return descriptionList;
    }

    @RequestMapping(value={"/escreveReview/{nome}"}, method = RequestMethod.GET)
    @ResponseBody
    public void escreveReview(@PathVariable String nome, HttpServletRequest request, HttpServletResponse response)
  			throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Cart cart = cartService.findUserByEmail(user.getEmail());
  		response.setContentType("text/xml");
  
  		 //Lista de todas as localidades cadastradas
  		//List<Localidade> localidades = localidadeService.listar();
  		
  		Model model = ModelFactory.createDefaultModel();
  		
  		String myNS = "http://localhost:8080/Ecommerce/data/produtos/";
  		
  
  		String smartNS = "http://dbpedia.org/resource/Camera/";
  		model.setNsPrefix("Camera", smartNS);
  		Resource smartFuji = ResourceFactory.createResource(smartNS + "Fuji");
  		
  		// Pos
  		String posNS = "http://dbpedia.org/ontology/review/";
  		model.setNsPrefix("pos", posNS);
  		Property productReview = ResourceFactory.createProperty(posNS + "productReview");
		Property suggested = ResourceFactory.createProperty(posNS + "suggested");
  		Property description = ResourceFactory.createProperty(posNS + "description");
  		Property productName = ResourceFactory.createProperty(posNS + "productName");
  		
  		// Currency amount
  		int i=0;
  
  		String produto;
  		String desc;
  		products descriptions = getCameraDescription();
  		for(i=0;i<descriptions.description.size();i++) {
  			desc = (descriptions.description.get(i));
  			produto = (descriptions.name.get(i));
  			model.createResource(myNS + nome + i)
  			.addProperty(RDF.type, smartFuji)
  			//.addProperty(RDFS.label, nome)
            .addProperty(suggested, nome)
  			.addProperty(description, desc)
  			.addProperty(productName, produto);
  		}
  
  
  		try (PrintWriter out = response.getWriter()) {
  			model.write(out, "RDF/XML");
  		}
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
