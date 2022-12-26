FROM python:3.10-alpine

RUN addgroup quizx && \
    adduser -G quizx --shell /bin/false --disabled-password quizx && \
    mkdir -p /srv/quizx && \
    chown quizx:quizx /srv/quizx

USER quizx

WORKDIR /srv/quizx
COPY requirements.txt .
COPY server.py .

RUN pip install -r requirements.txt

EXPOSE 5000
ENTRYPOINT [ "python3", "server.py", "./animalx-1-firebase.json" ]
