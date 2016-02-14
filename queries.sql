SELECT COUNT(*)
FROM User;

SELECT COUNT(*)
FROM Item
WHERE BINARY Location='New York';

SELECT COUNT(Grouped)
FROM 
(SELECT COUNT(*) AS Grouped 
	FROM Category
	GROUP BY ItemId) AS T1
WHERE Grouped=4;

SELECT ItemID
FROM Item
WHERE 
	Currently=
	(SELECT Max(Second)
	FROM 
		(SELECT ItemID AS First, Currently AS Second
		FROM Item
		WHERE Ends > "2001-12-20 00:00:01"
			AND Number_of_Bids > 0) AS T1)
	AND Ends > "2001-12-20 00:00:01"
	AND Number_of_Bids > 0;

SELECT COUNT(*)
FROM User
WHERE Seller_Rating > 1000;

SELECT COUNT(*)
FROM User
WHERE Seller_Rating > -9001
	AND Buyer_Rating > -9001;

SELECT COUNT(*)
FROM
	(SELECT *
	FROM Category
	INNER JOIN
		(SELECT ItemID AS First
		FROM Item
		WHERE Number_of_Bids > 0
			AND Currently > 100) AS T1
	ON T1.First=Category.ItemID
	GROUP BY Category) AS T2;
