package com.example.securityApp.rest;

import com.example.securityApp.entities.Items;
import com.example.securityApp.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api" )
public class MainrestControllers {
    @Autowired
    private ItemService itemService;

    @GetMapping(value = "/allItems")
    public ResponseEntity<?> getAllItems()
    {
        List<Items>items=itemService.getAllItems();
         return new ResponseEntity<>(items, HttpStatus.OK);

    }

    @PostMapping(value = "/addItem")
    public ResponseEntity<?>addItem(@RequestBody Items item)
    {
      itemService.addItem(item);
      return new ResponseEntity<>(item,HttpStatus.OK);
    }

    @PostMapping(value = "/toAddItem")
    public ResponseEntity<?>toAddItem(@RequestParam(name = "name") String name,
                                      @RequestParam(name = "price") double price,
                                      @RequestParam(name = "amount") int amount)
    {
        Items item=new Items();
        item.setName(name);
        item.setPrice(price);

        itemService.addItem(item);
        return  new ResponseEntity<>(item,HttpStatus.OK);
    }

    @GetMapping(value = "/getItem")
    public ResponseEntity<?>getItem(@RequestParam(name = "id")Long id)
    {
        Items item=itemService.getItem(id);
        return new ResponseEntity<>(item,HttpStatus.OK);
    }
}
