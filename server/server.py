import logging
import sys
import argparse

import firebase_admin
from firebase_admin import credentials, firestore
from firebase_admin.firestore import ArrayUnion, ArrayRemove, Query, Increment

from flask import Flask
from flask_restful import Resource, Api, reqparse

logging.basicConfig(level=logging.INFO, stream=sys.stdout)

parser = argparse.ArgumentParser()
parser.add_argument('credentials_file', help='Firebase credentials json file')
args = parser.parse_args()

"""
"users": {
    "u0": {
        "uuid": "u0",
        "displayName": "User Name",
        "score": 42,
        "pendingChallenges": ["q0", "q1"]
    },
    "u1": {
        ...
    }
}

"quizzes": {
    "q0": {
        "uuid": "q0",
        "authorId": "u1",
        "imageUri": "...",
        "correctAnswer": "answer",
        "sentToUsers": false,
        "correctSubmissions": 3,
        "wrongSubmissions": 2
    }
}
"""

USERS_ABOVE = 3
USERS_BELOW = 3
SCORE_K = 10

cred = credentials.Certificate(args.credentials_file)
firebase_admin.initialize_app(cred)
db = firestore.client()

users_ref = db.collection('users')
quizzes_ref = db.collection('quizzes')

def on_new_quiz(quiz_id, doc):
    doc = doc.to_dict()
    authorId = doc['authorId']
    logging.info(f'Received quiz {quiz_id}, sent by {authorId}')

    if doc.get('sentToUsers', False):
        logging.info(f'Quiz was already sent to users. Skipping')
        return

    author = users_ref.document(authorId).get()
    if not author.exists:
        logging.warning(f'Author does not exists. Skipping quiz')
        return

    author = author.to_dict()
    author_id = author['uuid']
    author_score = author['score']

    logging.info(f'Author has score {author_score}')

    # Users with higher score
    for user in users_ref.where('score', '>=', author_score).order_by('score').limit(USERS_ABOVE + 1).stream():
        if user.id != author_id:
            users_ref.document(user.id).update(
                {'pendingChallenges': ArrayUnion([quiz_id])}
            )
            logging.info(f'Added quiz {quiz_id} to user {user.id}')

    # Users with lower score
    for user in users_ref.where('score', '<', author_score).order_by('score', direction=Query.DESCENDING).limit(USERS_BELOW).stream():
        if user.id != author_id:
            users_ref.document(user.id).update(
                {'pendingChallenges': ArrayUnion([quiz_id])}
            )
            logging.info(f'Added quiz {quiz_id} to user {user.id}')

    quizzes_ref.document(quiz_id).update(
        {'sentToUsers': True}
    )

def on_snapshot(doc_snapshot, changes, read_time):
    for change in changes:
        if change.type.name == 'ADDED':
            on_new_quiz(change.document.id, change.document)

quizzes_ref.where('sentToUsers', '==', False).on_snapshot(on_snapshot)


app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('sender_id', required=True)
parser.add_argument('quiz_id', required=True)
parser.add_argument('answer', required=True)
parser.add_argument('covered_area', required=True)

class QuizHub(Resource):
    def answers_equal(self, a: str, b: str):
        return a.lower().strip() == b.lower().strip()

    def author_score(self, correct_subs: int, wrong_subs: int):
        """
        Returns a score in [-SCORE_K .. SCORE_K]
        If every submission is correct, the author's score is -SCORE_K
        If every submission is wrong, the score is SCORE_K
        """
        N = correct_subs + wrong_subs
        if N == 0:
            return 0
        return (wrong_subs - correct_subs) * SCORE_K / N

    def sender_score(self, covered_area: float):
        """
        Give a minimum of 3 points, and linearly interpolate up to SCORE_K
        """
        return round(3 + covered_area * (SCORE_K - 3))

    def post(self):
        args = parser.parse_args()
        sender_id = args['sender_id']
        quiz_id = args['quiz_id']
        answer = args['answer']
        covered_area = args['covered_area']
        logging.info(f'Received submission from {sender_id} on quiz {quiz_id} with answer {answer}, covered area {covered_area}')

        sender = users_ref.document(sender_id).get()
        if not sender.exists:
            logging.warning(f'Sender {sender_id} not found. Skipping submisson')
            return {"success": False}
        sender = sender.to_dict()

        quiz = quizzes_ref.document(quiz_id).get()
        if not quiz.exists:
            logging.warning(f'Quiz {quiz_id} not found. Skipping submission')
            return {"success": False}
        quiz = quiz.to_dict()
        author_id = quiz['authorId']

        author = users_ref.document(author_id).get()
        if not author.exists:
            logging.warning(f'Author {author_id} not found. Skipping submission')
            return {"success": False}
        author = author.to_dict()

        correct_subs = quiz.get('correctSubmissions', 0)
        wrong_subs = quiz.get('wrongSubmissions', 0)
        author_score_before = self.author_score(correct_subs, wrong_subs)

        correct = self.answers_equal(quiz['correctAnswer'], answer)
        sender_score_delta = 0

        if correct:
            correct_subs += 1
            sender_score_delta = self.sender_score(covered_area)
        else:
            wrong_subs += 1
            sender_score_delta = -self.sender_score(covered_area)

        author_score_after = self.author_score(correct_subs, wrong_subs)

        logging.info(f'Updating scores: sender ({sender_id}) {sender_score_delta:+}, author ({author_id} {author_score_after - author_score_before:+}')

        users_ref.document(sender_id).update({
            'score': Increment(sender_score_delta),
            'pendingChallenges': ArrayRemove([quiz_id])
        })
        users_ref.document(author_id).update({
            'score': Increment(author_score_after - author_score_before)
        })

        if correct:
            quizzes_ref.document(quiz_id).update({
                'correctSubmissions': Increment(1)
            })
        else:
            quizzes_ref.document(quiz_id).update({
                'wrongSubmissions': Increment(1)
            })

        return {
            "success": True,
            "result": correct,
            "score_obtained": sender_score_delta
        }

api.add_resource(QuizHub, '/submit')

if __name__ == '__main__':
    app.run('0.0.0.0', 5000)
