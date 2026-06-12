package ui;

import table.MenuModel;

public class OrderItem {
    private MenuModel menu;
    private int qty;

    public OrderItem(MenuModel menu) {
        this.menu = menu;
        this.qty  = 1;
    }

    public MenuModel getMenu() { return menu; }
    public int  getQty()       { return qty; }
    public void addQty()       { qty++; }
    public void minusQty()     { qty--; }

    public double getSubTotal() {
        return menu.getPrice() * qty;
    }
}