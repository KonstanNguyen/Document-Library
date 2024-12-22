import pandas as pd
import numpy as np
from flask import Flask, request, jsonify
from final import df, cnxn
from sklearn.neighbors import NearestNeighbors

app = Flask(__name__)

@app.route('/')
def index():
    user_id = request.args.get('user_id')

    if not user_id:
        return jsonify({"error": "user_id is required"}), 400

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
    return app.json.response({"recommended_category_id": int(user_category)})

if __name__ == '__main__':
    app.run(debug=True)