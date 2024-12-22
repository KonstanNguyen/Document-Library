import pandas as pd
import numpy as np
import json
from flask import Flask, request, jsonify
from final import df, cnxn
from sklearn.neighbors import NearestNeighbors
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

def user_history_download(user_id):
    query = f'''
        WITH RankedDocuments AS (
            SELECT 
                document.category_id,
                ROW_NUMBER() OVER (PARTITION BY document.category_id ORDER BY history_download.date DESC) AS rn
            FROM history_download
            INNER JOIN document ON document.document_id = history_download.document_id
            INNER JOIN (
                SELECT account_id
                FROM account
                WHERE user_id = {user_id}
            ) AS account ON account.account_id = history_download.account_id
        )
        SELECT category_id
        FROM RankedDocuments
        WHERE rn = 1
        ORDER BY category_id
    '''
    return pd.read_sql_query(query, cnxn)

def recommended_category_id(user_id):
    # Truy vấn thông tin từ cơ sở dữ liệu
    user_query = f'''
        SELECT 
            gender, 
            YEAR(GETDATE()) - YEAR(date_of_birth) AS age
        FROM [dbo].[doc_user]
        WHERE user_id = {user_id};
    '''
    user_df = pd.read_sql(user_query, cnxn)

    if user_df.empty:
        return jsonify({"error": f"No data found for user_id {user_id}"}), 404

    # # Mã hóa cột 'gender'
    user_df['gender'] = user_df['gender'].astype(int)  # Đảm bảo dữ liệu là chuỗi

    # Chuẩn hóa cột 'gender' và 'age'
    print(user_df)
    user_np = np.array(user_df[['gender', 'age']])
    nn = NearestNeighbors(n_neighbors=1)
    nn.fit(df[['gender', 'age']])
    _, indices = nn.kneighbors(user_np)

    user_cluster = df.iloc[indices[0][0]]['cluster']
    user_category = df.iloc[indices[0][0]]['category_id']
    print("User id: ", user_id)
    print(f"User belongs to cluster {user_cluster} and suggested category is {user_category}")

    cluster_summary = df.groupby('cluster').mean()
    print(cluster_summary)
    return user_category

def recommended_documents(category_id):

    query = f'''
        select document_id
        from document
        where category_id = {category_id}
    '''
    return pd.read_sql_query(query, cnxn)

def recently_documents(category_ids):
    categorys = f"({', '.join(map(str, category_ids))})"
    print("Test 1", categorys)
    query = f'''
        select top(10) document_id
        from document
        where category_id in {categorys}
        ORDER BY views DESC
    '''
    return pd.read_sql_query(query, cnxn)

@app.route('/')
def index():
    user_id = request.args.get('user_id')

    if not user_id:
        return jsonify({"error": "user_id is required"}), 400

    user_history = user_history_download(user_id)

    if user_history.empty:
        user_category = recommended_category_id(user_id=user_id)
        return app.json.response({"recommended_category_id": int(user_category)})

    category_ids = user_history['category_id'].values
    user_documents = recently_documents(category_ids)
    user_category = recommended_category_id(user_id=user_id)
    recommended_document_based_on_Kmeans = recommended_documents(user_category)
    final_recommendations = pd.concat([
        pd.Series(user_documents.values.flatten().tolist()),
        pd.Series(recommended_document_based_on_Kmeans.values.flatten().tolist())
    ], ignore_index=True).drop_duplicates()
    
    return app.json.response({
        "recommended_document_id": final_recommendations.tolist()
    })


if __name__ == '__main__':
    app.run(debug=True)