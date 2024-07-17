package com.example.demo.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.MenuItem;
import com.example.demo.MenuItemRepository;

@RestController
@RequestMapping("/menuItems")
public class MenuItemController {

    //Gives the repository all of the necessary dependencies
    @Autowired
    private MenuItemRepository menuItemRepository;

    //This get mapping takes the simplest get request gives back a List of MenuItem with all menu Items
    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    //This is a more specific Example, By getting through ID
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {

        // Optional because item might not exist
        //And searches on the Repository by the id we defined
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        // If found, returns the object and a '200 ok', otherwise, returns '404 not found' :)
        return menuItem.map(ResponseEntity::ok) //Maps MenuItem to the responseEntity.ok if present
                .orElseGet(() -> ResponseEntity.notFound().build()); //Returns 404 if not.
    }

    /// This is creating a new Menu Item with a Post Request containing "name" and "price" in the call
    @PostMapping
    public ResponseEntity<MenuItem> creatMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return ResponseEntity.created(URI.create("/menuItems/" + savedMenuItem.getId())).body(savedMenuItem);
    }

    //put method that updates the menu Item, needing Id and the changes
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItemDetails) {

        //Optional for possibility of the ID not existing, so search.
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);
        if (optionalMenuItem.isPresent()) {

            //Creates a menuItem object and attributes the item that's being changed, alters necessary params and saves it
            MenuItem menuItem = optionalMenuItem.get();
            menuItem.setName(menuItemDetails.getName());
            menuItem.setPrice(menuItemDetails.getPrice());
            return ResponseEntity.ok(menuItemRepository.save(menuItem));
        } else {
            // Rejects if not found, error 404
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        //Optional for trying to find the id of the Item
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);

        if (optionalMenuItem.isPresent()) {
            //if it exists, it gets deleted from the Repository
            menuItemRepository.delete(optionalMenuItem.get());
            return ResponseEntity.ok().build(); //returns 200
        } else {
            // Rejects if not found, returns 404
            return ResponseEntity.notFound().build();
        }
    }

}
