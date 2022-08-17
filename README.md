# DML93

## Python Notebooks
This section covers a short description of Python notebooks that are stored in `~\notebooks\`.
### merge_data.ipynb
Output data (in CSV format) from (Rainforest API)[https://www.rainforestapi.com/] and (Target)[https://www.target.com/] (through web scraping) are being loaded and merged into a uniform data structure.

The result can then again be used within a map-reduce routine to simply the dataset and gather information.

A sample of the output file is located within this repository as well.

### target_scraping.ipynb
The "All Deals : TVs" section on (Target)[https://www.target.com/] is being scraped by using Selenium to simulate a real browser and imitate user behaviour.

Since Target using lazy loading, javascript rendering and scrolling are required to load data about TV deals.

The notebook also deals with pagination and selects the next page as long as there are more TV deals that haven't been discovered through the Selenium web browser yet.

After aggregating information about all TV deals, these are being stored (one TV per row) in a CSV output file.

Samples of output files are located within this repository as well.

