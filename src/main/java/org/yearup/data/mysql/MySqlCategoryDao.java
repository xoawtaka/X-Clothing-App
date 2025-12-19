package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        // get all categories
        String sql = "SELECT category_id, name, description FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery())
        {
            while (rs.next())
            {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));

                categories.add(category);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error getting all categories", e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        // get category by id
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, categoryId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getInt("category_id"));   // or "categoryId" if that's your actual column
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));
                    return category;

                } else {

                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category create(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        // create a new category


        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            int affected = statement.executeUpdate();

            if (affected > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    // category.setCategoryId(keys.getInt(1));
                    int anotherID = keys.getInt(1);
                    return (getById(anotherID));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;


    }

    @Override
    public Category insert(Category category) {
        return null;
    }

   /* @Override
    public Category insert(Category category) {
        return null;
    }*/

    @Override
    public void update(int categoryId, Category category) {
        String sql =
                "UPDATE categories " +
                "SET name = ?, description = ? " +
                "WHERE category_id = ?";


        // update category
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE  = ?";
        // delete category
        try (Connection connection = getConnection()) {

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, categoryId);

        statement.executeUpdate();

    } catch(SQLException e) {
        throw new RuntimeException(e);
    }

    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
