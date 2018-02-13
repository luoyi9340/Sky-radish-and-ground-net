#   run server
nohup java -Xms64m -Xmx64m -XX:MaxPermSize=64m -XX:MaxDirectMemorySize=64m -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:CMSInitiatingOccupancyFraction=70 -jar sentry-group.jar > ./nohup.out &
