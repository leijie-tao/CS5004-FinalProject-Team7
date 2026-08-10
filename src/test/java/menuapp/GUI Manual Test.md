# Manual GUI Test Documentation — View Layer (Author: L.Boco)

## Why this document exists

The 131 JUnit tests in this repository only test the methods that take model data and turn it into text without 
using any user interface components. Because of this, parts of the program that need to be seen or clicked on 
are not tested automatically. This includes opening windows, moving between screens with `CardLayout`, 
switching tabs, selecting table rows, enabling and disabling buttons, showing `JOptionPane` messages, 
using `JFileChooser`, and replacing parts of a table with an empty state.

Every case in this document runs against `MockController`, not against the finished application.
`MainApp.main` is empty and `AppController` still throws `UnsupportedOperationException` from 17 of its
18 methods (pre-integration), so the application cannot currently launch. The panels never knew which controller they
were handed, which is the whole point of the fake controller (`MockController`). However, this entire suite reruns
unchanged once `MainApp` is wired. Nothing here has to be rewritten at integration time, although some adjustments
might need to be made.

For transparency, these features need to be checked manually by running the program and looking at the screen. This
is because `MockController` implements all 18 methods, the `showNotReady` branch inherited from `AppPanel` is 
unreachable in this repository outside of the view specific demos. This document helps explains those manual tests
to cover areas not checked by the automated JUnit tests. 


## Demo entry points

Each demo constructs a fresh `MockController` so that the conditions below are reproducible.

| Entry point | Opens |
|---|---|
| `MockMainFrameDemo` | The real `MainFrame` with every card and tab wired |
| `MockSharedControllerDemo` | `MenuPanel` and `FavoritesPanel` behind one controller |
| `MockMenuPanelDemo` | `MenuPanel` alone |
| `MockOrderPanelDemo` | `MenuPanel` and `OrderPanel` behind one controller |
| `MockFavoritesPanelDemo` | `FavoritesPanel` alone |

## Seeded failure triggers

`MockController` seeds five reproducible "triggers" that raises the error flags. Without
these, the failure branches would only be observable by editing source between test runs.

| Trigger | Effect |
|---|---|
| Add `Pečená kachna` to cart | `addToCart` throws, error dialog opens |
| Any file path containing `bad` | Save, load, or export throws, error dialog opens |
| `Durian Ice Cream`, seeded at stock 2 | Raise to quantity 3, checkout refuses |
| Search a nonsense string | Empty state swaps in for the table |
| Select `Alžírská káva` against `Piñonates` | Image present against `No picture for …` caption |

## Run log

A pass or fail column with no run context cannot be checked by anyone else.

| Field | Value |
|---|---|
| Tester | |
| Date | |
| Commit | |
| JDK | |
| OS | |

---

# 1. `MainFrame`

`MainFrame` is the only class here that is a window rather than a screen, so what is under test is the
containment tree and the navigation contract, not any model data. `MainFrame` holds exactly one private
`showCard(String)` method, and that method calls `cardLayout.show` and then `refresh()` on the panel it
just made visible. Nothing else in the codebase calls `cardLayout.show`. That rule exists because
`CardLayout` gives a panel no notification that it became visible, so a card shown without a following
`refresh()` would render whatever it held the last time it was looked at. The switch role button is also owned 
by `MainFrame`. It is hidden rather than disabled on the role screen.

**Preconditions for this section:** `MockMainFrameDemo` is running.

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| MF-01 | 1. Launch `MockMainFrameDemo`. | Window opens centred, 900 by 600, title bar reads `NEU Café Menu`. Role screen is showing. | |
| MF-02 | 1. Observe the bottom strip on the role screen. | No switch role button is visible. The strip collapses rather than showing a disabled button. | |
| MF-03 | 1. Click `I'm a Customer`. | Tabbed screen appears with three tabs, in order: `🍴 Menu`, `🛒 Cart`, `♥ Favorites`. Menu tab is selected and its table is populated. | |
| MF-04 | 1. From the customer screen, observe the bottom strip. | `Switch role` button is now visible. | |
| MF-05 | 1. Click `Switch role`. | Role screen returns. Switch role button disappears again. | |
| MF-06 | 1. Click `I'm a Staff`. | Tabbed screen appears with two tabs, in order: `✅ Inventory`, `📈 Sales`. Inventory tab is selected and both its tables are populated. | |
| MF-07 | 1. As Customer, Menu tab, select any row and add it to the cart. 2. Click `Switch role`. 3. Click `I'm a Customer`. 4. Open the Cart tab. | The item is still in the cart. Returning to the role screen does not discard model state, because the panels hold no state to discard. | |
| MF-08 | 1. As Staff, note the stock figure for any item. 2. Switch to Customer, add that item to the cart, check out. 3. Switch back to Staff, Inventory tab. | The stock figure has dropped by the quantity ordered. This proves `showCard` refreshed the panel rather than showing a stale card. | |
| MF-09 | 1. Close the window with the title bar close control. | The process exits. No stray window is left behind. | |

---

# 2. `RoleSelectionPanel`

`RoleSelectionPanel` is the first screen shown by `MainFrame`. It displays a heading and creates one button for each 
role in `Role.values()`. This means that if a new role is added to the `Role` enum later, a button for it will 
automatically appear without needing to change this panel. This panel does not decide which screen to open after a 
role is selected. Instead, it sends the selected role through `RoleSelectionListener`. `MainFrame` then decides 
which screen should be shown. The `refresh()` method is empty because this screen does not display any model 
data that needs to be updated. The method is still included because `MainFrame` calls `refresh()` whenever 
it shows a panel. Test case MF-05 checks that the empty `refresh()` method does not cause any problems.

**Preconditions for this section:** `MockMainFrameDemo` is running and the role selection screen is displayed.

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| RS-01 | 1. Read the heading. | Reads `Welcome! Select your role:`, bold, centred at the top of the panel. | |
| RS-02 | 1. Count the buttons and read their labels. | Exactly two, reading `I'm a Customer` and `I'm a Staff`, in `Role` declaration order. Both are formatted by `ItemTableFormat.formatEnumName`, so `CUSTOMER` renders as `Customer` and not as `CUSTOMER`. | |
| RS-03 | 1. Return to the role screen using `Switch role` several times. | The heading and both buttons render identically every time. Repeated empty `refresh()` calls change nothing on screen. | |

---

# 3. `TabbedRolePanel`

`TabbedRolePanel` extends `AppPanel` and holds a `JTabbedPane` by composition. From the outside it
behaves as a single screen, which is why `MainFrame` can register it as one card. Its `refresh()`
redraws only the tab the user is actually looking at, and its change listener redraws a tab at the
moment it is selected. A hidden tab is left alone deliberately, because refreshing a screen nobody can
see costs a controller round trip and is discarded on the next selection anyway.

**Preconditions for this section:** `MockMainFrameDemo` is running, Customer role selected.

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| TR-01 | 1. On the Menu tab, select an item and add it to the cart. 2. Click the Cart tab. | The cart already shows the item. The tab was refreshed on selection, not on construction. | |
| TR-02 | 1. On the Menu tab, add a second item to the cart. 2. Click Cart, then click Menu, then click Cart again. | The cart shows both items and the header count is correct. Repeated tab switching produces no duplicate rows and no doubled quantities. | |
| TR-03 | 1. Add an item to favorites from the Menu tab. 2. Click the Favorites tab. | The item appears in the favorites table and the header count has risen by one. | |
| TR-04 | 1. Select the Cart tab. 2. Click `Switch role`, then `I'm a Customer`. | The Cart tab is still the selected tab. Returning to the card does not reset the tab selection, and the tab that is showing is refreshed. | |

---

# 4. `MenuPanel`

`MenuPanel` carries most of the branches. Three controller methods sit behind
one table: `getGroupedMenu` when nothing is filtered, `filterByCategory` when only a category is
chosen, and `search` when a keyword is typed. `refresh()` reads the keyword and the category out of the
widgets rather than out of fields, so there is only one copy of the filter state and the controls can
never disagree with the table.

The image preview to the east of the table is the newest feature. Three of the fifteen seeded items
carry an image path and the other twelve are seeded null, which puts both branches of `previewTextFor`
on one screen without any setup. An item with a picture gets no caption, because the picture is the
message.

**Preconditions for this section:** `MockMenuPanelDemo` is running unless a case says otherwise.

## 4.1 Default rendering

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| MN-01 | 1. Launch `MockMenuPanelDemo`. | Table shows 15 rows under headers `Item`, `Category`, `Price`. Title reads `☕ Our Menu`. Status line reads `15 items`. | |
| MN-02 | 1. Read the table top to bottom. | Rows appear grouped by category in enum declaration order: five `Main`, then five `Dessert`, then five `Beverage`. Sections are never shuffled between launches, because `flattenGrouped` walks `Category.values()` rather than the map's own entry order. | |
| MN-03 | 1. Read the price column. | Every price shows two decimals with a dot separator, for example `$33.00` and `$6.60`. `Štrúdl` is seeded at 6.601 and displays as `$6.60`. | |
| MN-04 | 1. Read the accented item names: `Côte de bœuf`, `Pečená kachna`, `Bún Thịt Nướng`, `Piñonates`, `Cà Phê Sữa Đá`. | Every accent, diacritic, and ligature renders correctly. No boxes, question marks, or mangled characters. | |
| MN-05 | 1. Double-click any table cell. 2. Type. | Nothing happens. The cell does not enter edit mode, because `ReadOnlyTableModel.isCellEditable` returns false and all changes are owned by the controller. | |
| MN-06 | 1. Drag a column header sideways. | Columns do not reorder. Header reordering is disabled so the name column stays at index 0, which is the column selection is keyed on. | |
| MN-07 | 1. Observe the two action buttons before selecting anything. | `+ Add to cart` and `+ Add to favorites` are both disabled. | |
| MN-08 | 1. Observe the preview area before selecting anything. | Caption reads `Select an item to preview it` and no image is shown. | |

## 4.2 Category filter

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| MN-09 | 1. Open the category dropdown. | Four entries, in order: `All categories`, `Main`, `Dessert`, `Beverage`. Category labels match the text in the table's own category column exactly. | |
| MN-10 | 1. Select `Dessert`. | Table shows 5 rows, all with category `Dessert`. Status line reads `5 items in Dessert`. | |
| MN-11 | 1. Select `Main`, then `Beverage`, then `All categories`. | Each selection redraws the table immediately without any further click. Returning to `All categories` restores all 15 rows and the status reads `15 items`. | |

## 4.3 Search

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| MN-12 | 1. Type `caf` into the search field. 2. Click `Search`. | Table narrows to the matching items. Status line reads `2 matches for "caf"`. The noun is `matches`, not `items`, because a search is active. | |
| MN-13 | 1. Clear the field. 2. Type `Yuenyeung`. 3. Press Enter inside the field rather than clicking the button. | Same result as clicking `Search`. Status reads `1 match for "Yuenyeung"`, singular. Enter and the button run the same path. | |
| MN-14 | 1. Type `CAFÉ` in capitals. | The same rows match as for `café`. Search ignores case. | |
| MN-15 | 1. Type `zzz`. 2. Press Enter. | The table is replaced by a centred label reading `No match, please try another word.` Status reads `0 matches for "zzz"`. Both action buttons are disabled. Preview caption returns to `Select an item to preview it`. | |
| MN-16 | 1. From the state in MN-15, click `Show all`. | The table returns with all 15 rows, the search field is empty, and the dropdown reads `All categories`. Status reads `15 items`. | |
| MN-17 | 1. Type three spaces into the search field. 2. Press Enter. | All 15 rows still show and the status reads `15 items`, not `0 matches`. Whitespace does not count as a search. | |
| MN-18 | 1. Select `Dessert` in the dropdown. 2. Type `caf` and press Enter. | Result is the intersection: only dessert items matching `caf`, which is none. Empty state shows and status reads `0 matches for "caf" in Dessert`. Search runs across the whole menu and the category then narrows it. | |

## 4.4 Selection, images, and actions

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| MN-19 | 1. Click any row. | Both action buttons become enabled. | |
| MN-20 | 1. Select `Alžírská káva`. | A scaled image appears in the preview area to the right of the table. No caption text is shown beneath it. | |
| MN-21 | 1. Select `Profiteroles`, then `Cumin Lamb Biang-Biang Noodles`. | Each shows its own distinct image. The preview changes on every selection change, not only on the first. | |
| MN-22 | 1. Select `Piñonates`. | No image. Caption reads `No picture for Piñonates`. | |
| MN-23 | 1. Select an item with an image, then an item without. | The previous image is cleared rather than left behind under the new caption. | |
| MN-24 | 1. Select `Côte de bœuf`. 2. Click `+ Add to cart`. | Status line changes to `Added Côte de bœuf to the cart`. The table is unaffected. | |
| MN-25 | 1. Select `Štrúdl`. 2. Click `+ Add to favorites`. | Status line changes to `Added Štrúdl to favorites`. | |
| MN-26 | 1. Select `Pečená kachna`. 2. Click `+ Add to cart`. | An error dialog opens, titled `Menu`, reading `Sorry, could not add that item to the cart.` followed by `Mock refusal: Pečená kachna is unavailable`. | |
| MN-27 | 1. Dismiss the dialog from MN-26. | The status line does **not** read `Added Pečená kachna to the cart`. It holds whatever it said before. The screen never claims an add that did not happen. | |
| MN-28 | 1. Run `MockSharedControllerDemo`. 2. On the Menu tab, add `Piñonates` to favorites. 3. Switch to the Favorites tab. | `Piñonates` is in the favorites table. Two panels behind one controller stay in step. | |

---

# 5. `OrderPanel`

`OrderPanel` gets the latest cart information from the controller every time the screen is refreshed. 
It does not save its own copy of the cart, so the table always shows what is currently stored in the model. The quantity
is changed using buttons instead of editing the table directly. The table is read-only, which helps keep changes going 
through the controller instead of allowing the View to change the model itself. There are also two important behaviors 
that should be tested. `shouldRemoveOnDecrease` removes an item when its quantity reaches one instead of setting 
the quantity to zero, because `Order.setQuantity` does not allow zero or negative values.
`clampSelection` keeps a row selected after the table is refreshed. Since the table is rebuilt each time 
the screen updates, the selected row would otherwise be lost whenever the user presses a button.

**Preconditions for this section:** `MockOrderPanelDemo` is running with an empty cart, unless a case
says otherwise.

## 5.1 Empty cart

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| OR-01 | 1. Launch `MockOrderPanelDemo`. 2. Open the Cart tab. | No table. A centred label reads `Your cart is empty. ☺️ Add something from our menu for checkout!` Header reads `Cart (0 items)`. Total reads `Total: $0.00`. | |
| OR-02 | 1. Observe the four buttons on an empty cart. | `−`, `+`, and `Remove` are disabled because nothing is selected. `Checkout` is disabled because the table holds no rows. | |

## 5.2 Building a cart

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| OR-03 | 1. On the Menu tab, add `Côte de bœuf`. 2. Open the Cart tab. | The empty label is gone and a table shows one row under headers `Item`, `Price`, `Qty`, `Subtotal`. Header reads `Cart (1 item)`, singular. Total reads `Total: $33.00`. | |
| OR-04 | 1. Add `Café au lait` from the Menu tab. 2. Return to the Cart tab. | Two rows. Header reads `Cart (2 items)`. Total reads `Total: $36.50`. | |
| OR-05 | 1. Add `Côte de bœuf` a second time. 2. Return to the Cart tab. | Still two rows, not three. The `Côte de bœuf` line shows `Qty 2` and its subtotal is `$66.00`. Duplicate adds merge into one line. | |
| OR-06 | 1. Read the row order after OR-05. | Rows are in the order the items were first added, `Côte de bœuf` then `Café au lait`. Adding to an existing line does not move it. | |
| OR-07 | 1. Select the first row. | `−`, `+`, and `Remove` all become enabled. | |

## 5.3 Quantity, selection, and totals

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| OR-08 | 1. With a row selected, click `+`. | Qty rises by one, subtotal recalculates, total recalculates, and the same row is **still selected**. The selection surviving the rebuild is what `clampSelection` exists for. | |
| OR-09 | 1. Empty the cart by relaunching. 2. Add `Štrúdl`, seeded at 6.601. 3. Open the Cart tab and click `+` nine times. | Price column reads `$6.60`. Qty reads `10`. Subtotal reads `$66.01`, **not** `$66.00`. Total reads `Total: $66.01`. The subtotal multiplies the unrounded price and formats the product, rather than multiplying the rounded display text. | |
| OR-10 | 1. Click `−` until the quantity reaches 1. | Quantity falls one step per click. The row remains. Total tracks each step. | |
| OR-11 | 1. At quantity 1, click `−` once more. | The row is removed from the table. No exception, no error dialog. `Order.setQuantity` is never called with zero. | |
| OR-12 | 1. Remove the last remaining row with `−`. | The table is replaced by the empty state label, the header returns to `Cart (0 items)`, the total returns to `Total: $0.00`, and all four buttons are disabled again. | |
| OR-13 | 1. Build a two-row cart. 2. Select the **last** row. 3. Click `Remove`. | The row is gone and the selection lands on the new last row rather than being cleared. `clampSelection` falls back to the last available row when the previous index no longer exists. | |

## 5.4 Checkout

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| OR-14 | 1. With a non-empty cart, click `Checkout`. | A confirmation dialog opens, titled `Checkout`, asking `Place this order for Total: $NN.NN?` where the figure matches the total on screen. | |
| OR-15 | 1. Click `No` on that dialog. | The dialog closes. The cart is unchanged. Nothing was sent to the controller. | |
| OR-16 | 1. Click `Checkout` again. 2. Click `Yes`. | The cart empties. The table is replaced by the empty state label, header reads `Cart (0 items)`, total reads `Total: $0.00`, and `Checkout` is disabled again. | |
| OR-17 | 1. Relaunch. 2. Add `Durian Ice Cream`, seeded at stock 2. 3. On the Cart tab, click `+` twice to reach quantity 3. 4. Click `Checkout`, then `Yes`. | An error dialog opens, titled `Cart`, reading `😔 Sorry order can't be placed.` followed by `Mock refusal: only 2 of Durian Ice Cream left in stock`. | |
| OR-18 | 1. Dismiss the dialog from OR-17. | The cart is **unchanged**: one row, quantity 3, total `$10.50`. `Checkout` is still enabled. A refused checkout leaves the cart intact so the user can adjust it. | |
| OR-19 | 1. From OR-18, click `−` once to reach quantity 2. 2. Checkout and confirm. | The checkout succeeds and the cart empties. The refusal was about stock, not about the cart being unrecoverable. | |

---

# 6. `InventoryPanel`

`InventoryPanel` displays two tables next to each other. The table on the left shows all items in stock and is the 
only table where the user can select an item to restock. The table on the right shows only the items 
that are low in stock. Row selection is turned off for this table so there is no confusion about which item is being restocked.
The threshold only changes when the user presses `Apply`. The value stored in `activeThreshold` is used for the low-stock table, 
the header, and `exportLowStock`. This keeps all three using the same threshold, even if the user has typed a different value into the text field but has not applied it yet.
If the user enters an invalid threshold, the panel stops the input before it is sent to the controller. A warning message is shown because the action was not performed, rather than treating it as a program error.

**Preconditions for this section:** `MockMainFrameDemo` is running, Staff role, Inventory tab.

## 6.1 Default rendering

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| IN-01 | 1. Open the Inventory tab. | Two tables side by side, each under headers `Item`, `In stock`. Header reads `Inventory (15 items)`. | |
| IN-02 | 1. Read the left table. | 15 rows, sorted alphabetically by item name. Every item shows `20` except `Durian Ice Cream`, which shows `2`. | |
| IN-03 | 1. Read the right side. | Threshold field holds `5`. Sub-list header reads `Low stock at or below 5 (1 item)`, singular. The sub-list holds exactly one row, `Durian Ice Cream` at `2`. | |
| IN-04 | 1. Try to click a row in the right table. | No row highlights. The sub-list is display only. | |
| IN-05 | 1. Observe the buttons before selecting anything. | `Restock selected item` is disabled. `Export sub-list…` is enabled, because the sub-list is not empty. | |

## 6.2 Threshold

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| IN-06 | 1. Type `20` into the threshold field. 2. Click `Apply`. | Sub-list header reads `Low stock at or below 20 (15 items)` and the sub-list holds all 15 rows. The comparison is at or below, so items sitting exactly on the threshold are included. | |
| IN-07 | 1. Type `0`. 2. Click `Apply`. | Header reads `Low stock at or below 0 (0 items)`. The sub-list is empty and `Export sub-list…` becomes disabled. Zero is a legal threshold and means "what is completely out". | |
| IN-08 | 1. Type `5`. 2. Press Enter inside the field rather than clicking `Apply`. | Same result as clicking `Apply`. Enter and the button run the same handler. | |
| IN-09 | 1. Type `-1`. 2. Click `Apply`. | A warning dialog opens, titled `Inventory`, reading `Threshold must be a whole number of at least 0. Got: "-1".` The sub-list is unchanged behind the dialog. | |
| IN-10 | 1. Type `abc`. 2. Click `Apply`. | Same warning, quoting `"abc"`. | |
| IN-11 | 1. Clear the field entirely. 2. Click `Apply`. | Warning reads `Threshold must be a whole number of at least 0.` with no `Got:` clause, because there is nothing to quote. | |
| IN-12 | 1. Type `2` but do **not** press Apply. 2. Read the sub-list header. | The header still names the previously applied threshold, not `2`. Typing does not commit. | |

## 6.3 Restock

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| IN-13 | 1. Select `Durian Ice Cream` in the left table. | `Restock selected item` becomes enabled. | |
| IN-14 | 1. With threshold at `5` and `Durian Ice Cream` selected, type `10` into the restock field. 2. Click `Restock selected item`. | The left table shows `Durian Ice Cream` at `12`. The sub-list becomes empty and its header reads `Low stock at or below 5 (0 items)`. `Export sub-list…` becomes disabled. The restock field is cleared. | |
| IN-15 | 1. Select any row. 2. Type `3`. 3. Press Enter in the restock field. | Same as clicking the button. | |
| IN-16 | 1. Select a row. 2. Type `0`. 3. Click `Restock selected item`. | Warning dialog reads `Restock amount must be a whole number of at least 1. Got: "0".` The stock figure does not change. | |
| IN-17 | 1. Select a row. 2. Type `2.5`. 3. Click `Restock selected item`. | Same warning, quoting `"2.5"`. A decimal is not a whole number. | |
| IN-18 | 1. Select a row near the bottom of the table. 2. Restock it by `1`. | The same row is still selected after the table rebuilds. | |

## 6.4 Export

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| IN-19 | 1. Set the threshold to `5` and apply. 2. Click `Export sub-list…`. | A save dialog opens, titled `Export low stock list`, with the filename pre-filled as `low-stock-5.json`. The name carries the applied threshold. | |
| IN-20 | 1. Cancel that dialog. | Nothing happens. No error, no redraw needed. | |
| IN-21 | 1. Click `Export sub-list…`. 2. Save as `mylist` with no extension. | No error dialog. The path is silently given a `.json` extension, because the export must be JSON. | |
| IN-22 | 1. Click `Export sub-list…`. 2. Save as `bad.json`. | An error dialog opens, titled `Inventory`, reading `Sorry, could not export the low stock list.` followed by `Mock write failure for …` and the path. | |
| IN-23 | 1. Set the threshold to `20` and apply. 2. Export. | The pre-filled name is now `low-stock-20.json`. The exported threshold follows what the screen is showing, not what was last typed. | |

## 6.5 Cross-panel effect

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| IN-24 | 1. Note `Café au lait` stock on the Inventory tab. 2. Switch to Customer, add two `Café au lait`, check out. 3. Switch back to Staff, Inventory tab. | Stock has fallen by exactly 2. The panel re-read the inventory rather than showing the figure it rendered before. | |

---

# 7. `SalesChartPanel`

`SalesChartPanel` is display only. It reads `getRevenueByCategory`, updates a dataset in place rather
than rebuilding it, and draws a `ChartPanel` through JFreeChart. Every category is written into the
dataset on every redraw, even a category with no revenue, so the chart always shows the same set of
bars and does not change shape as sales come in.

Before any sale exists the chart is swapped for an empty state label. A bar chart of three flat zero
bars tells the reader nothing and looks like a rendering bug rather than an accurate report of zero.

**Sequencing note.** SC-03 onward cannot be run from a fresh launch, because there is no revenue to
draw until a checkout has completed. Run at least one successful checkout first, meaning OR-16 or
OR-19 in the same session, before starting SC-03.

**Preconditions for this section:** `MockMainFrameDemo` is running, Staff role, Sales tab.

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| SC-01 | 1. On a freshly launched demo, open the Sales tab. | No chart. A centred label reads `No sales yet. Check out an order to see revenue here!` Header reads `Revenue by category (total $0.00)`. | |
| SC-02 | 1. Switch away to Inventory and back to Sales, twice. | The empty state still shows and does not flicker into an empty chart in between. | |
| SC-03 | 1. Switch to Customer, add `Štrúdl` and raise it to quantity 10, check out. 2. Switch to Staff, Sales tab. | The empty label is replaced by a bar chart. Header reads `Revenue by category (total $66.01)`. | |
| SC-04 | 1. Read the chart. | Chart title reads `Revenue by category`. Horizontal axis is labelled `Category`, vertical axis `Revenue ($)`. Three bars are present, labelled `Main`, `Dessert`, `Beverage`, in enum declaration order. | |
| SC-05 | 1. Read the bar heights after SC-03. | Only the `Dessert` bar has height. `Main` and `Beverage` sit at zero but are still drawn and still labelled. | |
| SC-06 | 1. Switch to Customer, add one `Côte de bœuf` at `$33.00`, check out. 2. Return to the Sales tab. | The `Main` bar now has height. Header reads `Revenue by category (total $99.01)`. Revenue accumulates across checkouts rather than replacing. | |
| SC-07 | 1. Resize the window with the Sales tab showing. | The chart scales with the panel and stays legible. Axis labels are not clipped. | |

---

# 8. `FavoritesPanel`

`FavoritesPanel` owns no domain state. It holds no copy of the items, computes no total, and touches no
file directly. Every button calls the controller and then calls `refresh()`, and `refresh()` re-reads
the list from the controller. FV-11 is the case that proves this, since loading a file swaps in a
visibly different list under a different name, which a cached panel could not show.

Favorites deliberately have no path into the cart.

**Preconditions for this section:** `MockFavoritesPanelDemo` is running, unless a case says otherwise.

| ID | Steps | Expected result | Pass / Fail |
|---|---|---|---|
| FV-01 | 1. Launch `MockFavoritesPanelDemo`. | Table shows 4 rows under headers `Item`, `Category`, `Price`. Header reads `My Favorites (4 items)`. | |
| FV-02 | 1. Read the rows. | `Côte de bœuf`, `Cazuela de castañas`, `Štrúdl`, `Cortado con canela`, with their categories and prices formatted the same way as on `MenuPanel`. | |
| FV-03 | 1. Observe the four buttons before selecting anything. | `Remove Item` is disabled. `Rename List`, `Save Favorites`, and `Load Favorites` are enabled. | |
| FV-04 | 1. Select a row. | `Remove Item` becomes enabled. | |
| FV-05 | 1. Select `Štrúdl`. 2. Click `Remove Item`. | The row is gone. Header reads `My Favorites (3 items)`. | |
| FV-06 | 1. Remove all remaining rows one at a time. | After the last removal the table is replaced by a centred label reading `You have no favorites yet! Add items to see them here.` Header reads `My Favorites (0 items)`. `Remove Item` and `Save Favorites` are both disabled. | |
| FV-07 | 1. Relaunch. 2. Click `Rename List`. | An input dialog opens reading `Name for this list:` with `My Favorites` pre-filled. | |
| FV-08 | 1. Type `Weeknight Picks`. 2. Click OK. | Header reads `Weeknight Picks (4 items)`. The table contents are unchanged. | |
| FV-09 | 1. Click `Rename List`. 2. Click Cancel. | The name is unchanged. | |
| FV-10 | 1. Click `Rename List`. 2. Clear the field and click OK. | The name is unchanged. A blank name is rejected the same as a cancel. | |
| FV-11 | 1. Click `Load Favorites`. 2. Choose any file. | The list is replaced. Header reads `Weekend Picks (3 items)` and the rows are `Côte de bœuf` at `$31.00`, `Profiteroles` at `$7.50`, `Café au lait` at `$3.00`. Note the different price on `Côte de bœuf`, which proves the panel re-read from the controller rather than redrawing a cached list. | |
| FV-12 | 1. Click `Save Favorites`. | A save dialog opens, titled `Save favorites`, with the filename pre-filled from the current list name plus `.json`. | |
| FV-13 | 1. Cancel that dialog. | Nothing happens and the list is unchanged. | |
| FV-14 | 1. Click `Save Favorites`. 2. Save as `bad.json`. | An error dialog opens, titled `Favorites`, reading `Could not save the list.` followed by `Mock write failure for …` and the path. | |
| FV-15 | 1. Dismiss the dialog. 2. Read the table. | The list is unchanged. A failed save does not damage what is on screen. | |
| FV-16 | 1. Click `Load Favorites`. 2. Choose a file whose path contains `bad`. | An error dialog reads `Could not load that file.` followed by `Mock read failure for …`. The previous list is still showing behind it. | |
| FV-17 | 1. Run `MockSharedControllerDemo`. 2. Add three items to favorites from the Menu tab. 3. Switch to the Favorites tab. | All three appear and the header count has risen by three. | |

---

# 9. Known gaps

These are recorded rather than omitted for references. 

| Gap | Why it is not covered |
|---|---|
| `AppPanel.showNotReady` | `MockController` implements all 18 controller methods, so no demo entry point can reach this branch. It becomes reachable, and testable, the moment a real `AppController` with unimplemented methods is passed to `MainFrame`. |
| `InventoryPanel` empty state, `Nothing is stocked yet.` | `mockSeedStock` gives every catalog item a stock entry, so the stock map is never empty. Reachable only against a real `Inventory` with nothing set. |
| `MenuPanel` and `FavoritesPanel` behaviour under a genuinely failing file system | The `bad` path convention throws before touching the disk by design, so the dialogs are tested but real IO failures such as permissions or a full disk are not. |
| JFreeChart rendering correctness | SC-04 through SC-06 check that the right numbers reach the chart and that it draws. Whether JFreeChart draws a bar correctly is that library's concern, not this project's. |