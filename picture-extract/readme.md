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

## Deploy

Build and deploy function:
```bash
AWS_PROFILE=xebia ./gradlew deploy
```
