package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.nio.file.Paths;

public class ShoppingCartItem
{
    private Product product;
    private int productId;
    private int quantity;
    private BigDecimal discountPercent = BigDecimal.ZERO;

    // no arg constr for frameworks / DAO usage
    public ShoppingCartItem()
    {
    }

    public ShoppingCartItem(Product product, int productId, int quantity, BigDecimal discountPercent) {
        this.product = product;

        if (product != null) {
            this.productId = product.getProductId();
        } else {
            this.productId = productId;
        }

        this.quantity = quantity;
        this.discountPercent = (discountPercent != null) ? discountPercent : BigDecimal.ZERO;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
        if (product != null)
        {
            this.productId = product.getProductId();
        }
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = (discountPercent != null ? discountPercent : BigDecimal.ZERO);
    }

    @JsonIgnore
    public int getProductId()
    {
        if (product != null)
        {
            return product.getProductId();
        }
        return productId;
    }

    public BigDecimal getLineTotal()
    {
        // treat as 0 if null
        if (product == null || product.getPrice() == null)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = product.getPrice();
        BigDecimal quantity = new BigDecimal(this.quantity);

        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }
}