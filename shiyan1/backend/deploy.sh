#!/bin/bash
# 若依管理系统后端部署脚本
# 文件路径: backend/deploy.sh

APP_NAME="ruoyi-admin"
APP_DIR="/opt/ruoyi-admin"
JAR_NAME="${APP_NAME}-1.0.0.jar"
JAR_PATH="${APP_DIR}/${JAR_NAME}"
LOG_PATH="/var/log/ruoyi-admin"
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_PATH}/heapdump.hprof"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}===========================================${NC}"
echo -e "${YELLOW}  若依管理系统后端部署脚本${NC}"
echo -e "${YELLOW}===========================================${NC}"

case "$1" in
    start)
        echo -e "${GREEN}正在启动服务...${NC}"
        if [ ! -d "${LOG_PATH}" ]; then
            mkdir -p "${LOG_PATH}"
        fi
        nohup java ${JAVA_OPTS} -jar ${JAR_PATH} --spring.profiles.active=prod > ${LOG_PATH}/nohup.out 2>&1 &
        sleep 3
        if pgrep -f "${JAR_NAME}" > /dev/null; then
            echo -e "${GREEN}服务启动成功！${NC}"
        else
            echo -e "${RED}服务启动失败，请查看日志：${LOG_PATH}/nohup.out${NC}"
            exit 1
        fi
        ;;
    stop)
        echo -e "${GREEN}正在停止服务...${NC}"
        PID=$(pgrep -f "${JAR_NAME}")
        if [ -n "${PID}" ]; then
            kill -15 ${PID}
            sleep 5
            if pgrep -f "${JAR_NAME}" > /dev/null; then
                kill -9 ${PID}
            fi
            echo -e "${GREEN}服务已停止${NC}"
        else
            echo -e "${YELLOW}服务未运行${NC}"
        fi
        ;;
    restart)
        $0 stop
        sleep 2
        $0 start
        ;;
    status)
        if pgrep -f "${JAR_NAME}" > /dev/null; then
            echo -e "${GREEN}服务运行中${NC}"
        else
            echo -e "${RED}服务未运行${NC}"
        fi
        ;;
    deploy)
        echo -e "${GREEN}正在部署新版本...${NC}"
        $0 stop
        sleep 3
        if [ ! -d "${APP_DIR}" ]; then
            mkdir -p "${APP_DIR}"
        fi
        cp target/${JAR_NAME} ${APP_DIR}/
        $0 start
        ;;
    *)
        echo -e "${YELLOW}用法: $0 {start|stop|restart|status|deploy}${NC}"
        exit 1
        ;;
esac

exit 0