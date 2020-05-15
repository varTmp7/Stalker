setting-up-env:
	export EMAIL_PASSWORD=$(echo -n "VHVsbGlvUnVsZWdnaWE=" | base64 -D)
	export CLOUDINARY_API_SECRET=$(echo -n "MGVCeGZBUUhIU0FWQWh0MjktSmFJbFR3bGpR" | base64 -D)
	export APP_ID=$(echo -n "MDU0OWY4OTQtNjRlZS00MGM1LTgwNDUtYjAwZjZkNzBlZDRm" | base64 -D)
	export NOTIFICATION_AUTH_TOKEN=$(echo -n "TURFMFlUZGpZbVF0WkRkaFpDMDBaV0ZqTFRsbU9UQXRNVGswT1dZMk5tVTNOR0k1" | base64 -D)

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

