default:
  @just --list --unsorted

# update gradle wrapper
wrapper version="8.8":
    ./gradlew wrapper --gradle-version={{version}}
