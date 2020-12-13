package com.mozart.webfluxdemo.webclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import static com.mozart.webfluxdemo.constants.ItemConstants.ITEM_END_POINT;

import com.mozart.webfluxdemo.document.Item;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {
	
	WebClient webClient = WebClient.create("http://webfluxdemo-env-1.eba-tqxcqdhv.us-east-1.elasticbeanstalk.com");
	
	@GetMapping("/client/items")
	public Flux<Item> getAllItems(){

		return	webClient
			.get()
			.uri(ITEM_END_POINT)
			.retrieve()
			.bodyToFlux(Item.class)
			.log("Items retrieved by the reactive client : ");
	}
	
	@GetMapping("client/getItemById/{id}")
	public Mono<Item> getItemById( @PathVariable String id){
				
		return webClient
				.get()
				.uri(ITEM_END_POINT + "/{id}", id)
				.retrieve()
				.bodyToMono(Item.class)
				.log("Single Item received by the Client : ");
	}
	
	
	@PostMapping("/client/createItem")
	public Mono<Item> createItem( @RequestBody Item item){
		
		Mono<Item> itemMono = Mono.just(item);
		return webClient
				.post()
				.uri(ITEM_END_POINT)
				.contentType(MediaType.APPLICATION_JSON)
				.body(itemMono, Item.class)
				.retrieve()
				.bodyToMono(Item.class)
				.log("Item Created via web client : ");
	}
	
	@PutMapping("/client/updateItem/{id}")
	public Mono<Item> updateItem( @PathVariable String id, @RequestBody Item item){
		
		Mono<Item> itemBody = Mono.just(item);
		
		return webClient
				.put()
				.uri(ITEM_END_POINT + "/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.body(itemBody, Item.class)
				.retrieve()
				.bodyToMono(Item.class)
				.log("The Item has been updated via web client : ");
	}
	
	
	@DeleteMapping("/client/deleteItem/{id}")
	public Mono<Void> deleteItem( @PathVariable String id){
		
		return webClient
				.delete()
				.uri(ITEM_END_POINT + "/{id}", id)
				.retrieve()
				.bodyToMono(Void.class)
				.log("The Item has been deleted using the webclient : ");
	}
	

	@GetMapping("/client/retrieve/error")
	public Flux<Item> errorRetriever(){
		
		return webClient
				.get()
				.uri(ITEM_END_POINT + "/runtimeException")
				.retrieve()
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> {
				   Mono<String> stringError = clientResponse.bodyToMono(String.class);
						   return stringError.flatMap((errorMessage) -> {
							 log.error(" Here is the Error Message : " + errorMessage);
							 throw new RuntimeException(errorMessage);
						   });
								   
				})
				.bodyToFlux(Item.class);
	}
}
