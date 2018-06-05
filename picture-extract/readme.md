# Calendar Extract

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
    PARENT_FOLDER_ID: "..."
       
dev:
    <<: *default
    CREDENTIAL_KMS_KEY_ARN: "..."
    CREDENTIAL_BUCKET: "..."
    CREDENTIAL_KEY: "..."
    STORE_BUCKET: "..."
    STORE_KEY: "..."
```

Build and deploy function:
```bash
AWS_PROFILE=xebia ./gradlew deploy
```
