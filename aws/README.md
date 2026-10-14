# AWS deployment

Recommended production shape:

React static container -> AWS Load Balancer -> EKS -> auth/account/transaction services
                                                     |
                                                     -> Amazon RDS for MySQL
                                                     -> CloudWatch / AWS X-Ray or OpenTelemetry

The repository includes Kubernetes manifests and an example `eksctl` configuration. Local Kubernetes can run MySQL in-cluster for learning; for AWS production, move the database to Amazon RDS for MySQL and keep database credentials in AWS Secrets Manager or External Secrets.

## 1. Create an EKS cluster

Update the region and node sizing in `cluster.yaml`, then run:

```bash
eksctl create cluster -f cluster.yaml
aws eks update-kubeconfig --region ap-south-1 --name nova-bank
kubectl get nodes
```

## 2. Create ECR repositories

```bash
aws ecr create-repository --repository-name nova-bank-auth --region ap-south-1
aws ecr create-repository --repository-name nova-bank-account --region ap-south-1
aws ecr create-repository --repository-name nova-bank-transaction --region ap-south-1
aws ecr create-repository --repository-name nova-bank-frontend --region ap-south-1
```

Authenticate Docker to ECR:

```bash
aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com
```

Build and push the images from the repository root:

```bash
docker build -f backend/auth-service/Dockerfile -t nova-bank-auth ./backend
docker build -f backend/account-service/Dockerfile -t nova-bank-account ./backend
docker build -f backend/transaction-service/Dockerfile -t nova-bank-transaction ./backend
docker build -t nova-bank-frontend ./frontend

docker tag nova-bank-auth:latest YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-auth:latest
docker tag nova-bank-account:latest YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-account:latest
docker tag nova-bank-transaction:latest YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-transaction:latest
docker tag nova-bank-frontend:latest YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-frontend:latest

docker push YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-auth:latest
docker push YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-account:latest
docker push YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-transaction:latest
docker push YOUR_AWS_ACCOUNT.dkr.ecr.ap-south-1.amazonaws.com/nova-bank-frontend:latest
```

Replace `YOUR_AWS_ACCOUNT` in `k8s/apps/*.yaml` and in the commands above.

## 3. Deploy to Kubernetes

```bash
kubectl apply -k k8s/
kubectl get pods -n nova-bank
kubectl get svc -n nova-bank
```

For the AWS load balancer address:

```bash
kubectl get svc frontend -n nova-bank -w
```

For a production RDS setup, update the three services to use the RDS endpoint instead of the `mysql` Kubernetes Service, and do not deploy `k8s/mysql/mysql.yaml`.
