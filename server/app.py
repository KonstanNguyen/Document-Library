import pandas as pd
import pyodbc
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans

SERVER = 'localhost'
DATABASE = 'HTTM'
USERNAME = 'sa'
PASSWORD = 'sa'

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER='+SERVER+';DATABASE='+DATABASE+';UID='+USERNAME+';PWD='+ PASSWORD)
cursor = cnxn.cursor()

def encode_attributes(df):
    
    le = LabelEncoder()
    scaler = MinMaxScaler()
    df['Category'] = le.fit_transform(df['Category'])
    df['Gender'] = df['Gender'].map({'Male': 0, 'Female': 1})

    def categorize_age(age):
        if age >= 12 and age <= 25:
            return 0  # Trẻ
        elif age > 25 and age <= 40:
            return 1  # Trung niên
        else:
            return 2  # Cao niên

    df['Age'] = df['Age'].apply(categorize_age)
    df['Rating'] = scaler.fit_transform(df[['Rating']])

    return df

def find_K(df):
    distortions = []
    K = range(1, 10)

    for k in K:
        kmeanModel = KMeans(n_clusters=k, init='k-means++', max_iter=300, n_init=10, random_state=0)
        kmeanModel.fit(dataset)
        distortions.append(kmeanModel.inertia_)
        print(f'Inertia for {k} clusters: {kmeanModel.inertia_}')

    for i in range(1, len(distortions)):
        if distortions[i] / distortions[i-1] > 0.93:
            return i + 1

    # Nếu không có sự giảm nhanh, trả về số cụm lớn nhất
    return 9


def train_kmeans(df, n_clusters=3):
    df_encoded = encode_attributes(df)
    
    X = df_encoded[['Category', 'Gender', 'Age', 'Rating']]
    
    kmeans = KMeans(n_clusters=find_K(X), init='k-means++', max_iter=300, n_init=10, random_state=0)
    kmeans.fit(X)
    
    with open('model.pkl', 'wb') as f:
        pickle.dump(kmeans, f)
    
    print("Model đã được huấn luyện và lưu vào file 'model.pkl'.")
    return kmeans