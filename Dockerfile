FROM openjdk:17-alpine

WORKDIR /user/app

ENV name alex

RUN apk update &&\
	apk add maven
	
COPY starwars-api-imperial .

CMD mvn spring-boot:run

EXPOSE 8080