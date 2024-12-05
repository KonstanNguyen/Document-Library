import pandas as pd
import numpy as np
from sklearn.cluster import KMeans
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
from sqlalchemy import create_engine

user_history = None

# Database Connection
def get_database_connection():
    """
    Establish and return a database connection.
    Replace 'DATABASE_URL' with your actual database connection string.
    """
    database_url = "mssql+pyodbc://sa:Ntn123@LAPTOP-KJ4L2LKH:1433/HTTM?driver=ODBC+Driver+17+for+SQL+Server"
    engine = create_engine(database_url)
    return engine.connect()


# Load Data
def load_user_history(user_id):
    """
    Load the download history of a specific user.
    """
    connection = get_database_connection()
    query = f"SELECT document_id FROM history_download WHERE account_id = {user_id}"
    return pd.read_sql(query, connection)


def load_documents():
    """
    Load all documents with metadata.
    """
    connection = get_database_connection()
    query = '''
    SELECT 
	document.document_id, 
	category_id, 
	DownloadHIS.download_count, 
	title 
FROM document 
INNER JOIN (
	SELECT COUNT(*) as download_count, document_id
	FROM history_download
	GROUP BY document_id
) AS DownloadHIS ON DownloadHIS.document_id = document.document_id
    '''
    return pd.read_sql(query, connection)


# Preprocess Data
def preprocess_documents(documents):
    """
    Convert document content into TF-IDF vectors for similarity and clustering.
    """
    vectorizer = TfidfVectorizer(max_features=1000, stop_words='english')
    tfidf_matrix = vectorizer.fit_transform(documents['title'])
    return tfidf_matrix

def generate_user_vectors(user_id, user_history):
    # Placeholder logic for generating user vectors
    if user_history.empty:
        return np.array([])  # Return an empty array if there's no data

    # Otherwise, create the actual user vector
    user_vector = some_vector_generation_logic(user_id, user_history)
    return user_vector

# User History-Based Recommendations
def get_user_based_recommendations(user_history, documents, tfidf_matrix):
    """
    Generate recommendations based on user download history.
    """
    # Find documents similar to those in user history
    user_indices = documents[documents['document_id'].isin(
        user_history['document_id'])].index
    
    if user_history.empty:
        return np.array([])  # Return an empty array if user history is empty
    # user_vectors = tfidf_matrix[user_indices]
    user_vectors = generate_user_vectors(user_id, user_history)
    
    print("Shape of user_vectors:", user_vectors.shape)
    print("Shape of tfidf_matrix:", tfidf_matrix.shape)
    
    if user_vectors.shape[0] == 0 or tfidf_matrix.shape[0] == 0:
        raise ValueError("One of the input arrays is empty. Please check data generation steps.")

    
    similarity_scores = cosine_similarity(user_vectors, tfidf_matrix)

    # Aggregate and rank recommendations
    avg_similarity = similarity_scores.mean(axis=0)
    documents['relevance_score'] = avg_similarity
    recommendations = documents.sort_values(by='relevance_score',
                                            ascending=False)

    return recommendations


# K-Means Clustering for Unsupervised Learning
def apply_kmeans(tfidf_matrix, num_clusters=10):
    """
    Cluster documents using K-Means.
    """
    num_clusters = min(num_clusters, tfidf_matrix.shape[0])
    kmeans = KMeans(n_clusters=num_clusters, random_state=42)
    clusters = kmeans.fit_predict(tfidf_matrix)
    return clusters


def get_cluster_based_recommendations(user_history, documents, clusters):
    """
    Generate recommendations from document clusters.
    """
    # Identify clusters of user-downloaded documents
    user_clusters = documents[documents['document_id'].isin(
        user_history['document_id'])]['cluster'].unique()

    # Recommend documents from similar clusters
    recommendations = documents[(documents['cluster'].isin(user_clusters)) & (
        ~documents['document_id'].isin(user_history['document_id']))]
    return recommendations


# Combine Recommendations
def combine_recommendations(user_recommendations,
                            cluster_recommendations,
                            total_recommendations=10):
    """
    Combine user-based and cluster-based recommendations.
    """
    num_user_based = int(0.7 * total_recommendations)
    num_cluster_based = total_recommendations - num_user_based

    # Convert the NumPy array to a Pandas DataFrame
    user_recommendations_df = pd.DataFrame(user_recommendations)

    # Slice top recommendations and combine
    final_recommendations = pd.concat([
        user_recommendations_df.head(num_user_based),
        cluster_recommendations.head(num_cluster_based)
    ]).drop_duplicates()

    return final_recommendations


# Main Function
def recommend_documents(user_id, total_recommendations=10, num_clusters=10):
    """
    Generate recommendations for a user.
    """
    # Load data
    user_history = load_user_history(user_id)
    documents = load_documents()

    # Preprocess document content
    tfidf_matrix = preprocess_documents(documents)
    print("Shape of tfidf_matrix:", tfidf_matrix.shape)

    # User-based recommendations
    user_recommendations = get_user_based_recommendations(
        user_history, documents, tfidf_matrix)

    num_clusters = min(num_clusters, tfidf_matrix.shape[0])
    # K-Means clustering
    clusters = apply_kmeans(tfidf_matrix, num_clusters)
    documents['cluster'] = clusters

    # Cluster-based recommendations
    cluster_recommendations = get_cluster_based_recommendations(
        user_history, documents, clusters)

    # Combine recommendations
    final_recommendations = combine_recommendations(user_recommendations,
                                                    cluster_recommendations,
                                                    total_recommendations)

    return final_recommendations


# Run the System
if __name__ == "__main__":
    user_id = 123  # Replace with the target user ID
    total_recommendations = 10
    recommendations = recommend_documents(user_id, total_recommendations)
    print(recommendations)
