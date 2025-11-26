#!/bin/bash

cd /home/ubuntu/bookstore-deploy

echo "Pulling latest image..."
sudo docker compose pull

echo "Starting containers...."
sudo docker compose up -d

sudo docker image prune -f