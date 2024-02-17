spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=obdealcapture;trustServerCertificate=true
spring.datasource.username=dealcapture
spring.datasource.password=dealcapture
endpoints.health.enabled=true
logging.level.org.springframework=INFO
logging.level.com.onbelay=INFO
#logging.level.com.zaxxer.hikari=DEBUG
#logging.level.org.hibernate.SQL=DEBUG
#logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
#Log4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
endpoints.metrics.enabled=true
server.port=9101
server.servlet.context-path=/DealCapture
server.shutdown=graceful
batchInserterIsSqlServer=true
spring.application.name=DealCapture
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.main.allow-bean-definition-overriding=true
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.mvc.pathmatch.matching-strategy=ant_path_matcher

spring.security.oauth2.client.provider.okta.issuer-uri=http://localhost:8383/realms/master
spring.security.oauth2.client.provider.okta.token-uri=http://localhost:8383/realms/master/protocol/openid-connect/token
spring.security.oauth2.client.registration.okta.client-id=dealcapture
spring.security.oauth2.client.registration.okta.client-secret=FkN5Z0snfaN1sdwQgsfF6C9qSFm42QAu
spring.security.oauth2.client.registration.okta.scope=openid, profile, email
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8383/realms/master/protocol/openid-connect/certs

spring.profiles.active=messaging
spring.cloud.stream.bindings.out.destination=deal.generate.positions.request
spring.cloud.stream.bindings.out.contentType=application/json

spring.cloud.function.definition=organizationConsumer;generatePositionsRequestConsumer
spring.cloud.stream.rabbit.bindings.organizationConsumer-in-0.consumer.auto-bind-dlq=true
spring.cloud.stream.rabbit.bindings.organizationConsumer-in-0.consumer.quorum.enabled=true
spring.cloud.stream.rabbit.bindings.organizationConsumer-in-0.consumer.single-active-consumer=true
spring.cloud.stream.bindings.organizationConsumer-in-0.destination=org.organization.save
spring.cloud.stream.bindings.organizationConsumer-in-0.group=organization.dealcapture
spring.cloud.stream.bindings.organizationConsumer-in-0.consumer.default-retryable=false
spring.cloud.stream.bindings.organizationConsumer-in-0.consumer.retryable-exceptions.org.springframework.dao.NonTransientDataAccessResourceException=false
spring.cloud.stream.bindings.organizationConsumer-in-0.consumer.retryable-exceptions.org.springframework.dao.TransientDataAccessResourceException=true

spring.cloud.stream.rabbit.bindings.generatePositionsRequestConsumer-in-0.consumer.auto-bind-dlq=true
spring.cloud.stream.rabbit.bindings.generatePositionsRequestConsumer-in-0.consumer.quorum.enabled=true
spring.cloud.stream.rabbit.bindings.generatePositionsRequestConsumer-in-0.consumer.single-active-consumer=true
spring.cloud.stream.bindings.generatePositionsRequestConsumer-in-0.destination=deal.generate.positions.request
spring.cloud.stream.bindings.generatePositionsRequestConsumer-in-0.group=positions.dealcapture
spring.cloud.stream.bindings.generatePositionsRequestConsumer-in-0.consumer.default-retryable=false
spring.cloud.stream.bindings.generatePositionsRequestConsumer-in-0.consumer.retryable-exceptions.org.springframework.dao.NonTransientDataAccessResourceException=false
spring.cloud.stream.bindings.generatePositionsRequestConsumer-in-0.consumer.retryable-exceptions.org.springframework.dao.TransientDataAccessResourceException=true
