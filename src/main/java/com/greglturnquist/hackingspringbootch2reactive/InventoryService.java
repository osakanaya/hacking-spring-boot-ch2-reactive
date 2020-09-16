package com.greglturnquist.hackingspringbootch2reactive;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import reactor.core.publisher.Flux;

@Service
public class InventoryService {
	private ItemRepository repository;
//	private ItemByExampleRepository exampleRepository;
	private ReactiveFluentMongoOperations fluentOperations;
	
	InventoryService(
			ItemRepository repository, 
//			ItemByExampleRepository exampleRepository,
			ReactiveFluentMongoOperations fluentOperations) {
		this.repository = repository;
//		this.exampleRepository = exampleRepository;
		this.fluentOperations = fluentOperations;
	}
	
	Flux<Item> getItems() {
		return Flux.empty();
	}
	
	Flux<Item> search(String partialName, String partialDescription, boolean useAnd) {
		if (partialName != null) {
			if (partialDescription != null) {
				if (useAnd) {
					return repository.findByNameContainingAndDescriptionContainingAllIgnoreCase(partialName, partialDescription);
				} else {
					return repository.findByNameContainingOrDescriptionContainingAllIgnoreCase(partialName, partialDescription);
				}
			} else {
				return repository.findByNameContainingIgnoreCase(partialName);
			}
		} else {
			if (partialDescription != null) {
				return repository.findByDescriptionContainingIgnoreCase(partialName);
			} else {
				return repository.findAll();
			}
		}
	}
	
	Flux<Item> searchByExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0);
		
		ExampleMatcher matcher = (useAnd
			? ExampleMatcher.matchingAll()
			: ExampleMatcher.matchingAny())
				.withStringMatcher(StringMatcher.CONTAINING)
				.withIgnoreCase()
				.withIgnorePaths("price");
		
		Example<Item> probe = Example.of(item, matcher);
		
		return repository.findAll(probe);
	}
	
	Flux<Item> serachByFluentExample(String name, String description) {
		return fluentOperations.query(Item.class)
			.matching(query(where("TV tray").is(name).and("Smurf").is(description)))
			.all();
	}
	
	Flux<Item> serchByFluentExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0);
		
		ExampleMatcher matcher = (useAnd
			? ExampleMatcher.matchingAll()
			: ExampleMatcher.matchingAny())
				.withStringMatcher(StringMatcher.CONTAINING)
				.withIgnoreCase()
				.withIgnorePaths("price");
		
		return fluentOperations.query(Item.class)
			.matching(query(byExample(Example.of(item, matcher))))
			.all();
	}
}
