/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.StringBuilder;
import java.io.PrintWriter;



class MyParser {
    
    static final String columnSeparator = " |*| ";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }

    // key: itemID -> Item object
    public static class Item {
        String name;
        String currently;
        String buyPrice;
        String firstBid;
        String numberOfBids;
        String location;
        String latitude;
        String longitude;
        String country;
        String started;
        String ends;
        String sellerUserID;
        String description;

        Item(String n, String c, String bp, String fb, String nob, String l,
            String lat, String lon, String coun, String start,
            String end, String seller, String desc){
            name = n;
            currently = c;
            buyPrice = bp;
            firstBid = fb;
            numberOfBids = nob;
            location = l;
            latitude = lat;
            longitude = lon;
            country = coun;
            started = start;
            ends = end;
            sellerUserID = seller;
            description = desc;
        }

        void print(){
            System.out.println("Name: " + name);
            System.out.println("Currently: " + currently);
            System.out.println("Buy Price: " + buyPrice);
            System.out.println("First Bid: " + firstBid);
            System.out.println("Number of Bids: " + numberOfBids);
            System.out.println("Location: " + location);
            System.out.println("Latitude: " + latitude);
            System.out.println("Longitude: " + longitude);
            System.out.println("Country: " + country);
            System.out.println("Started: " + started);
            System.out.println("Ends: " + ends);
            System.out.println("Seller UID: " + sellerUserID);
            System.out.println("Description: " + description);
        }
    }
    static Map<String, Item> itemMap = new HashMap<String, Item>();

    // key: itemID -> list of categories
    static Map<String, List<String> > categoryMap = new HashMap<String, List<String> >();

    // key: itemID -> list of Bid objects
    public static class Bid {
        String bidderUID;
        String time;
        String amount;

        Bid(String b, String t, String a){
            bidderUID = b;
            time = t;
            amount = a;
        }

        void print(){
            System.out.println("Bidder UID: " + bidderUID);
            System.out.println("Bid time: " + time);
            System.out.println("Bid amount: " + amount);
        }
    }
    static Map<String, List<Bid> > bidMap = new HashMap<String, List<Bid> >();

    // key: userID -> User object
    public static class User {
        String bidderRating;
        String sellerRating;
        String location;
        String country;

        User(String br, String l, String c){
            bidderRating = br;
            sellerRating = "";
            location = l;
            country = c;
        }
        User(String sr){
            bidderRating = "";
            sellerRating = sr;
            location = "";
            country = "";
        }

        void updateSeller(String sr){
            sellerRating = sr;
        }

        void updateBidder(String br, String l, String c){
            bidderRating = br;
            location = l;
            country = c;
        }

        void print(){
            System.out.println("Bidder rating: " + bidderRating);
            System.out.println("Seller Rating: " + sellerRating);
            System.out.println("Location: " + location);
            System.out.println("Country: " + country);
        }
    }
    static Map<String, User> userMap = new HashMap<String, User>();

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }

    
    /* Returns the attribute associated with the given tagName 
     * or "" if the attribute does not have a specified or default value
     */
    static String getAttributeTextByTagName(Element e, String tagName) {
        return e.getAttribute(tagName);
    }

    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with all subelements
     * of e with the given tagName. If no such subelements exist or 
     * subelements contains no text, "" is returned.
     */
    static String[] getAllElementTextByTagName(Element e, String tagName) {
        Element[] elements = getElementsByTagNameNR(e, tagName);
        Vector< String > texts = new Vector< String >();
        for (int i = 0; i < elements.length; i++){
            texts.add(getElementText(elements[i]));
        }

        String[] result = new String[texts.size()];
        texts.copyInto(result);
        return result;
    }

    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    /* Returns the string truncated to 4000 characters or less
     * if the input was less than 4000 characters
     */
    static String truncate(String text) {
        if (text.length() > 4000){
            return text.substring(0, 4000);
        }
        else
            return text;
    }

    /* Return the string time converted to MySQL TIMESTAMP format
     * Ex. "Dec-07-01 15:02:54" UTC becomes "2001-12-07 15:02:54" UTC
     */
    static String convert(String time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String format(String text) {
        if (text.equals(""))
            return "";
        else
            return text;
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String formatNumber(String text) {
        if (text.equals(""))
            return "-9001";
        else
            return text;
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String formatCoordinate(String text) {
        if (text.equals(""))
            return "-9001";
        else
            return text;
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String formatBid(Bid object) {
        StringBuilder builder = new StringBuilder();
        builder.append(format(object.bidderUID)+ columnSeparator);
        builder.append(format(object.time) + columnSeparator);
        builder.append(format(object.amount));
        return builder.toString();
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String formatUser(User object) {
        StringBuilder builder = new StringBuilder();
        builder.append(formatNumber(object.bidderRating)+ columnSeparator);
        builder.append(formatNumber(object.sellerRating) + columnSeparator);
        builder.append(format(object.location) + columnSeparator);
        builder.append(format(object.country));
        return builder.toString();
    }

    /* Return the string in a format for MySQL load
     * 
     */
    static String formatItem(Item object) {
        StringBuilder builder = new StringBuilder();
        builder.append(format(object.name)+ columnSeparator);
        builder.append(format(object.currently) + columnSeparator);
        builder.append(formatNumber(object.buyPrice) + columnSeparator);
        builder.append(format(object.firstBid) + columnSeparator);
        builder.append(format(object.numberOfBids) + columnSeparator);
        builder.append(format(object.location) + columnSeparator);
        builder.append(formatCoordinate(object.latitude) + columnSeparator);
        builder.append(formatCoordinate(object.longitude) + columnSeparator);
        builder.append(format(object.country) + columnSeparator);
        builder.append(format(object.started) + columnSeparator);
        builder.append(format(object.ends) + columnSeparator);
        builder.append(format(object.sellerUserID) + columnSeparator);
        builder.append(format(object.description));
        return builder.toString();
    }

    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */

        // Each XML data file is valid with respect to the XML DTD specified
        // in the file items.dtd.

        // Get root element "Items"
        Element root = doc.getDocumentElement();

        // Get children "Item"
        Element[] items = getElementsByTagNameNR(root, "Item");

        // Process all items (0 or more)
        for (int i = 0; i < items.length; i++){
            //int i = 0; // for testing one item
            Element current = items[i];

            Map<String, User> currentUsers = new HashMap<String, User>();

            // Seller
            Element eSeller = getElementByTagNameNR(current, "Seller");

            // Seller UID
            String sellerUserID = getAttributeTextByTagName(eSeller, "UserID");

            // Seller rating
            String sellerRating = getAttributeTextByTagName(eSeller, "Rating");

            if (userMap.containsKey(sellerUserID)){
                User old = userMap.get(sellerUserID);
                old.updateSeller(sellerRating);
                userMap.put(sellerUserID, old);
            }
            else{
                currentUsers.put(sellerUserID, new User(sellerRating));            
            }

            // ItemID (required, unique)
            String itemID = getAttributeTextByTagName(current, "ItemID");

            // Name
            String name = getElementTextByTagNameNR(current, "Name");

            // Categories (at least one)
            String[] categories = getAllElementTextByTagName(current, "Category");
            List<String> listCategories = new ArrayList<String>();
            for (int j = 0; j < categories.length; j++){
                listCategories.add(categories[j]);
            }
            categoryMap.put(itemID, listCategories);

            // current price
            String currently = getElementTextByTagNameNR(current, "Currently");
            currently = strip(currently);

            // buy_price (optional)
            String buyPrice = getElementTextByTagNameNR(current, "Buy_Price");
            buyPrice = strip(buyPrice);

            // first_bid
            String firstBid = getElementTextByTagNameNR(current, "First_Bid");
            firstBid = strip(firstBid);

            // number of bids
            String numberOfBids = getElementTextByTagNameNR(current, "Number_of_Bids");

            // bids (0 or more)
            Element eBids = getElementByTagNameNR(current, "Bids");
            Element[] bids = getElementsByTagNameNR(eBids, "Bid");
            List<Bid> listBids = new ArrayList<Bid>();
            for (int k = 0; k < bids.length; k++){
                // Bidder
                Element eBidder = getElementByTagNameNR(bids[k], "Bidder");

                // Bidder UID
                String bidderUID = getAttributeTextByTagName(eBidder, "UserID");

                // Bidder Rating
                String bidderRating = getAttributeTextByTagName(eBidder, "Rating");

                // Bidder Location (optional)
                String bidderLocation = getElementTextByTagNameNR(eBidder, "Location");

                // Bidder Country (optional)
                String bidderCountry = getElementTextByTagNameNR(eBidder, "Country");

                // Time
                String time = getElementTextByTagNameNR(bids[k], "Time");
                time = convert(time);

                // Amount
                String amount = getElementTextByTagNameNR(bids[k], "Amount");
                amount = strip(amount);

                listBids.add(new Bid(bidderUID, time, amount));
                if (currentUsers.containsKey(bidderUID)){
                    User old = currentUsers.get(bidderUID);
                    old.updateBidder(bidderRating, bidderLocation, bidderCountry);
                    currentUsers.put(bidderUID, old);
                }
                else if (userMap.containsKey(bidderUID)){
                    User old = userMap.get(bidderUID);
                    old.updateBidder(bidderRating, bidderLocation, bidderCountry);
                    userMap.put(bidderUID, old);
                }
                else{
                    currentUsers.put(bidderUID, new User(bidderRating, bidderLocation, bidderCountry));
                }
            }
            bidMap.put(itemID, listBids);

            // location
            Element eLocation = getElementByTagNameNR(current, "Location");
            String location = getElementTextByTagNameNR(current, "Location");

            // latitude (optional)
            String latitude = getAttributeTextByTagName(eLocation, "Latitude");

            // latitude (optional)
            String longitude = getAttributeTextByTagName(eLocation, "Longitude");

            // Country 
            String country = getElementTextByTagNameNR(current, "Country");

            // Started
            String started = getElementTextByTagNameNR(current, "Started");
            started = convert(started);

            // Ends
            String ends = getElementTextByTagNameNR(current, "Ends");
            ends = convert(ends);

            // Description
            String description = getElementTextByTagNameNR(current, "Description");
            description = truncate(description);

            itemMap.put(itemID, new Item(name, currently, buyPrice, firstBid, numberOfBids, location, latitude, longitude, country, started, ends, sellerUserID, description));

            for (String key : currentUsers.keySet()){
                if (userMap.containsKey(key)){
                    User old = userMap.get(key);
                    User newer = currentUsers.get(key);
                    if (newer.bidderRating == "")
                        newer.bidderRating = old.bidderRating;
                    if (newer.sellerRating == "")
                        newer.sellerRating = old.sellerRating;
                    if (newer.location == "")
                        newer.location = old.location;
                    if (newer.country == "")
                        newer.country = old.country;
                    userMap.put(key, newer);
                }
                else
                    userMap.put(key, currentUsers.get(key));
            }
        }
                
        /**************************************************************/
        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        try{
            PrintWriter outfile = new PrintWriter("user.csv", "UTF-8");
            for (String key : userMap.keySet()){
                StringBuilder builder = new StringBuilder();
                builder.append(format(key) + columnSeparator);
                builder.append(formatUser(userMap.get(key)) + "\n");
                outfile.print(builder.toString());
            }
            outfile.close();

            outfile = new PrintWriter("item.csv", "UTF-8");
            for (String key : itemMap.keySet()){
                StringBuilder builder = new StringBuilder();
                builder.append(format(key) + columnSeparator);
                builder.append(formatItem(itemMap.get(key)) + "\n");
                outfile.print(builder.toString());
            }
            outfile.close();

            outfile = new PrintWriter("category.csv", "UTF-8");
            for (String key : categoryMap.keySet()){
                StringBuilder builder = new StringBuilder();
                List<String> temp = categoryMap.get(key);
                for (String x : temp){
                    builder.append(format(key) + columnSeparator);
                    builder.append(format(x) + "\n");
                }
                outfile.print(builder.toString());
            }
            outfile.close();

            outfile = new PrintWriter("bid.csv", "UTF-8");
            for (String key : bidMap.keySet()){
                StringBuilder builder = new StringBuilder();
                List<Bid> temp = bidMap.get(key);
                for (Bid x : temp){
                    builder.append(format(key) + columnSeparator);
                    builder.append(formatBid(x) + "\n");
                }
                outfile.print(builder.toString());
            }
            outfile.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(3);
        }

    }
}
