mvn clean install
web: java -Dserver.port=9090 -jar target/fractals-1.0-SNAPSHOT.jar -Xmx512m
heroku ps:scale web=1 -a fractal