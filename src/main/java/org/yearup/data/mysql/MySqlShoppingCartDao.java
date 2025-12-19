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
import java.util.List;
import java.util.Map;

@Component
public class MySqlShoppingCartDao  extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getCart(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //create query where user retrieves their items
        String sql = """
                SELECT cart.user_id, cart.productId, cart.quantity, 
                prod.name, prod.price, prod.category_id, prod.description, prod.subcategory, prod.image_url, prod.stock, prod.featured
                FROM shopping_cart cart
                JOIN products prod ON cart.product_id = prod.product_id
                WHERE cart.user_id = ?
                """;

        ArrayList<ShoppingCartItem> itemList = getShoppingCartItems(userId, sql);
        shoppingCart.setItems((Map<Integer, ShoppingCartItem>) itemList);
        return shoppingCart;
    }

    private ArrayList<ShoppingCartItem> getShoppingCartItems(int userId, String sql) {
        ArrayList<ShoppingCartItem> itemList = new ArrayList<>();

        try(Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet rows = statement.executeQuery();

            while(rows.next())
            {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProductId(rows.getInt("product_id"));
                item.setQuantity(rows.getInt("quantity"));

                itemList.add(item);
            }

        }
        catch (SQLException e)
        {
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
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)" +
                "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            statement.executeQuery();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getCart(userId);



        }
        return null;
    }

    @Override
    public ShoppingCart insert(ShoppingCartItem shoppingCartItem) {
        return null;
    }

    @Override
    public void update(int userId, int productId, int quantity) {

    }

    @Override
    public void delete(int userId, int productId, ShoppingCartItem shoppingCartItem) {

    }

    @Override
    public void clear(int userId) {

    }
}
