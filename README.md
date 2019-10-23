# Build
다음은 도커태크를 생성하지 말고 빌드합니다
```
mvn clean package -DskipDockerTag
```

# Docker Image로 실행하는 방법

데이터베이스 주소가 192.168.11.22 이고 포트가 2003 스키마가 WBOSANG
사용자 계정이 wfare 비밀번호가 1234일 경우 

```
docker run --name krud  -p 8080:8080  -e db.host=192.168.11.22
          -e db.port=2003 
          -e db.schema=WBOSANG
          -e db.user=wfare
         -e db.password=1234
         -e editConfig.schema=WFARE
        -e editConfig.packageName=com.koreanair -d  krud
```
