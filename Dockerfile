# ベースイメージの設定
FROM openjdk:21-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# Gradle依存関係とコードをコピー
COPY . .

# ポートの公開
EXPOSE 8080  
# Spring Bootのデフォルトポートを指定

# アプリケーションを実行
CMD ["java", "-jar", "build/libs/postApp-0.0.1-SNAPSHOT.jar"]
