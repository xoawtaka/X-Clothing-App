package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao {


        // add additional signatures here

        ShoppingCart getByUserId(int userId);

        // returns the user's cart items
        ShoppingCart getCart(int userId);

        ShoppingCart addItem(int id, int productId, int quantity);

        ShoppingCart insert(ShoppingCartItem shoppingCartItem);

        void update(int userId, int productId, int quantity);

        // removes one product from the user's cart
        void delete(int userId, int productId, ShoppingCartItem shoppingCartItem);

        // clear the users cart
        void clear(int userId);

}

// use the shoppingcartDao to get all items in the cart and return the cart
// update
// put
// delete
