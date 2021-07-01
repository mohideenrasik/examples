./mvnw clean liquibase:update spring-boot:run > logs.txt &
echo $! > pid
