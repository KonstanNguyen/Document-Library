# import pandas as pd
# import numpy as np
# from sklearn.cluster import KMeans
# from sklearn.metrics.pairwise import cosine_similarity
# from sklearn.feature_extraction.text import TfidfVectorizer
# from sqlalchemy import create_engine

# user_history = None

# # Database Connection
# def get_database_connection():
#     """
#     Establish and return a database connection.
#     Replace 'DATABASE_URL' with your actual database connection string.
#     """
#     database_url = "mssql+pyodbc://sa:sa@MSI:16240/HTTM?driver=ODBC+Driver+17+for+SQL+Server"
#     engine = create_engine(database_url)
#     return engine.connect()


# # Load Data
# def load_user_history(user_id):
#     """
#     Load the download history of a specific user.
#     """
#     connection = get_database_connection()
#     query = f"SELECT document_id FROM history_download WHERE account_id = {user_id}"
#     user_history = pd.read_sql(query, connection)
#     if user_history.empty:
#         print(f"No download history found for user_id {user_id}.")
#     return user_history


# def load_documents():
#     """
#     Load all documents with metadata.
#     """
#     connection = get_database_connection()
#     query = '''
#     SELECT 
#         document.document_id, 
#         category_id, 
#         ISNULL(DownloadHIS.download_count,0) as download_count, 
#         title 
#     FROM document 
#     LEFT JOIN (
#         SELECT COUNT(*) as download_count, document_id
#         FROM history_download
#         GROUP BY document_id
#     ) AS DownloadHIS ON DownloadHIS.document_id = document.document_id
#     '''
#     return pd.read_sql(query, connection)


# # Preprocess Data
# def preprocess_documents(documents):
#     """
#     Convert document content into TF-IDF vectors for similarity and clustering.
#     """
#     # Xử lý thiếu dữ liệu
#     if 'title' not in documents.columns or documents['title'].isnull().all():
#         print("Document titles are missing or empty. Returning empty TF-IDF matrix.")
#         return np.zeros((0, 0))

#     documents['title'] = documents['title'].fillna('')  # Thay giá trị null bằng chuỗi rỗng

#     vectorizer = TfidfVectorizer(max_features=1000, stop_words='english')
#     tfidf_matrix = vectorizer.fit_transform(documents['title'])
#     return tfidf_matrix

# def some_vector_generation_logic(user_id, user_history, tfidf_matrix, documents):
#     """
#     Tạo vector đại diện cho người dùng dựa trên lịch sử tải tài liệu.

#     Args:
#         user_id (int): ID người dùng.
#         user_history (DataFrame): Lịch sử tải tài liệu của người dùng.
#         tfidf_matrix (scipy.sparse matrix): Ma trận TF-IDF của các tài liệu.
#         documents (DataFrame): Thông tin tài liệu (bao gồm document_id).

#     Returns:
#         numpy.array: Vector đại diện cho người dùng.
#     """
#     # Tìm index của tài liệu trong lịch sử người dùng từ ma trận TF-IDF
#     user_document_indices = documents[
#         documents['document_id'].isin(user_history['document_id'])
#     ].index

#     # Nếu người dùng không có lịch sử tải => Trả về vector rỗng
#     if len(user_document_indices) == 0:
#         return np.zeros(tfidf_matrix.shape[1])

#     # Lấy các vector TF-IDF của tài liệu trong lịch sử người dùng
#     user_vectors = tfidf_matrix[user_document_indices]

#     # Tính vector trung bình (hoặc tổng hợp)
#     user_vector = np.asarray(user_vectors.mean(axis=0)).flatten()

#     return user_vector


# def generate_user_vectors(user_id, user_history , tfidf_matrix, documents):
#     # Placeholder logic for generating user vectors
#     if user_history.empty:
#         return np.zeros(tfidf_matrix.shape[1])

#     # Otherwise, create the actual user vector
#     user_vector = some_vector_generation_logic(user_id, user_history, tfidf_matrix, documents)
#     return user_vector

# # User History-Based Recommendations
# def get_user_based_recommendations(user_history, documents, tfidf_matrix):
#     """
#     Generate recommendations based on user download history.
#     """
#     # Find documents similar to those in user history
#     user_indices = documents[documents['document_id'].isin(
#         user_history['document_id'])].index
    
#     if user_history.empty:
#         return documents.assign(relevance_score=0)
#     # user_vectors = tfidf_matrix[user_indices]
#     user_vectors = generate_user_vectors(user_id, user_history, tfidf_matrix, documents)
    
#     print("Shape of user_vectors:", user_vectors.shape)
#     print("Shape of tfidf_matrix:", tfidf_matrix.shape)
    
#     if user_vectors.shape[0] == 0 or tfidf_matrix.shape[0] == 0:
#         return []

    
#     similarity_scores = cosine_similarity(user_vectors.reshape(1,-1), tfidf_matrix)
#     print(similarity_scores.shape)

#     # Aggregate and rank recommendations
#     # avg_similarity = similarity_scores.mean(axis=0)
#     # documents['relevance_score'] = avg_similarity
#     documents['relevance_score'] = similarity_scores.flatten()
#     recommendations = documents.sort_values(by='relevance_score',
#                                             ascending=False)

#     return recommendations


# # K-Means Clustering for Unsupervised Learning
# def apply_kmeans(tfidf_matrix, num_clusters=10):
#     """
#     Cluster documents using K-Means.
#     """
#     num_clusters = min(num_clusters, tfidf_matrix.shape[0])

#     if num_clusters <= 1:
#         print("Not enough documents for clustering. Skipping K-Means.")
#         return np.zeros(tfidf_matrix.shape[0], dtype=int)  # Gán tất cả tài liệu vào một cụm

#     kmeans = KMeans(n_clusters=num_clusters, random_state=42)
#     clusters = kmeans.fit_predict(tfidf_matrix)
#     return clusters


# def get_cluster_based_recommendations(user_history, documents, clusters):
#     """
#     Generate recommendations from document clusters.
#     """

#     if 'cluster' not in documents.columns or documents['cluster'].isnull().all():
#         print("Clusters not assigned or missing. Returning default recommendations.")
#         return documents.head(10)  # Return the first 10 documents as default

#     # Identify clusters of user-downloaded documents
#     user_clusters = documents[documents['document_id'].isin(
#         user_history['document_id'])]['cluster'].unique()

#     # Recommend documents from similar clusters
#     recommendations = documents[(documents['cluster'].isin(user_clusters)) & (
#         ~documents['document_id'].isin(user_history['document_id']))]
#     return recommendations


# # Combine Recommendations
# def combine_recommendations(user_recommendations,
#                             cluster_recommendations,
#                             total_recommendations=10):
#     """
#     Combine user-based and cluster-based recommendations.
#     """
#     num_user_based = int(0.7 * total_recommendations)
#     num_cluster_based = total_recommendations - num_user_based

#     # # Convert the NumPy array to a Pandas DataFrame
#     user_recommendations_df = pd.DataFrame(user_recommendations)

#     # Slice top recommendations and combine
#     final_recommendations = pd.concat([
#         user_recommendations_df.head(num_user_based),
#         user_recommendations.head(num_user_based),
#         cluster_recommendations.head(num_cluster_based),
#     ]).drop_duplicates()

#     return final_recommendations


# # Main Function
# def recommend_documents(user_id, total_recommendations=10, num_clusters=10):
#     """
#     Generate recommendations for a user.
#     """
#     # Load data
#     user_history = load_user_history(user_id)
#     documents = load_documents()

#     print(user_history)
#     print(documents)

#     if documents.empty:
#         print("No documents found in the system.")
#         return pd.DataFrame()  # Return an empty DataFrame if no documents are found


#     # Preprocess document content
#     tfidf_matrix = preprocess_documents(documents)
#     print("Shape of tfidf_matrix:", tfidf_matrix.shape)

#     # User-based recommendations
#     user_recommendations = get_user_based_recommendations(
#         user_history, documents, tfidf_matrix)

#     num_clusters = min(num_clusters, tfidf_matrix.shape[0])
#     # K-Means clustering
#     clusters = apply_kmeans(tfidf_matrix, num_clusters)
#     if len(clusters) != len(documents):
#         raise ValueError("Mismatch between number of clusters and documents.")
#     documents['cluster'] = clusters

#     # Cluster-based recommendations
#     cluster_recommendations = get_cluster_based_recommendations(
#         user_history, documents, clusters)

#     # Combine recommendations
#     final_recommendations = combine_recommendations(user_recommendations,
#                                                     cluster_recommendations,
#                                                     total_recommendations)

#     return final_recommendations


# # import pandas as pd
# # import numpy as np
# # from sklearn.cluster import KMeans
# # from sklearn.metrics.pairwise import cosine_similarity
# # from sklearn.feature_extraction.text import TfidfVectorizer
# # from sqlalchemy import create_engine

# # user_history = None

# # # Database Connection
# # def get_database_connection():
# #     database_url = "mssql+pyodbc://sa:sa@MSI:16240/HTTM?driver=ODBC+Driver+17+for+SQL+Server"
# #     engine = create_engine(database_url)
# #     return engine.connect()


# # # Load Data
# # def load_user_history(user_id):
# #     connection = get_database_connection()
# #     query = f"SELECT document_id FROM history_download WHERE account_id = {user_id}"
# #     user_history = pd.read_sql(query, connection)
# #     if user_history.empty:
# #         print(f"No download history found for user_id {user_id}.")
# #     return user_history


# # # Combine Recommendations
# # def combine_recommendations(user_recommendations,
# #                             cluster_recommendations,
# #                             total_recommendations=10):
# #     """
# #     Combine user-based and cluster-based recommendations.
# #     """
# #     num_user_based = int(0.7 * total_recommendations)
# #     num_cluster_based = total_recommendations - num_user_based

# #     # # Convert the NumPy array to a Pandas DataFrame
# #     user_recommendations_df = pd.DataFrame(user_recommendations)

# #     # Slice top recommendations and combine
# #     final_recommendations = pd.concat([
# #         user_recommendations_df.head(num_user_based),
# #         user_recommendations.head(num_user_based),
# #         cluster_recommendations.head(num_cluster_based),
# #     ]).drop_duplicates()

# #     return final_recommendations

# # def load_documents():
# #     connection = get_database_connection()
# #     query = '''
# #     SELECT 
# #         document.document_id, 
# #         category_id, 
# #         ISNULL(DownloadHIS.download_count,0) as download_count, 
# #         title, 
# #         (SELECT age FROM users WHERE users.user_id = document.user_id) AS user_age,
# #         (SELECT gender FROM users WHERE users.user_id = document.user_id) AS user_gender,
# #         (SELECT rating FROM ratings WHERE ratings.document_id = document.document_id) AS rating
# #     FROM document 
# #     LEFT JOIN (
# #         SELECT COUNT(*) as download_count, document_id
# #         FROM history_download
# #         GROUP BY document_id
# #     ) AS DownloadHIS ON DownloadHIS.document_id = document.document_id
# #     '''
# #     return pd.read_sql(query, connection)


# # # Preprocess Data
# # def preprocess_documents(documents):
# #     if 'title' not in documents.columns or documents['title'].isnull().all():
# #         print("Document titles are missing or empty. Returning empty TF-IDF matrix.")
# #         return np.zeros((0, 0))

# #     documents['title'] = documents['title'].fillna('')

# #     vectorizer = TfidfVectorizer(max_features=1000, stop_words='english')
# #     tfidf_matrix = vectorizer.fit_transform(documents['title'])
# #     return tfidf_matrix


# # def preprocess_user_data(documents):
# #     """
# #     Chuyển đổi các yếu tố bổ sung (độ tuổi, giới tính, thể loại, rating) thành ma trận đặc trưng.
# #     """
# #     features = []

# #     # Chuyển đổi các yếu tố vào ma trận
# #     features.append(documents['user_age'].fillna(0))  # Độ tuổi
# #     features.append(pd.get_dummies(documents['user_gender']))  # Giới tính (nam, nữ)
# #     features.append(pd.get_dummies(documents['category_id']))  # Thể loại tài liệu (dùng one-hot encoding)
# #     features.append(documents['rating'].fillna(0))  # Điểm đánh giá

# #     return np.column_stack(features)


# # # Update the logic to include new features
# # def generate_user_vectors(user_id, user_history, tfidf_matrix, documents):
# #     user_document_indices = documents[documents['document_id'].isin(user_history['document_id'])].index

# #     if len(user_document_indices) == 0:
# #         return np.zeros(tfidf_matrix.shape[1] + 4)  # Adjusting to the number of additional features

# #     user_vectors = tfidf_matrix[user_document_indices]
# #     user_data = preprocess_user_data(documents.loc[user_document_indices])

# #     user_vector = np.asarray(user_vectors.mean(axis=0)).flatten()
# #     user_data_vector = np.asarray(user_data.mean(axis=0)).flatten()

# #     # Combine TF-IDF vector and user-specific features
# #     user_combined_vector = np.concatenate([user_vector, user_data_vector])

# #     return user_combined_vector


# # # K-Means Clustering for Unsupervised Learning with extra features
# # def apply_kmeans(tfidf_matrix, documents, num_clusters=10):
# #     num_clusters = min(num_clusters, tfidf_matrix.shape[0])

# #     if num_clusters <= 1:
# #         print("Not enough documents for clustering. Skipping K-Means.")
# #         return np.zeros(tfidf_matrix.shape[0], dtype=int)  # All documents in the same cluster

# #     features_matrix = np.column_stack([tfidf_matrix.toarray(), preprocess_user_data(documents)])

# #     kmeans = KMeans(n_clusters=num_clusters, random_state=42)
# #     clusters = kmeans.fit_predict(features_matrix)

# #     return clusters


# # def get_user_based_recommendations(user_history, documents, tfidf_matrix):
# #     user_indices = documents[documents['document_id'].isin(user_history['document_id'])].index

# #     if user_history.empty:
# #         return documents.assign(relevance_score=0)

# #     user_vectors = generate_user_vectors(user_id, user_history, tfidf_matrix, documents)
    
# #     if user_vectors.shape[0] == 0 or tfidf_matrix.shape[0] == 0:
# #         return []

# #     similarity_scores = cosine_similarity(user_vectors.reshape(1, -1), tfidf_matrix)
# #     documents['relevance_score'] = similarity_scores.flatten()
# #     recommendations = documents.sort_values(by='relevance_score', ascending=False)

# #     return recommendations


# # def get_cluster_based_recommendations(user_history, documents, clusters):
# #     if 'cluster' not in documents.columns or documents['cluster'].isnull().all():
# #         print("Clusters not assigned or missing. Returning default recommendations.")
# #         return documents.head(10)

# #     user_clusters = documents[documents['document_id'].isin(user_history['document_id'])]['cluster'].unique()
# #     recommendations = documents[(documents['cluster'].isin(user_clusters)) & (~documents['document_id'].isin(user_history['document_id']))]
# #     return recommendations


# # # Main Function for Combining Recommendations
# # def recommend_documents(user_id, total_recommendations=10, num_clusters=10):
# #     user_history = load_user_history(user_id)
# #     documents = load_documents()

# #     if documents.empty:
# #         print("No documents found in the system.")
# #         return pd.DataFrame()

# #     tfidf_matrix = preprocess_documents(documents)

# #     user_recommendations = get_user_based_recommendations(user_history, documents, tfidf_matrix)

# #     num_clusters = min(num_clusters, tfidf_matrix.shape[0])
# #     clusters = apply_kmeans(tfidf_matrix, documents, num_clusters)
    
# #     if len(clusters) != len(documents):
# #         raise ValueError("Mismatch between number of clusters and documents.")
# #     documents['cluster'] = clusters

# #     cluster_recommendations = get_cluster_based_recommendations(user_history, documents, clusters)

# #     final_recommendations = combine_recommendations(user_recommendations, cluster_recommendations, total_recommendations)

# #     return final_recommendations



# # # Run the System
# # if __name__ == "__main__":
# #     users_id = [1,2,3,4,5,6,8,9,10,11,12,14,15,16,17,19,20,21,23,25,26,30,31,32]
# #     # user_id = 2  # Replace with the target user ID
# #     # total_recommendations = 10
# #     # recommendations = recommend_documents(user_id, total_recommendations)
# #     # print(recommendations)

# #     total_recommendations = 10
# #     for user_id in users_id:
# #         recommendations = recommend_documents(user_id, total_recommendations)
# #         print(recommendations)
        


from sklearn.cluster import KMeans
import pandas as pd
from sklearn.preprocessing import StandardScaler

# Giả sử bạn có DataFrame với các đặc trưng (gender, age_group, category_id)
data = {
    'gender': [0, 1, 0, 1, 0],  # Giới tính
    'age_group': [1, 2, 1, 3, 2],  # Nhóm tuổi (ví dụ: 1: 18-25, 2: 26-35, ...)
    'category_id': [1, 2, 3, 1, 2]  # Mã thể loại tài liệu
}

df = pd.DataFrame(data)

# Chuẩn hóa dữ liệu
scaler = StandardScaler()
scaled_data = scaler.fit_transform(df)

# Áp dụng K-means với 3 cluster
kmeans = KMeans(n_clusters=3, random_state=0)
df['cluster'] = kmeans.fit_predict(scaled_data)

# Xem kết quả phân nhóm
print(df)