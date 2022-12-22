import logging
import sys
import argparse

import firebase_admin
from firebase_admin import credentials, firestore
from firebase_admin.firestore import ArrayUnion, Query

logging.basicConfig(level=logging.INFO, stream=sys.stdout)

parser = argparse.ArgumentParser()
parser.add_argument('credentials_file', help='Firebase credentials json file')
args = parser.parse_args()


cred = credentials.Certificate(args.credentials_file)
firebase_admin.initialize_app(cred)
db = firestore.client()

users_ref = db.collection('users')
quizzes_ref = db.collection('quizzes')


USERS_ABOVE = 3
USERS_BELOW = 3

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


import time
while True:
    time.sleep(1000)
