package com.mozart.webfluxdemo.initialize;


import com.mozart.webfluxdemo.document.Item;
import com.mozart.webfluxdemo.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {
	
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ReactiveMongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {

        initalDataSetUp();
     //   createCappedCollection();
     //   dataSetUpforCappedCollection();
    }


    public List<Item> data() {

        return Arrays.asList(new Item(null, "iphone 12 Pro Max", 1199.99),
                new Item(null, "Apple Airpods Max", 549.00),
                new Item(null, "Apple Watch", 449.99),
                new Item("ABC", "MacBook Pro", 2900.00));
    }


    private void initalDataSetUp() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                        .flatMap(itemReactiveRepository::save)
                        .thenMany(itemReactiveRepository.findAll())
                        .subscribe((item -> {
                            System.out.println("Item inserted from CommandLineRunner : " + item);
                        }));

    }

}
