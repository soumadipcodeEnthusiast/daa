.PHONY: build test run-backend run-frontend docker-up docker-down clean

build:
	cd backend && mvn clean package -DskipTests
	cd frontend && npm install && npm run build

test:
	cd backend && mvn test

run-backend:
	cd backend && mvn spring-boot:run

run-frontend:
	cd frontend && npm run dev

docker-up:
	docker compose up --build

docker-down:
	docker compose down

clean:
	cd backend && mvn clean
	cd frontend && rm -rf dist node_modules
