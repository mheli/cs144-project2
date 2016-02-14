1. Relational schema design:
Item(ItemID, Name, Currently, Buy_Price, First_Bid, Number_of_Bids, Location, Latitude, Longitude, Country, Started, Ends, SellerID, Description)
Key: ItemID
Foreign Key: SellerID references User.UserID

Category(ItemID, Category)
Key: ItemID, Category
Foreign Key: ItemID references Item.ItemID

Bid(ItemID, BidderID, Time, Amount)
Key: ItemID, BidderID, Time
Foreign Key: ItemID references Item.ItemID
Foreign Key: BidderID references User.UserID

2. All nontrivial functional dependencies are keys.

3. All the relations are in BCNF.

4. All the relations are in 4NF.
