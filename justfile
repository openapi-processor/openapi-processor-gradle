default:
  @just --list --unsorted

# update gradle wrapper
wrapper version="8.12":
    ./gradlew wrapper --gradle-version={{version}}

cache:
    ./gradlew --configuration-cache help
