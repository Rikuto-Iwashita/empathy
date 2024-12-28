# ベースイメージとしてOpenJDK 21を使用
FROM openjdk:21-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# Gradleラッパーと関連ファイルをコピーして依存関係を事前にダウンロード
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradleの依存関係をダウンロード（キャッシュを活用）
RUN ./gradlew build --no-daemon || return 0

# プロジェクト全体をコピー
COPY . .

# プロジェクトをビルド
RUN ./gradlew build --no-daemon

# アプリケーションを実行
CMD ["java", "-jar", "build/libs/postApp-0.0.1-SNAPSHOT.jar"]
