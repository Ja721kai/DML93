# DML93

## Python Notebooks & Scripts
This section covers a short description of Python notebooks and scripts.
### [notebooks/merge_data.ipynb](./notebooks/merge_data.ipynb)
Output data (in CSV format) from [Rainforest API](https://www.rainforestapi.com/) and [Target](https://www.target.com/) (through web scraping) are being loaded and merged into a uniform data structure.

The result can then again be used within a map-reduce routine to simply the dataset and gather information.

A sample of the output file is located within this repository as well.

### [notebooks/target_scraping.ipynb](./notebooks/target_scraping.ipynb)
The "All Deals : TVs" section on [Target](https://www.target.com/) is being scraped by using Selenium to simulate a real browser and imitate user behaviour.

Since Target using lazy loading, javascript rendering and scrolling are required to load data about TV deals.

The notebook also deals with pagination and selects the next page as long as there are more TV deals that haven't been discovered through the Selenium web browser yet.

After aggregating information about all TV deals, these are being stored (one TV per row) in a CSV output file.

Samples of output files are located within this repository as well.

### [RainforestAPI/main.py](./RainforestAPI/main.py)
The script requests the [Rainforest API](https://www.rainforestapi.com/) and stores the results as a CSV output file.

A sample of the output file is located within this repository as well.


## MapReduce
This section contains all the relevant information regarding the mapreduce jobs.

### Setup

First step is installing OpenJDK 11.

```bash
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt install openjdk-11-jdk
```

Now we can install hadoop-3.3.3.

```bash
wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.3/hadoop-3.3.3.tar.gz
tar -xzvf hadoop-3.3.3.tar.gz -C /usr/lib
```

To run the jobs locally, it is necessary to set the following environment variables.
To do so, we need to add them at the bottom of `$HOME/.bashrc`.

```bash
export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
export HADOOP_HOME="/usr/lib/hadoop-3.3.3"
export PATH="$HADOOP_HOME/bin:$JAVA_HOME/bin:$PATH"
export HADOOP_CLASSPATH="$(hadoop classpath)"
```

### [mapreduce/src/de/akad/dml93/MapReduce.java](./mapreduce/src/de/akad/dml93/MapReduce.java)
This file contains all the mapper and reducer classes.

### [mapreduce/run.sh](./mapreduce/run.sh)
This script executes all mapreduce jobs and saves for each job an output file in TSV format.
The output files are also located in the mapreduce folder.
