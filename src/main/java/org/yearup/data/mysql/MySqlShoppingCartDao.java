package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getCart(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //create query where user retrieves their items
        String sql = """
                 SELECT cart.user_id,\s
                 cart.product_id AS product_id,
                 cart.quantity,\s
                 prod.name, prod.price, prod.category_id, prod.description, prod.subcategory, prod.image_url, prod.stock, prod.featured
                 FROM shopping_cart cart
                 JOIN products prod ON cart.product_id = prod.product_id
                 WHERE cart.user_id = ?
                \s""";

        List<ShoppingCartItem> itemList = getShoppingCartItems(userId, sql);
        Map<Integer, ShoppingCartItem> items = new HashMap<>();
        for (ShoppingCartItem item : itemList) { //look for each item present within the list
            items.put(item.getProductId(), item); // HashMap put method
        }

        shoppingCart.setItems(items);
        return shoppingCart;
    }


    private ArrayList<ShoppingCartItem> getShoppingCartItems(int userId, String sql) {
        ArrayList<ShoppingCartItem> itemList = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet rows = statement.executeQuery();

            while (rows.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProductId(rows.getInt("product_id"));
                item.setQuantity(rows.getInt("quantity"));

                itemList.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return itemList;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        return null;
    }

    @Override
    public ShoppingCart addItem(int userId, int productId, int quantity) {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getCart(userId);

    }

    @Override
    public ShoppingCart insert(ShoppingCartItem shoppingCartItem) {
        return null;
    }

    // same as add
//@Override
//public ShoppingCart insert(ShoppingCartItem shoppingCartItem) {
//
//    return null;
//    }


    @Override
    public void update(int userId, int productId, int quantity) {
        // TODO
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? " +
                "AND product_id = ?";

        try (Connection connection = getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(int userId, int productId, ShoppingCartItem shoppingCartItem) {

    }

//    @Override
//    public void delete(int userId, int productId, ShoppingCartItem shoppingCartItem) {
//        // TODO


    @Override
    public void clear(int userId) {

        // TODO
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        try (Connection connection = getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }
}














