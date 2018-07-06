# Picture Extract

## Requirements

Install [Serverless](https://serverless.com/framework/docs/getting-started/):
```bash
$ serverless -v
1.28.0
```

Configure a profile for dev and prod:
```bash
aws configure --profile xebia
aws configure --profile xebia-prod
```

## Build & deploy
```bash
./gradlew buildLambda
```

## Deploy

Deploy to dev
```bash
AWS_PROFILE=xebia sls deploy
```

Deploy to prod
```bash
AWS_PROFILE=xebia-prod sls deploy --stage prod
```
