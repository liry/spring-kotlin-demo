# spring-kotlin-demo
Based on [Bootiful Kotlin by Josh Long](https://www.youtube.com/watch?v=SlBRce-aBOc).

## How-To
* Run: `mvn spring-boot:run`
* Get customers: `curl localhost:8080/customers`
* Post new customer: `curl localhost:8080/customers -X POST --header "Content-type: application/json" -d '{"name":"Tester"}'`