# Start ExplorViz Backend in version 1.5.0
# Monitored applications can be connected

# Environment Variables are based on the
# properties file of each service, e.g,
# https://github.com/ExplorViz/explorviz-backend/blob/dev-1/user-service/src/main/resources/explorviz.properties

# Modified for ExplorViz as a Service:
# - Removed discovery service because it is not useful for us
# - Only publish the ports we need, in order to avoid any conflicts on the host
# - Remove all volumes because we don't want persistent data
# - Add placeholder variables
# - Removed fixed container_names

# TODO: Fix broken traefik reverse proxy with multiple instances

version: "3.3"
services:

  ### ExplorViz Services ###

  user-service:
    image: explorviz/explorviz-backend-user-service:1.5.0
    expose:
      - "8082"
    depends_on:
      - mongo-user
      - kafka
      - traefik
    environment:
      - MONGO_HOST=mongo-user
      - EXCHANGE_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    labels:
      - "traefik.enable=true"
      - "traefik.port=8082"
      - "traefik.http.routers.user-service.rule=PathPrefix(`/v1/tokens`) || PathPrefix(`/v1/users`) || PathPrefix(`/v1/roles`) || PathPrefix(`/v1/userbatch`)"

  settings-service:
    image: explorviz/explorviz-backend-settings-service:1.5.0
    expose:
      - "8087"
    depends_on:
      - mongo-settings
      - kafka
      - traefik
    environment:
      - MONGO_HOST=mongo-settings
      - EXCHANGE_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    labels:
      - "traefik.enable=true"
      - "traefik.port=8087"
      - "traefik.http.routers.settings-service.rule=PathPrefix(`/v1/settings`) || PathPrefix(`/v1/preferences`)"

  landscape-service:
    image: explorviz/explorviz-backend-landscape-service:1.5.0
# EaaS: Disable publishing of port
#    ports:
#      - "10135:10135"
    expose:
      - "10135"
    depends_on:
      - kafka
    environment:
      - EXCHANGE_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - EXCHANGE_KAFKA_TOPIC_NAME=landscape-update

  broadcast-service:
    image: explorviz/explorviz-backend-broadcast-service:1.5.0
    expose:
      - "8081"
    depends_on:
      - kafka
      - traefik
    environment:
      - EXCHANGE_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - EXCHANGE_KAFKA_TOPIC_NAME=landscape-update
    labels:
      - "traefik.enable=true"
      - "traefik.port=8081"
      - "traefik.http.routers.broadcast-service.rule=PathPrefix(`/v1/landscapes/broadcast`)"
      - "traefik.http.routers.broadcast-service.priority=200"

  history-service:
    image: explorviz/explorviz-backend-history-service:1.5.0
    expose:
      - "8086"
    depends_on:
      - mongo-history
      - kafka
      - traefik
    environment:
      - MONGO_HOST=mongo-history
      - EXCHANGE_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - EXCHANGE_KAFKA_TOPIC_NAME=landscape-update
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.history-service.rule=PathPrefix(`/v1/landscapes`) || PathPrefix(`/v1/timestamps`) "
      - "traefik.port=8086"

  analysis-service:
    image: explorviz/explorviz-backend-analysis-service:1.5.0
# EaaS: Changed from ports to expose
#    ports:
#      - "10133:10133"
    expose:
      - "10133"

# EaaS: Remove discovery-service
#  discovery-service:
#    image: explorviz/explorviz-backend-discovery-service:1.5.0
#    ports:
#      - "8083:8083"
#    depends_on:
#      - traefik
#    labels:
#      - "traefik.enable=true"
#      - "traefik.port=8083"
#      - "traefik.http.routers.discovery-service.rule=PathPrefix(`/v1/agents`)"

  frontend:
    image: explorviz/explorviz-frontend:1.5.0
    ports:
      - "%FRONTEND_PORT%:80"
    environment:
      - API_ROOT=http://traefik:8080
      - FRONTEND_IP=%ACCESS_URL%
      # Change localhost to your host ip adress, if you want to connect
      # from remote devices
    depends_on:
# EaaS: Remove discovery-service
#      - discovery-service
      - landscape-service
      - analysis-service
      - user-service
      - settings-service
      - history-service

  ### Software Stack ###

  traefik:
    image: "traefik:v2.0.1"
    command:
      - "--entrypoints.web.address=:8080"
      - "--providers.docker=true"
      - "--providers.docker.exposedbydefault=false"
# EaaS: Changed from ports to expose
#    ports:
#      - "8080:8080"
    expose:
        - "8080"
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock:ro"

  zookeeper:
    image: wurstmeister/zookeeper
    expose:
      - "2181"

  kafka:
    image: wurstmeister/kafka
    depends_on:
      - zookeeper
    expose:
      - "9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: kafka
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

  mongo-user:
    image: mongo
    command: mongod --port 27017
# EaaS: No persistent volumes
#    volumes:
#      - explorviz-user-mongo-data:/data/db
#      - explorviz-user-mongo-configdb:/data/configdb
    expose:
      - "27017"

  mongo-history:
    image: mongo
    command: mongod --port 27018
# EaaS: No persistent volumes
#    volumes:
#      - explorviz-landscape-mongo-data:/data/db
#      - explorviz-landscape-mongo-configdb:/data/configdb
    expose:
      - "27018"

  mongo-settings:
    image: mongo
    command: mongod --port 27019
# EaaS: Changed from ports to expose
#    ports:
#      - 27019:27019
    expose:
       - "27019"
# EaaS: No persistent volumes
#    volumes:
#      - explorviz-settings-mongo-data:/data/db
#      - explorviz-settings-mongo-configdb:/data/configdb

# EaaS: Add the application we want to test
  application:
    image: %APPLICATION_IMAGE%

# EaaS: No persistent volumes
#volumes:
#  explorviz-user-mongo-data:
#  explorviz-user-mongo-configdb:
#  explorviz-landscape-mongo-data:
#  explorviz-landscape-mongo-configdb:
#  explorviz-settings-mongo-data:
#  explorviz-settings-mongo-configdb:
