### 참고 SITE
* [sbt-microsites](https://github.com/47degrees/sbt-microsites)
* [mdoc](https://scalameta.org/mdoc/)

### docs project 사용
[sbt-microsites build](https://47degrees.github.io/sbt-microsites/docs/build-the-microsite/#view-the-microsite-in-your-browser) 를 참고하면
* sbt docs/makeMicrosite
* cd docs/target/site
* build.sbt 에서 micrositeBaseUrl=cats 로 설정된 것 확인후 아래와 같이 서버를 실행
  ```bash
  jekyll serve -b /cats --host=0.0.0.0
  ```
* 브라우저에서 아래 URL 로 접근 (마지막 slash 추가 필수)
  ```http request
  http://my-server-host:4000/cats/
  ```
