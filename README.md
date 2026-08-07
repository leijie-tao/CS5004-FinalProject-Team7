# Final Project for CS 5004 - (APPLICATION NAME/Update)

(remove this and add your sections/elements)
This readme should contain the following information: 

* The group member's names and github accounts
* The application name and a brief description of the application
* Links to design documents and manuals
* Instructions on how to run the application

Ask yourself, if you started here in the readme, would you have what you need to work on this project and/or use the application?

### Order & Inventory (Model — Yixuan Liu)

`Order` is the customer's cart. It adds items and merges duplicates into one
line, adjusts quantities, removes items by name, and computes the running total
(price × quantity). Item order is preserved, and every getter returns a fresh
copy so callers cannot mutate the cart's internals.

`Inventory` tracks stock and sales in one class — there is no separate sales
record. It sets and reads stock per item, restocks and sells while never letting
stock go negative, and builds the low-stock sub-list from a staff-chosen
threshold (the required filtered-sub-list feature). On checkout it records a
sale: the order count and total revenue rise, and revenue is accumulated per
category (MAIN, DESSERT, BEVERAGE) for the staff sales chart.

Both classes are covered by JUnit 5 unit tests (38 tests), including edge cases:
non-positive quantities, over-selling, stock reaching exactly zero, unknown
items, empty orders, threshold boundaries, and defensive copies.



### GUI Components (View - Lucille Boco)

#### *View* 
`MainFrame`
`RoleSelectionPanel`
`MenuPanel`
`OrderPanel`
`InventoryPanel`
`SalesChartPanel`
`FavoritesPanel`
`ItemTableFormat`
`ReadOnlyTableModel`
`RoleSelectionListener`
`TabbedRolePanel`

#### *Tests for View* 
`MockController`
`MockFavoritesList`
`MockFavoritesPanelDemo`
`MockMainFrameDemo`
`MockMenuPanelDemo`
`MockSharedControllerDemo`
`MockOrderPanelDemo`
`FavoritesPanelTest`
`ItemTableFormatTest`
`MainFrameTest`
`MenuPanelTest`
`RoleSelectionPanelTest`
`TabbedRolePanelTest`
`OrderPanelTest`
`InventoryPanelTest`
`SalesChartPanelTest`

### *Deliverable - Manual Tests*

