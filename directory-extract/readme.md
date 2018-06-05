# Directory Extract

## Requirements

```bash
$ serverless -v
1.26.1
```

## Configure AWS Profile

Configure a profile named `xebia`:
```bash
aws configure --profile xebia
```

## Provision

Fill secrets in `serverless-secrets.yml` using following syntax

```yaml
default: &default
  <<: *default
  DOMAIN: "..."

dev:
  <<: *default
  CREDENTIAL_KMS_KEY_ARN: "..."
  CREDENTIAL_BUCKET: "..."
  CREDENTIAL_KEY: "..."
  SERVICE_ACCOUNT_ID: "..."
  SERVICE_ACCOUNT_USER: "..."
  SERVICE_ACCOUNT_KEY_ALIAS: "..."
  SERVICE_ACCOUNT_KEY_PASSWORD: "..."
  STORE_BUCKET: "..."
  STORE_KEY: "..."
default: &default
  <<: *default
  DOMAIN: "..."
```

Build and deploy function:
```bash
AWS_PROFILE=xebia ./gradlew deploy
```
