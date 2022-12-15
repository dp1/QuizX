from flask import Flask, jsonify
from flask_restful import Resource, Api, reqparse

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('quiz_id', required=True)

class myHUB(Resource):
    def post(self):
        args = parser.parse_args()
        quiz_id = args['quiz_id']

        return jsonify({"status":"success"})

api.add_resource(myHUB, '/broadcast_quiz')

if __name__ == '__main__':
    print('Running')
    app.run('0.0.0.0')
