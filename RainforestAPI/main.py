# import necessary libraries
import requests
import requests_cache
import pandas as pd

# set up request cache to reduce identical API calls
requests_cache.install_cache('rainforest_cache')

# Set up the request parameters
params = {
  'api_key': '1DD6DF4727184B8A9214AD1872A248FB',
  'type': 'deals',
  'amazon_domain': 'amazon.com',
  'category_id': '1266092011',  # id for category TV & Video
  'max_page': 5
}

# send the http GET request to Rainforest API
api_result = requests.get('https://api.rainforestapi.com/request', params)

# convert deals results to csv using pandas
deals_results = api_result.json()["deals_results"]
df = pd.DataFrame(deals_results)
df.to_csv("rainforest_tvdeals_20220705.csv")
