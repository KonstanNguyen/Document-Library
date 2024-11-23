import pandas as pd
import pyodbc
import pickle
import matplotlib.pyplot as plt
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import MinMaxScaler
from sklearn.cluster import KMeans

SERVER = 'LAPTOP-KJ4L2LKH'
DATABASE = 'HTTM'
USERNAME = 'sa'
PASSWORD = 'Ntn@2003'

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=' + SERVER + ';DATABASE=' +
                      DATABASE + ';UID=' + USERNAME + ';PWD=' + PASSWORD)
cursor = cnxn.cursor()


def encode_attributes(df):
    le = LabelEncoder()
    scaler = MinMaxScaler()

    # Handle missing values
    df.fillna({
        'Category': 'Unknown',  # Fill categorical NaNs
        'Gender': 'Unknown',
        'Age': df['Age'].mean(),  # Fill numerical NaNs
        'Rating': df['Rating'].mean(),
    }, inplace=True)

    # Encoding and scaling
    df['Category'] = le.fit_transform(df['Category'])
    df['Gender'] = le.fit_transform(df['Gender']) #df['Gender'].map({'True': 1, 'False': 0})
    df['Age'] = df['Age'].apply(lambda age: 0 if 12 <= age <= 25 else (1 if age <= 40 else 2))
    df['Rating'] = le.fit_transform(df['Rating'])
    
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

    X = df_encoded[['Category', 'Gender', 'Age', 'Rating']]

    optimal_k = find_K(X)
    optimal_k = min(optimal_k, len(X))

    kmeans = KMeans(n_clusters=optimal_k,
                    init='k-means++',
                    max_iter=300,
                    n_init=10,
                    random_state=0)
    kmeans.fit(X)

    with open('model.pkl', 'wb') as f:
        pickle.dump(kmeans, f)

    print("Model đã được huấn luyện và lưu vào file 'model.pkl'.")
    return kmeans


query = '''
    select 
        CategoryDocument.name  as Category, 
        USERINF.gender as Gender, 
        YEAR(GETDATE()) - YEAR(USERINF.dateOfBirth) AS Age,
        RATE.numberOfRating as Rating
    from [dbo].[Account]
    inner join (
        select * from [dbo].[User]
    ) as USERINF ON USERINF.account_id = Account.account_id
    inner join (
        select 
            Document.document_id,
            Categroy.category_id,
            Categroy.name,
            History_Download.account_id
        from Document
        INNER JOIN Categroy on Document.category_id = Categroy.category_id
        INNER JOIN History_Download ON History_Download.document_id = Document.document_id
    ) as CategoryDocument ON CategoryDocument.account_id = Account.account_id
    INNER JOIN (
        select account_id, COUNT(*) as numberOfRating from Rating
        group by account_id
    ) AS RATE on RATE.account_id = Account.account_id
    '''
df = pd.read_sql(query, cnxn)
print(df)
train_kmeans(df)
