package de.akad.dml93;

import com.opencsv.CSVReader;
import java.io.StringReader;

public class Helper {
    public static String[] parseCSV(String line) throws InterruptedException{
        String[]  columns = line.split(",");
        // Skipping csv header
        if (columns[0].equals("data_source")) return null;

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

    public static float parsePrice(String priceString) {
        priceString = priceString.replaceAll(",","");
        priceString = priceString.replaceAll("\\$","");

        float price = 0;
        if(priceString.contains("-")) {
            String[] prices = priceString.split(" - ");
            price = (Float.parseFloat(prices[1]) + Float.parseFloat(prices[0]))/2;
        }
        else if(priceString.length() == 0){
            price = 0;
        }
        else {
            price = Float.parseFloat(priceString);
        }
        return price;
    }
}
