#!/bin/bash

cd /home/ubuntu/bookstore-deploy

#echo "Pulling latest image..."
#sudo docker compose pull
#
#echo  "down containers....."
#sudo docker compose down
#
#echo "Starting containers...."
#sudo docker compose up -d
#
#sudo docker image prune -f

# 1. 현재 떠 있는 컨테이너 확인 (blue가 켜져 있는지 확인)
IS_BLUE=$(docker ps -q -f name=app-blue)
IS_GREEN=$(docker ps -q -f name=app-green)

# 2. 배포 대상(Target) 결정
if [ -z "$IS_BLUE" ]; then
  #Blue가 없으면(Green이 켜져있거나 아예 없으면) -> Blue를 목표로 설정
  echo "### Target : BLUE (BLUE가 없거나 꺼져있음)  ###"
  TARGET_SERVICE="app-blue"
  STOP_SERVICE="app-green"
else
  # Blue가 켜져 있으면 -> Green 배포
  echo "### Target : Green (Blue가 켜져있음) ###"
  TARGET_SERVICE="app-green"
  STOP_SERVICE="app-blue"
fi

# 2. 새 이미지 Pull & 컨테이너 실행
echo ">>> 이미지 Pull: $TARGET_SERVICE"
docker compose pull $TARGET_SERVICE

# 3. Target 컨테이너 실행 (기존 컨테이너는 켜둔 채로)
echo ">> 컨테이너 실행: $TARGET_SERVICE"
docker compose up -d $TARGET_SERVICE

# 4. Health Check (단순 대기) / actuator가 붙어있으면 거기로 때린다
echo ">> Waiting for Health Check (20s)"
sleep 20

# 5. Nginx 설정 변경 (핵심!)
# 이전 설정이 app-blue든 app-green이든 상관없이, 무조건 현재 Target으로 덮어씀
NGINX_CONF="./nginx/conf.d/default.conf"
echo ">>> Updating Nginx upstream to $TARGET_SERVICE"

# [설명] server app-.*:8080; 패턴을 찾아서 server $TARGET_SERVICE:8080;으로 변경
# 이렇게 하면 초기 상태가 뭐든 상관없이 무조건 타겟으로 고정됨
sed -i "s/server app-.*:8080;/server $TARGET_SERVICE:8080;/g" $NGINX_CONF

# 6. Nginx가 켜져 있는지 확인 후 Reload 또는 Start
IS_NGINX=$(docker ps -q -f name=bookstore-nginx)

if [ -z "$IS_NGINX" ]; then
  echo ">>> Nginx is not running. Starting Nginx...."
  docker compose up -d nginx
else
  echo ">>> Nginx is running. Reloading..."
  docker exec bookstore-nginx -s reload
fi

# 7. 이전 컨테이너 중지 (켜져 있었을 경우에만)
if [ -n "$IS_GREEN" ] && [ "$TARGET_SERVICE" == "app-blue" ]; then
  echo ">>> Stopping Green .... "
  docker compose stop app-green
fi

if [ -n "$IS_BLUE" ] && [ "$TARGET_SERVICE" == "app-green" ]; then
  echo ">>> Stopping Blue..."
  docker compose stop app-blue
fi

echo "### 배포완료! Current Active: $TARGET_SERVICE ###"
