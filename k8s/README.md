# Netco Kubernetes practice deployment

This is a local/practice deployment. Kafka and MySQL are single replicas; use managed, replicated services and TLS/authentication before exposing this setup outside a development cluster.

## Build images

From the repository root, build the four service jars, then their images:

```sh
mvn -pl inventory-service,order-service,notification-service,shipping-service -am package -DskipTests
docker build -f inventory-service/Dockerfile -t netco/inventory-service:local .
docker build -f order-service/Dockerfile -t netco/order-service:local .
docker build -f notification-service/Dockerfile -t netco/notification-service:local .
docker build -f shipping-service/Dockerfile -t netco/shipping-service:local .
```

For Minikube, load each image with `minikube image load netco/inventory-service:local` (repeat for the other images). For kind, use `kind load docker-image ...`.

## Run locally with Docker and Maven

Prerequisites: JDK 21, Maven, and Docker. Start Kafka and MySQL (MySQL is separate from the Kafka Compose file):

```sh
docker compose -f order-service/docker-compose.yaml up -d
docker run -d --name netco-mysql --restart unless-stopped \
  -e MYSQL_ROOT_PASSWORD=devroot -e MYSQL_DATABASE=Netco_microservices \
  -p 3308:3306 -v netco-mysql-data:/var/lib/mysql mysql:8.4
```

Run each app in its own terminal from the repo root. Set `SPRING_DATASOURCE_PASSWORD=devroot` for inventory, order, and shipping. The inventory service seeds `Alaska` (3 available) and `Terana` (0 available) at startup.

```sh
SPRING_DATASOURCE_PASSWORD=devroot mvn -pl inventory-service spring-boot:run
SPRING_DATASOURCE_PASSWORD=devroot mvn -pl order-service spring-boot:run
RESEND_API_KEY=re_your_key EMAIL_FROM=onboarding@resend.dev mvn -pl notification-service spring-boot:run
SPRING_DATASOURCE_PASSWORD=devroot SHIPPO_TEST_TOKEN=shippo_test_your_token \
  SHIP_FROM_STREET='417 Montgomery Street' SHIP_FROM_CITY='San Francisco' \
  SHIP_FROM_STATE=CA SHIP_FROM_ZIP=94104 SHIP_FROM_PHONE=4153334445 \
  mvn -pl shipping-service spring-boot:run
```

For a real email attempt, use an address permitted by your Resend account and a valid API key. Shippo's test token creates only test data. The Shippo origin above is a sample US address; replace it with your own valid Shippo test origin.

Submit an order and confirm that checkout returns before consumers finish:

```sh
curl -i http://localhost:8081/api/order -H 'Content-Type: application/json' -d '{
  "customerEmail":"your-permitted-recipient@example.com",
  "shippingAddress":{"name":"Test Buyer","street1":"179 N Harbor Dr","city":"Redondo Beach","state":"CA","zip":"90277","country":"US","phone":"8573875756"},
  "orderLineItemsDtoList":[{"skuCode":"Alaska","price":12.50,"quantity":1}]}'
```

Check each service's terminal for the email and Shippo shipment result. `Terana` is a useful negative check because inventory marks it out of stock. Actuator is available at `http://localhost:8081/actuator/health` and `/actuator/prometheus` (likewise ports 8083, 8084, and 8085).

To exercise outbox recovery locally, stop Kafka with `docker compose -f order-service/docker-compose.yaml stop kafka`, place an order, then start Kafka again. The order should still be accepted; after Kafka returns, the scheduled publisher drains the pending outbox event. Check order-service logs for publish retries and consumer logs after recovery.

## Free provider setup

1. Create a free Resend account and API key. Its current free plan advertises 3,000 emails/month and 100/day. Set `RESEND_API_KEY`. Use `onboarding@resend.dev` for Resend's test sender restrictions, or verify a sender domain and set `EMAIL_FROM` for permitted recipients.
2. Create a free Shippo account and generate a **test** API token (`shippo_test_...`). Set `SHIPPO_TEST_TOKEN` and the `SHIP_FROM_*` values. The service rejects live tokens, and only creates test shipment/rate records; it never purchases a label.
3. The checkout JSON must include `customerEmail` and a `shippingAddress` object with `name`, `street1`, `city`, `state`, `zip`, `country`, and `phone` as well as `orderLineItemsDtoList`.

Example request fields:

```json
{
  "customerEmail": "buyer@example.com",
  "shippingAddress": {"name":"Buyer","street1":"1 Main St","city":"San Francisco","state":"CA","zip":"94104","country":"US","phone":"4155550100"},
  "orderLineItemsDtoList": [{"skuCode":"SKU-1","price":12.50,"quantity":1}]
}
```

## Deploy

Copy `k8s/secrets.example.yaml` to `k8s/secrets.yaml`, replace every placeholder (use the same DB password for `SPRING_DATASOURCE_PASSWORD` and `MYSQL_ROOT_PASSWORD`), and keep that file out of version control. Then run:

```sh
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/config.yaml -f k8s/secrets.yaml
kubectl apply -f k8s/kafka.yaml -f k8s/mysql.yaml
kubectl -n netco rollout status statefulset/kafka --timeout=180s
kubectl -n netco rollout status statefulset/mysql --timeout=180s
kubectl apply -f k8s/apps.yaml
kubectl apply -f k8s/observability.yaml
```

Port-forward order service with `kubectl -n netco port-forward svc/order-service 8081:8081`. Prometheus is at node port 30090 and Grafana at 30030 (practice-only credentials `admin` / `practice-only`). Prometheus scrapes Actuator metrics from all annotated service pods; health/readiness/liveness probes use Actuator.

Wait for pods with `kubectl -n netco get pods -w`; inspect processing with `kubectl -n netco logs deploy/order-service`, `kubectl -n netco logs deploy/notification-service`, and `kubectl -n netco logs deploy/shipping-service`. With Minikube, open the UIs using `minikube service -n netco prometheus --url` and `minikube service -n netco grafana --url`; for clusters exposing NodePorts directly, use `<node-ip>:30090` and `<node-ip>:30030`. In Prometheus, check **Status → Targets** for the five application pods (order has two replicas), then query `http_server_requests_seconds_count` or `jvm_memory_used_bytes`. Grafana is provisioned with Prometheus as a datasource; use Explore to run those queries.

To test outbox recovery in Kubernetes, scale Kafka down, submit an order through the port-forward, and scale Kafka up again:

```sh
kubectl -n netco scale statefulset/kafka --replicas=0
# submit the curl request above while the order and inventory pods remain ready
kubectl -n netco scale statefulset/kafka --replicas=1
kubectl -n netco logs deploy/order-service -f
```

The outbox is stored in the order database and retries publication with capped exponential backoff. Kafka delivery is at least once; email uses Resend's idempotency key and shipping records processed order IDs in MySQL to ignore normal redelivery. A crash between a carrier API side effect and recording its result can still create a duplicate Shippo test shipment because remote APIs and local SQL cannot share a transaction.
