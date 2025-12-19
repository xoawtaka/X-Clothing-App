package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, ProductDao productDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }


    // each method in this controller requires a Principal object as a parameter - checked - added shopping cart
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart -  checked
            ShoppingCart cart = shoppingCartDao.getCart(userId);
            if (cart == null) {
                // depending on your DAO implementation this might not be needed,
                // but at minimum we don't want to return null
                cart = new ShoppingCart();
            }
            return cart;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }


    // @RequestMapping(value = "/cart", method = RequestMethod.POST)
    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProduct(@PathVariable int productId,
                                   @RequestBody ShoppingCartItem shoppingCartItem,
                                   Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            return shoppingCartDao.addItem(user.getId(), productId, shoppingCartItem.getQuantity());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }


    // add a PUT method to update an existing product in the cart - the url should be - checked
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated) - checked
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    @PutMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable int productId,
                              @RequestBody ShoppingCartItem shoppingCartItem,
                              Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            shoppingCartDao.update(user.getId(), productId, shoppingCartItem.getQuantity());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }


    // add a DELETE method to clear all products from the current users cart -  checked
    // https://localhost:8080/cart

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEverythingFromCart(Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            shoppingCartDao.clear(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

}