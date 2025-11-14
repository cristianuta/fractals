
# package
# web: mvn jpro:release

# config
# heroku config:set MAVEN_CUSTOM_GOALS="clean package jpro:release"
# heroku config:set MAVEN_CUSTOM_OPTS="--update-snapshots -DskipTests=true"
# heroku config:set MAVEN_JAVA_OPTS="-Xss2g"

# copy maven files
# for DIR in ".m2" ".maven" ; do
#  cp -r $CACHE_DIR/$DIR $BUILD_DIR/$DIR
# done

# run
# web: java -jar target/fractals-1.0-SNAPSHOT.jar -Djpro.port=9090 -Xmx512m

# start
web: ./app/target/fractals-jpro/bin/restart-background.sh

# scale up
# heroku ps:scale web=1 -a fractal

# build
# mvn clean package