package com.greglturnquist.hackingspringbootch2reactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import reactor.core.publisher.Mono;

@Controller
public class HomeController {
	private ItemRepository itemRepository;
	private CartRepository cartRepository;
	
	private InventoryService inventoryService;
	private CartService cartService;
	
	public HomeController(ItemRepository itemRepository, CartRepository cartRepository,
			InventoryService inventoryService, CartService cartService) {
		this.itemRepository = itemRepository;
		this.cartRepository = cartRepository;

		this.inventoryService = inventoryService;
		this.cartService = cartService;
	}
	
	@GetMapping
	Mono<Rendering> home() {
		return Mono.just(
			Rendering.view("home.html")
			.modelAttribute("items", this.itemRepository.findAll())
			.modelAttribute("cart", this.cartRepository.findById("My Cart")
					.defaultIfEmpty(new Cart("My Cart")))
			.build()
		);
	}
	
	@PostMapping("/add/{id}")
	Mono<String> addCart(@PathVariable String id) {
		return this.cartService.addToCart("My Cart", id)
			.thenReturn("redirect:/");
	}
	
	@PostMapping
	Mono<String> createItem(@ModelAttribute Item newItem) {
		return this.itemRepository.save(newItem)
			.thenReturn("redirect:/");
	}

	@DeleteMapping("/delete/{id}")
	Mono<String> deleteItem(@PathVariable String id) {
		return this.itemRepository.deleteById(id)
			.thenReturn("redirect:/");
	}
	
	Mono<Rendering> serach(
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String description,
		@RequestParam boolean useAnd) {
		
		return Mono.just(Rendering.view("home.html")
			.modelAttribute("results", inventoryService.searchByExample(name, description, useAnd))
			.build());
	}
}
