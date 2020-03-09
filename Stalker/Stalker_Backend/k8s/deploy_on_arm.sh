kubectl apply -f  postgres-pv.yml
kubectl apply -f  postgres-pvc.yml
kubectl apply -f  postgres-credentials.yml
kubectl apply -f  postgres-deployment.yml
kubectl apply -f  postgres-service.yml

kubectl apply -f  rethink-pv.yml
kubectl apply -f  rethink-pvc.yml
kubectl apply -f  rethink-deployment.yml
kubectl apply -f  rethink-service.yml

kubectl apply -f  stalker-deployment-arm.yml
kubectl apply -f  stalker-service.yml
