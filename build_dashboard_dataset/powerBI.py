import pandas as pd
import os


def get_items_in_partr_row_simple(line: str):
    keyvalue = line.split("\t")
    return keyvalue[0], keyvalue[1].strip()


def get_items_in_partr_row_ratings(line: str):
    name = line.split(":")[0]
    rating = line.split(":")[1].split("\t")[0]
    count = line.split("\t")[1]
    return name, rating, count


def get_dir_paths(path, startswith_string):
    dirs = os.listdir(path)
    count_dirs = list(filter(lambda k: k.startswith(startswith_string), dirs))
    paths = [os.path.join(path, x) for x in count_dirs]
    return paths


def deal_count_by_filter(path_to_hadoop_results="../mapreduce/"):
    paths = get_dir_paths(path_to_hadoop_results, "output-count")

    results = {
        "filter_type": [],
        "name": [],
        "count": []
    }
    for path in paths:
        filter_type = path.split("/")[-1].split("-")[-1]
        with open(os.path.join(path,"part-r-00000"), "r") as f:
            for line in f.readlines():
                name, count = get_items_in_partr_row_simple(line)
                results["filter_type"] += [filter_type]
                results["name"] += [name]
                results["count"] += [int(float(count))]
    df = pd.DataFrame.from_dict(results)
    df.to_excel("count_by_filter.xlsx", index=False)


def rating_by_filter(path_to_hadoop_results="../mapreduce/"):
    paths = get_dir_paths(path_to_hadoop_results, "output-rating")

    results = {
        "filter_type": [],
        "name": [],
        "rating": [],
        "rating-count": []
    }
    for path in paths:
        filter_type = path.split("/")[-1].split("-")[-1]
        with open(os.path.join(path, "part-r-00000"), "r") as f:
            for line in f.readlines():
                name, rating, count = get_items_in_partr_row_ratings(line)
                results["filter_type"] += [filter_type]
                results["name"] += [name]
                results["rating"] += [float(rating)]
                results["rating-count"] += [int(float(count))]
    df = pd.DataFrame.from_dict(results)
    df.to_excel("rating_by_filter.xlsx", index=False)


def pricediffavg(path_to_hadoop_results="../mapreduce/"):
    path = get_dir_paths(path_to_hadoop_results, "output-pricediffavg")[0]

    results = {
        "datasource": [],
        "pricediffavg": []
    }
    with open(os.path.join(path, "part-r-00000"), "r") as f:
        for line in f.readlines():
            source, pricediffavg = get_items_in_partr_row_simple(line)
            results["datasource"] += [source]
            results["pricediffavg"] += [pricediffavg]
    df = pd.DataFrame.from_dict(results)
    df.to_excel("pricediffavg.xlsx", index=False)


if __name__ == '__main__':
    # deal_count_by_filter()
    # rating_by_filter()
    pricediffavg()
