package com.mozart.webfluxdemo.controller;

import com.mozart.webfluxdemo.document.Item;
import com.mozart.webfluxdemo.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mozart.webfluxdemo.constants.ItemConstants.ITEM_END_POINT;;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @GetMapping(ITEM_END_POINT)
    public Flux<Item> getAllItems(){

       return itemReactiveRepository.findAll();

    }
    
    @GetMapping(ITEM_END_POINT+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){

        return itemReactiveRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
    
    @PostMapping(ITEM_END_POINT)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){

        return itemReactiveRepository.save(item);


    }

    @DeleteMapping(ITEM_END_POINT+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){

        return itemReactiveRepository.deleteById(id);


    }

    @GetMapping(ITEM_END_POINT+"/runtimeException")
    public Flux<Item> runtimeException(){

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred.")));
    }


    @PutMapping(ITEM_END_POINT+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id,
                                                 @RequestBody Item item){

        return itemReactiveRepository.findById(id)
                .flatMap(currentItem -> {

                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
    
    
}
