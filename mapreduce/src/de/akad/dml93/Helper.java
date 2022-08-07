package de.akad.dml93;

import com.opencsv.CSVReader;
import java.io.StringReader;

public class Helper {
    // parseCSV parses a single csv row and returns the columns as a String array
    public static String[] parseCSV(String line) throws InterruptedException{
        String[] columns = line.split(",");
        // skipping csv header
        if (columns[0].equals("data_source")) return null;

        // create new csv reader to read input line
        CSVReader csvReader = new CSVReader(new StringReader(line));
        try {
            columns = csvReader.readNext();
            csvReader.close();
        } catch (Exception e) {
            System.out.println(e);
            throw new InterruptedException();
        }
        return columns;
    }

    // parseFloat converts a String to a float
    // returns 0 if input String is empty
    public static float parseFloat(String input){
        if(input.isEmpty()) return 0;
        return Float.parseFloat(input);
    }

    // parsePrice converts price value to a float
    public static float parsePrice(String priceString) {
        // removes commas and $ signs from String
        priceString = priceString.replaceAll(",","");
        priceString = priceString.replaceAll("\\$","");

        float price = 0;
        if(priceString.contains("-")) {
            // if listPrice or dealPrice contains a dash, price range is given;
            // then the average of the upper and lower price is calculated
            String[] prices = priceString.split(" - ");
            price = (Float.parseFloat(prices[1]) + Float.parseFloat(prices[0]))/2;
        }
        else {
            // if no price range is given, the string will be just parsed
            price = Helper.parseFloat(priceString);
        }
        return price;
    }
}
