LOAD DATA LOCAL INFILE 'user.csv' INTO TABLE User
FIELDS TERMINATED BY ' |*| ';
LOAD DATA LOCAL INFILE 'item.csv' INTO TABLE Item
FIELDS TERMINATED BY ' |*| ';
LOAD DATA LOCAL INFILE 'category.csv' INTO TABLE Category
FIELDS TERMINATED BY ' |*| ';
LOAD DATA LOCAL INFILE 'bid.csv' INTO TABLE Bid
FIELDS TERMINATED BY ' |*| ';