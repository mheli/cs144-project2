# README #

### What is this repository for? ###

The goal of this project is to transform XML data into MySQL load files. The XML data is a snapshot of auctions on Ebay.

### How do I get set up? ###

runLoad.sh will create the tables in the pre-existing CS144 mysql database, use ant to transform the ebay data located at $EBAY_DATA, and then load the generated mysql load files into the tables.

### Other Info ###
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