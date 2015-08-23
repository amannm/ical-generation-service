FROM amannm/java8-docker-base
MAINTAINER Amann Malik "amannmalik@gmail.com"
ADD build/libs/ical-generation-service.jar /srv/ical-generation-service.jar
EXPOSE 8080
ENTRYPOINT java -Xmx128m -jar /srv/ical-generation-service.jar