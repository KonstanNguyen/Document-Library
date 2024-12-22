from flask import Flask, jsonify
from flask_cors import CORS
from final import user_category

app = Flask(__name__)
CORS(app, origins=["http://localhost:5173"])

@app.route('/')
def index():
    return app.json.response({"recommended_category_id": int(user_category)})

if __name__ == '__main__':
    app.run(debug=True)