package de.akad.dml93;

import java.io.IOException;

// import hadoop libraries
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// main MapReduce class that contains all mapper and reducer classes
public class MapReduce {

    // MapperPriceDiff is a mapper class that calculates the difference between listPrice and dealPrice
    public static class MapperPriceDiff extends Mapper<Object, Text, Text, FloatWritable> {

        // map function will be called for every line of the csv-file
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // the Helper.parseCSV function parses the csv-row to an array of Strings
            // each element of this String array represents a column value
            String[] columns = Helper.parseCSV(value.toString());
            // columns is null if the csv header was detected; hence the header will be skipped
            if (columns == null) return;

            // convert the listPrice and dealPrice from a String value to float
            float listPrice = Helper.parsePrice(columns[5]);
            float dealPrice = Helper.parsePrice(columns[4]);

            // emit a key value pair containing the datasource as key and price difference as value
            context.write(new Text(columns[0]), new FloatWritable(listPrice - dealPrice));
        }
    }

    // MapperCount is a mapper class that takes the columnIndex configuration parameter
    // and emits for every column value the value 1
    public static class MapperCount extends Mapper<Object, Text, Text, FloatWritable> {
        private int columnIndex = 0;

        // the setup function reads the columnIndex from the job configuration and stores it in its
        // private variable columnIndex; if not configured, it assumes the index is 0
        public void setup(Context context) {
            this.columnIndex = context.getConfiguration().getInt("columnIndex", 0);
        }

        // map function will be called for every line of the csv-file
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // the Helper.parseCSV function parses the csv-row to an array of Strings
            // each element of this String array represents a column value
            String[] columns = Helper.parseCSV(value.toString());
            // columns is null if the csv header was detected; hence the header will be skipped
            if (columns == null) return;

            // emit a key value pair containing the column value of the configured columnIndex as key
            // and the value 1
            context.write(new Text(columns[columnIndex]), new FloatWritable(1));
        }
    }

    // MapperRating is a mapper class that groups the ratings into 10 bins
    // and emits for every column value and bin the value 1
    public static class MapperRating extends Mapper<Object, Text, Text, FloatWritable> {
        private int columnIndex = 0;

        // identical to the previous setup function of mapper class MapperCount
        public void setup(Context context) {
            this.columnIndex = context.getConfiguration().getInt("columnIndex", 0);
        }

        // map function will be called for every line of the csv-file
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // identical to previous map functions
            String[] columns = Helper.parseCSV(value.toString());
            if (columns == null) return;

            // convert rating from a String to float
            float rating = Helper.parseFloat(columns[7]);
            // divide rating values into 10 equal bins of width 0.5
            // bins are named after lowest value of the bin
            String bin = "0";
            if (rating >= 4.5) bin = "4.5";
            if (rating < 4.5 && rating >= 4) bin = "4";
            if (rating < 4 && rating >= 3.5) bin = "3.5";
            if (rating < 3.5 && rating >= 3) bin = "3";
            if (rating < 3 && rating >= 2.5) bin = "2.5";
            if (rating < 2.5 && rating >= 2) bin = "2";
            if (rating < 2 && rating >= 1.5) bin = "1.5";
            if (rating < 1.5 && rating >= 1) bin = "1";
            if (rating < 1 && rating >= 0.5) bin = "0.5";

            // emit column value and bin name as key and 1 as value
            context.write(new Text(columns[columnIndex] + ":" + bin), new FloatWritable(1));
        }
    }

    // ReducerAvg is a reducer class that calculates the average value of all values with the same key
    public static class ReducerAvg extends Reducer<Text, FloatWritable, Text, FloatWritable> {

        // reduce function will be called for every unique key once
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            float cnt = 0;
            for (FloatWritable val : values) {
                sum += val.get();
                cnt++;
            }

            // emit the original key as key and the average as value
            context.write(key, new FloatWritable(sum / cnt));
        }
    }

    // ReducerSum is a reducer class that sums up all values of a single key
    public static class ReducerSum extends Reducer<Text, FloatWritable, Text, FloatWritable> {

        // reduce function will be called for every unique key once
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            for (FloatWritable val : values) {
                sum += val.get();
            }

            // emit the original key as key and the sum as value
            context.write(key, new FloatWritable(sum));
        }
    }


    // main function of the job
    public static void main(String[] args) throws Exception {
        System.out.println("starting...");
        // get job name, input path and output path from job arguments
        String jobName = args[0];
        Path inputPath = new Path(args[1]);
        Path outputPath = new Path(args[2]);
        // create a new job configuration
        Configuration conf = new Configuration();
        if (jobName.contains(":")) {
            // if job name contains ":", a columnIndex was configured and
            // needs to be extracted from the job name
            String[] s = jobName.split(":");
            conf.set("columnIndex", s[1]);
            jobName = s[0];
        }
        // create a new hadoop job
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(MapReduce.class);

        // set mapper and reducer class depending on jobName
        switch (jobName.toLowerCase()){
            case "countsum":
                job.setMapperClass(MapperCount.class);
                job.setReducerClass(ReducerSum.class);
                break;
            case "pricediffavg":
                job.setMapperClass(MapperPriceDiff.class);
                job.setReducerClass(ReducerAvg.class);
                break;
            case "rating":
                job.setMapperClass(MapperRating.class);
                job.setReducerClass(ReducerSum.class);
                break;
            default:
                throw new Exception("invalid jobName");
        }

        // set output key as text and output value as float
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        // configure input path and output path
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}