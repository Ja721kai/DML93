package de.akad.dml93;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapReduce {

    public static class MapperPriceDiff extends Mapper<Object, Text, Text, FloatWritable> {
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] columns = Helper.parseCSV(value.toString());
            if (columns == null) return;

            float listPrice = Helper.parsePrice(columns[5]);
            float dealPrice = Helper.parsePrice(columns[4]);

            word.set(columns[0]);
            context.write(word, new FloatWritable(listPrice - dealPrice));
        }
    }

    public static class MapperCount extends Mapper<Object, Text, Text, FloatWritable> {
        private Text word = new Text();
        private int columnIndex = 0;

        public void setup(Context context) {
            this.columnIndex = context.getConfiguration().getInt("columnIndex", 0);
        }

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] columns = Helper.parseCSV(value.toString());
            if (columns == null) return;

            word.set(columns[columnIndex]);
            context.write(word, new FloatWritable(1));
        }
    }

    public static class MapperRating extends Mapper<Object, Text, Text, FloatWritable> {
        private Text word = new Text();
        private int columnIndex = 0;

        public void setup(Context context) {
            this.columnIndex = context.getConfiguration().getInt("columnIndex", 0);
        }

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] columns = Helper.parseCSV(value.toString());
            if (columns == null) return;

            float rating = Helper.parseFloat(columns[7]);
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

            word.set(columns[columnIndex] + ":" + bin);
            context.write(word, new FloatWritable(1));
        }
    }


    public static class ReducerAvg extends Reducer<Text, FloatWritable, Text, FloatWritable> {
        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            float cnt = 0;
            for (FloatWritable val : values) {
                sum += val.get();
                cnt++;
            }
            result.set(sum / cnt);
            context.write(key, result);
        }
    }

    public static class ReducerSum extends Reducer<Text, FloatWritable, Text, FloatWritable> {
        private FloatWritable result = new FloatWritable();

        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            for (FloatWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println("starting...");
        String mapreduceParameter = args[0];
        Path inputPath = new Path(args[1]);
        Path outputPath = new Path(args[2]);
        Configuration conf = new Configuration();
        if (mapreduceParameter.contains(":")) {
            String[] s = mapreduceParameter.split(":");
            conf.set("columnIndex", s[1]);
            mapreduceParameter = s[0];
        }
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(MapReduce.class);
        if (mapreduceParameter.equalsIgnoreCase("countsum")) {
            job.setMapperClass(MapperCount.class);
            job.setReducerClass(ReducerSum.class);
        } else if (mapreduceParameter.equalsIgnoreCase("pricediffavg")) {
            job.setMapperClass(MapperPriceDiff.class);
            job.setReducerClass(ReducerAvg.class);
        } else if (mapreduceParameter.equalsIgnoreCase("rating")) {
            job.setMapperClass(MapperRating.class);
            job.setReducerClass(ReducerSum.class);
        } else {
            throw new Exception("invalid mapreduce parameter");
        }
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}