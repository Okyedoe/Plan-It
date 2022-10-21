# run_new_was.sh

#!/bin/bash

CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 9001 ]; then
  TARGET_PORT=9003  # 9001 -> 9003
elif [ ${CURRENT_PORT} -eq 9003 ]; then
  TARGET_PORT=9001 # 9003-> 9001
else
  echo "> No WAS is connected to nginx"
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill WAS running at ${TARGET_PORT}."
  sudo kill ${TARGET_PID}
fi

nohup java -jar -Dserver.port=${TARGET_PORT} /var/www/dev_planet/cicd_template/build/libs/demo-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0