import pandas as pd
import pyodbc
import pickle
import matplotlib.pyplot as plt
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans

SERVER = 'DESKTOP-SVEFPRH\\MSSQLSERVER_TN'
DATABASE = 'HTTM'
USERNAME = 'sa'
PASSWORD = 'sa'

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=' + SERVER + ';DATABASE=' +
                      DATABASE + ';UID=' + USERNAME + ';PWD=' + PASSWORD)
cursor = cnxn.cursor()

# Get user download history
user_id = 1  # Example user ID
user_history = pd.read_sql(f"SELECT document_id FROM history_download WHERE account_id = {user_id}", cnxn)

documents = pd.read_sql(f"SELECT document_id FROM document WHERE account_id = {user_id}", cnxn)

# Find similar documents based on download history
similar_docs = documents[documents['document_id'].isin(user_history['document_id'])]

# Optionally rank by popularity within the user's category
popular_docs = documents[documents['category_id'].isin(similar_docs['category_id'])].sort_values(by='download_count', ascending=False)

# Select the top 70% recommendations from user history
user_based_recommendations = popular_docs.head(int(0.7 * total_recommendations))


def encode_attributes(df):
    le = LabelEncoder()
    scaler = MinMaxScaler()

    # Handle missing values
    df.fillna({
        'Category': 'Unknown',  # Fill categorical NaNs
        'Gender': 'Unknown',
        'Age': df['Age'].mean(),  # Fill numerical NaNs
        # 'Rating': df['Rating'].mean(),
    }, inplace=True)

    # Encoding and scaling
    df['Category'] = le.fit_transform(df['Category'])
    df['Gender'] = le.fit_transform(df['Gender']) #df['Gender'].map({'True': 1, 'False': 0})
    df['Age'] = df['Age'].apply(lambda age: 0 if 12 <= age <= 25 else (1 if age <= 40 else 2))
    # df['Rating'] = le.fit_transform(df['Rating'])
    
    df = df.drop_duplicates()
    return df


def find_K(df):
    distortions = []
    max_clusters = min(10, len(df))
    K = range(1, max_clusters + 1)
       
    for k in K:
        kmeanModel = KMeans(n_clusters=k,
                            init='k-means++',
                            max_iter=300,
                            n_init=10,
                            random_state=0)
        kmeanModel.fit(df)
        distortions.append(kmeanModel.inertia_)
        print(f'Inertia for {k} clusters: {kmeanModel.inertia_}')

    plt.figure(figsize=(8, 5))
    plt.plot(K, distortions, marker='o')
    plt.xlabel('Number of Clusters (K)')
    plt.ylabel('Distortion (Inertia)')
    plt.title('Elbow Method')
    plt.show()
    
    for i in range(1, len(distortions)):
        if distortions[i - 1] != 0 and distortions[i] / distortions[i - 1] > 0.93:
            return i + 1

    # Nếu không có sự giảm nhanh, trả về số cụm lớn nhất
    return max_clusters


def train_kmeans(df):
    df_encoded = encode_attributes(df)
    
    # # Handle any remaining NaN values after encoding
    # df_encoded.fillna(0, inplace=True)  # Replace any leftover NaNs with 0 or an appropriate default value

    X = df_encoded[['Category', 'Gender', 'Age'
                    # , 'Rating'
                    ]]

    optimal_k = find_K(X)
    optimal_k = min(optimal_k, len(X))

    kmeans = KMeans(n_clusters=optimal_k,
                    init='k-means++',
                    max_iter=300,
                    n_init=10,
                    random_state=0)
    kmeans.fit(X)

    # with open('model.pkl', 'wb') as f:
    #     pickle.dump(kmeans, f)

    # print("Model đã được huấn luyện và lưu vào file 'model.pkl'.")
    return kmeans


# query = '''
#     select 
#         CategoryDocument.name  as Category, 
#         USERINF.gender as Gender, 
#         YEAR(GETDATE()) - YEAR(USERINF.dateOfBirth) AS Age,
#         RATE.numberOfRating as Rating
#     from [dbo].[Account]
#     inner join (
#         select * from [dbo].[User]
#     ) as USERINF ON USERINF.account_id = Account.account_id
#     inner join (
#         select 
#             Document.document_id,
#             Categroy.category_id,
#             Categroy.name,
#             History_Download.account_id
#         from Document
#         INNER JOIN Categroy on Document.category_id = Categroy.category_id
#         INNER JOIN History_Download ON History_Download.document_id = Document.document_id
#     ) as CategoryDocument ON CategoryDocument.account_id = Account.account_id
#     INNER JOIN (
#         select account_id, COUNT(*) as numberOfRating from Rating
#         group by account_id
#     ) AS RATE on RATE.account_id = Account.account_id
#     '''
# df = pd.read_sql(query, cnxn)
# print(df)
# train_kmeans(df)


# Identify clusters of user-downloaded documents
user_clusters = documents[documents['document_id'].isin(user_history['document_id'])]['cluster'].unique()

# Recommend documents from the same clusters
cluster_based_recommendations = documents[(documents['cluster'].isin(user_clusters)) & 
                                          (~documents['document_id'].isin(user_history['document_id']))]

# Select the top 30% recommendations from cluster-based suggestions
cluster_based_recommendations = cluster_based_recommendations.head(int(0.3 * total_recommendations))

# Combine and ensure uniqueness
final_recommendations = pd.concat([user_based_recommendations, cluster_based_recommendations]).drop_duplicates()

# Sort recommendations (optional)
final_recommendations = final_recommendations.sort_values(by='relevance_score', ascending=False)

# Calculate recommendation limits dynamically
total_recommendations = 10  # For example
num_user_based = int(0.7 * total_recommendations)
num_cluster_based = total_recommendations - num_user_based

# Slice top recommendations based on calculated limits
final_recommendations = pd.concat([
    user_based_recommendations.head(num_user_based),
    cluster_based_recommendations.head(num_cluster_based)
]).drop_duplicates()

print(final_recommendations)