# Feature Requirements

NU Cafe Program — CS 5004 Team 7


## Feature map

The 4 required features and 5 additional features.  

| # | Feature | Type | Where, owner | What to build |
|---|---------|------|--------------|---------------|
| 1 | Graphical user interface | Required | `view`, Boco | `MainFrame` (`CardLayout`), `TabbedRolePanel`, `AppPanel` screens, role selection |
| 2 | View all by category | Required | `Menu.groupByCategory` (model), `MenuPanel` Boco | group items by category and show them in the menu |
| 3 | Build filtered sub-list by criteria | Required | `Inventory.lowStockItems` (model), `InventoryPanel` Boco | staff sets a stock threshold; app returns the low-stock name list |
| 4 | Save list to JSON | Required | `FileHandler` / `JsonFileHandler` Jessie, `AppController.exportLowStock` Leijie | export the low-stock list (`List<String>`) to JSON |
| 5 | Load and modify a list | Additional | `FileHandler` Jessie, `FavoritesList` (model), `FavoritesPanel` Boco, `saveFavorites` / `loadFavorites` Leijie | view favorites, save/load JSON, edit |
| 6 | Search | Additional | `Menu.search` (model), `MenuPanel` Boco | find menu items by name |
| 7 | Filter | Additional | `Menu.itemsInCategory` (model), `AppController.filterByCategory` Leijie, `MenuPanel` Boco | dropdown shows one category (or all) |
| 8 | Numeric data in a graph | Additional | `Inventory.getRevenueByCategory` (model), `AppController` Leijie, `SalesChartPanel` Boco | revenue per category; bar chart (JFreeChart) |
| 9 | Images for items | Additional | `MenuItem.imagePath` (model), panels Boco, seed paths in `MainApp` Leijie | classpath image per item (e.g. `/images/burger.png`) |

## Business flow

```mermaid
flowchart TD
    Start([Launch app]) --> Role{Choose role}
    Role -->|Customer| M[Browse menu by category]
    Role -->|Staff| Inv[View inventory]

    M --> FS[Filter by category / search by name]
    FS --> Add{Add item}
    Add -->|to cart| Cart[Cart with quantities and total]
    Cart --> Checkout[Checkout]
    Checkout --> Effect[[Decrease stock, record revenue by category]]
    Add -->|to favorites| Fav[Favorites list]
    Fav --> Save[Save favorites to JSON]
    Save -. later .-> Load[Load and modify favorites]

    Inv --> Restock[Restock items]
    Inv --> Low[Set threshold, get low stock list]
    Low --> Export[Export low stock list to JSON]
    Inv --> Chart[Sales chart, revenue by category]
```

