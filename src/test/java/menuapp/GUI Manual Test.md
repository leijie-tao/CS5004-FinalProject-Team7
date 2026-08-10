## Main Frame

- Opening role screen. Is there a switch button visible? 
- Select the customer role. What do you see? 
- What is rendered on the menu tab? 
- As a customer add something to your cart 
- As a customer add something to your favorite 
- If you switch role to Staff, is there a button that allows you to do that? 
- If there is, and you clicked it--what happens? 


## Order Panel 
- Was the cart empty with a 0.0 subtotal and checkout disabled? 
- If we add one item, what happens? (use MockOrderPanelDemo with stale info) vs (use MockMainFrame)
- If the MockMainFrame (version w/ no stale and refresh) works with the cart check if 
  - Can we add item using the - or + signs? 
  - If we can add/remove does the subtotal updates itself? If it does, is it 2 decimals (restoreSelection)? 
  - If we press - all the way to 1 and try to press once more what visibly happens next? 
- If we try to place an order for an item that is out of stock, happens? 
- What happens if we try to add Pečená kachna? 