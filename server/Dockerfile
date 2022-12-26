FROM python:3.10-alpine

WORKDIR /srv
COPY requirements.txt .
COPY server.py .

RUN pip install -r requirements.txt

EXPOSE 5000
ENTRYPOINT [ "python3", "server.py", "./animalx-1-firebase.json" ]
