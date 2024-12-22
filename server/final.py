import numpy as np
import pandas as pd
import pyodbc
import pickle
import matplotlib.pyplot as plt
from sklearn.metrics import silhouette_score
from sklearn.neighbors import NearestNeighbors
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler


SERVER = 'MSI'
DATABASE = 'HTTM'
USERNAME = 'sa'
PASSWORD = 'sa'

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=' + SERVER + ';DATABASE=' +
                      DATABASE + ';UID=' + USERNAME + ';PWD=' + PASSWORD)
cursor = cnxn.cursor()


def find_K(df):
    # distortions = []
    # max_clusters = min(10, len(df))
    # K = range(1, max_clusters + 1)
       
    # for k in K:
    #     kmeanModel = KMeans(n_clusters=k,
    #                         init='k-means++',
    #                         max_iter=300,
    #                         n_init=10,
    #                         random_state=0)
    #     kmeanModel.fit(df)
    #     distortions.append(kmeanModel.inertia_)
    #     print(f'Inertia for {k} clusters: {kmeanModel.inertia_}')
    
    # for i in range(1, len(distortions)):
    #     if distortions[i - 1] != 0 and distortions[i] / distortions[i - 1] > 0.93:
    #         return i + 1

    # # Nếu không có sự giảm nhanh, trả về số cụm lớn nhất
    # return max_clusters
    distortions = []
    K = range(1, min(10, len(df)) + 1)
    for k in K:
        kmeans = KMeans(n_clusters=k, init='k-means++', max_iter=300, n_init=10, random_state=42)
        kmeans.fit(df)
        distortions.append(kmeans.inertia_)
    # "Elbow" method
    for i in range(1, len(distortions)):
        if distortions[i - 1] != 0 and distortions[i] / distortions[i - 1] > 0.93:
            return i + 1
    return max(K)

query = '''
	SELECT
    USERINF.gender AS gender, 
    YEAR(GETDATE()) - YEAR(USERINF.date_of_birth) AS age, 
    CategoryDocument.category_id AS category_id
FROM [dbo].[account]
INNER JOIN (
    SELECT user_id, gender, date_of_birth
    FROM [dbo].[doc_user]
) AS USERINF ON USERINF.user_id = account.user_id
INNER JOIN (
    SELECT 
        history_download.account_id, 
        document.category_id
    FROM document
    INNER JOIN history_download ON history_download.document_id = document.document_id
) AS CategoryDocument ON CategoryDocument.account_id = account.account_id;
    '''
df = pd.read_sql(query, cnxn)

scaler = StandardScaler()
scaled_data = scaler.fit_transform(df[['gender', 'age', 'category_id']])

kmeans = KMeans(n_clusters=find_K(df), random_state=42)

df['cluster'] = kmeans.fit_predict(scaled_data)

# user_data = np.array([[1, 20]])
# nn = NearestNeighbors(n_neighbors=1)
# nn.fit(df[['gender', 'age']])
# _, indices = nn.kneighbors(user_data)

# user_cluster = df.iloc[indices[0][0]]['cluster']
# user_category = df.iloc[indices[0][0]]['category_id']
# print(f"User belongs to cluster {user_cluster} and suggested category is {user_category}")

# cluster_summary = df.groupby('cluster').mean()
# print(cluster_summary)