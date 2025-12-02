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

# [추가] 헬스체크를 수행하려면 Nginx 컨테이너가 살아있어야 함 (curl 도구 셔틀)
echo ">>> Checking if Nginx is running"
IS_NGINX=$(docker ps -q -f name=bookstore-nginx)

if [ -z "$IS_NGINX" ]; then
  echo ">>> Nginx is not running. Starting Nginx for health check..."
  docker compose up -d nginx
fi

# 4. Health Check (단순 대기) / actuator가 붙어있으면 거기로 때린다
#echo ">> Waiting for Health Check (20s)"
#sleep 20

# 4. 서비스가 뜨는지 확실하게 확인
echo ">>> 🐢 $TARGET_SERVICE 가 완전히 뜰 때까지 Health Check 시작..."

# 4-1. 반복문으로 최대 10번 확인 (10초 간격 = 총 100초 대기)
# t3.medium 성능 고려해서 넉넉히 잡음
for RETRY_COUNT in {1..10}
do
  echo ">> 시도 ($RETRY_COUNT/10)..."

  # 4-2. curl로 실제로 200 OK가 오는지 찔러봄 (Nginx 컨테이너 안에서 찌르는 게 가장 확실함)
  # 아직 안 떴으면 에러가 나거나 000이 뜸
  RESPONSE=$(docker exec bookstore-nginx curl -s -o /dev/null -w "%{http_code}" http://$TARGET_SERVICE:8080)

  if [ "$RESPONSE" = "200" ]; then
    ehco ">>> $TARGET_SERVICE 구동 완료! (Status: 200)"
    break
  else
    echo ">>> 아직 응답 없음 (Status: $RESPONSE). 10초 대기..."
    sleep 10
  fi
done

# 4-3. 10번 다 했는데도 200이 안나오면 배포 실패처리
if [ "$RESPONSE" != "200" ]; then
  echo ">>> 에러: $TARGET_SERVICE 가 정상적으로 뜨지 않았습니다. 배포를 중단합니다."
  exit 1
fi

# 5. Nginx 설정 변경 (핵심!)
# 이전 설정이 app-blue든 app-green이든 상관없이, 무조건 현재 Target으로 덮어씀
#NGINX_CONF="./nginx/conf.d/default.conf"
#echo ">>> Updating Nginx upstream to $TARGET_SERVICE"

# [설명] server app-.*:8080; 패턴을 찾아서 server $TARGET_SERVICE:8080;으로 변경
# 이렇게 하면 초기 상태가 뭐든 상관없이 무조건 타겟으로 고정됨
#sed -i "s/server app-.*:8080;/server $TARGET_SERVICE:8080;/g" $NGINX_CONF

# nginx는 기본적으로 conf.d/*.conf로 되어 있는 파일을 모두 읽어들임
# 따라서 templates로 blue,green.conf 파일 격리가 필요함.
# templates 폴더에서 가져와서 conf.d/default.conf로 덮어씌움.
TEMPLATE_PATH="./nginx/templates"
TARGET_PATH="./nginx/conf.d/default.conf"

echo ">>> Updating Nginx config (Copying file) ... "

if [ "$TARGET_SERVICE" == "app-blue" ]; then
  # Blue 설정 파일을 default.conf로 강제 복사(-f)
  cp -f "$TEMPLATE_PATH/blue.conf" "$TARGET_PATH"
else
  # Green 설정 파일을 default.conf로 강제 복사 (-f)
  cp -f "$TEMPLATE_PATH/green.conf" "$TARGET_PATH"
fi

# 6. Nginx가 켜져 있는지 확인 후 Reload 또는 Start
IS_NGINX=$(docker ps -q -f name=bookstore-nginx)

if [ -z "$IS_NGINX" ]; then
  echo ">>> Nginx is not running. Starting Nginx...."
  docker compose up -d nginx
else
  echo ">>> Nginx is running. Reloading..."
  docker exec bookstore-nginx nginx -s reload
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
