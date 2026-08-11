# NU Cafe Program — User Manual

CS 5004 Final Project — Team 7

How to launch the app, and how customers and staff use each screen.

## 1. Launch

```bash
./gradlew run
```

Requires JDK 17+.

Choose a role on the first screen.

![Role selection](screenshots/role_selection.png)

---

## 2. Customer

### 2.1 Browse menu

Choose **Customer**. Menu items appear by category, and click items to show images.

![Browse menu](screenshots/browse_menu.png)

### 2.2 Search and filter

Use the category dropdown to find all main items. Use search box to search for coffee.

![Filter by MAIN](screenshots/filter_by_main_category.png)

![Search for Coffee](screenshots/search_for_coffee.png)

### 2.3 Cart and checkout

Add items to the cart, change quantities of cake, then **Checkout** with total price and clear the cart.

![Add items to cart](screenshots/add_items_to_cart.png)

![Change quantity](screenshots/change_quantity_of_cake.png)

![Ready to checkout](screenshots/ready_to_checkout.png)

![Checkout clears cart](screenshots/checkout_and_clear_cart.png)

### 2.4 Favorites

Add items to favorites. **Save** favorites into a JSON file.  
Clear the favorites to **load** the saved JSON file, and the panel is updated after loading.  
**Modify** the favorites and save it again to update.

![Add to favorites](screenshots/add_items_into_favorites.png)

![Save favorites dialog](screenshots/save_favorites_Json.png)

![Saved favorites file](screenshots/saved_favorites_file.png)

![Clear favorites](screenshots/clear_favorites.png)

![Select favorites file](screenshots/select_favorites_file.png)

![Load favorites successfully](screenshots/load_favorites_successfully.png)

![Modify favorites](screenshots/modify_favorites.png)

---

## 3. Staff

Use **Switch role**, then choose **Staff**, to open inventory and sales.

### 3.1 Inventory

View stock.

![Inventory](screenshots/inventory.png)

### 3.2 Restock

Select cake and add 1 with restock button. Stock updates in the table.

![Restock cake](screenshots/restock_cake.png)

![Restock successfully](screenshots/restock_successfully.png)

### 3.3 Low-stock export

Set a threshold and export the low-stock name list to JSON.

![Export low stock](screenshots/export_low_stock_list.png)

![Low stock JSON](screenshots/low_stock_list.png)

### 3.4 Sales chart

Bar chart of revenue by category (MAIN / DESSERT / BEVERAGE) after checkouts.

![Sales chart](screenshots/sales_chart.png)
