
# package
web: mvn jpro:release

# config
heroku config:set MAVEN_CUSTOM_GOALS="clean package jpro:release"
heroku config:set MAVEN_CUSTOM_OPTS="--update-snapshots -DskipTests=true"
heroku config:set MAVEN_JAVA_OPTS="-Xss2g"

# run
web: java -jar target/fractals-1.0-SNAPSHOT.jar -Dserver.port=9090 -Xmx512m

# scale up
# heroku ps:scale web=1 -a fractal

# build
# mvn clean package