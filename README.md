#xCursion

# RUN INSTRUCTIONS

Run Backend:
run /root/src/main/java/com/example/backend/root/RootApplication.java

Run Frontend:<br>
cd /root/apps/frontend <br>
npm install (if needed) <br>
npm start

<br>
Notes:

Backend Connects to MySQL, to adjust the backend for your local server, goto /root/src/main/java/com/example/backend/root/control/Database.java and on line 57, you can adjust the url, username, and password to match that of your sql connection.

Backend runs on localhost:8080

Frontend runs on localhost:3000
