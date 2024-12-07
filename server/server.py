from flask import Flask, jsonify
from final import user_category

app = Flask(__name__)

@app.route('/')
def index():
    return app.json.response({"recommended_category_id": int(user_category)})

if __name__ == '__main__':
    app.run(debug=True)