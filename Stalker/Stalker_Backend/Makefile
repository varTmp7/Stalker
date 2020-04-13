setting-up-env:
	export EMAIL_PASSWORD=$(echo -n "VHVsbGlvUnVsZWdnaWE=" | base64 -D)

clean-containers:
	docker-compose -f tests/docker-compose.yml down

run-tests-with-coverage: clean-containers
	docker-compose -f tests/docker-compose.yml up -d
	pytest --cov-report term --cov-report xml --cov=stalker_backend tests/
	docker-compose -f tests/docker-compose.yml down


run-tests: clean-containers
	docker-compose -f tests/docker-compose.yml up -d
	pytest tests/

run-tests-first-fail: clean-containers
	docker-compose -f tests/docker-compose.yml up -d
	pytest tests/ -x

start-backend: setting-up-env
	docker-compose up --build

start-backend-https: setting-up-env
	export LOCAL_HTTPS=1
	docker-compose up --build

