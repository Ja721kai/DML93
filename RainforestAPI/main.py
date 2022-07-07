import json
import requests
import requests_cache
import pandas as pd

requests_cache.install_cache('rainforest_cache')

# Set up the request parameters
params = {
'api_key': '1DD6DF4727184B8A9214AD1872A248FB',
  'type': 'deals',
  'amazon_domain': 'amazon.com',
  'category_id': '1266092011',
  'max_page': 5
}

# make the http GET request to Rainforest API
api_result = requests.get('https://api.rainforestapi.com/request', params)

# Deals_results als python object
deals_results = api_result.json()["deals_results"]
df = pd.DataFrame(deals_results)
pd.set_option('display.max_columns', None)  # or 1000
df.to_csv("rainforest_tvdeals_20220705.csv")
