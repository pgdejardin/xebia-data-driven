# Xke Api

## Requirements

```bash
node -v
v8.10.0
```

```bash
npm -v
5.8.0
```

## Documentation

To run documentation

``` bash
npm run doc
```

## Installation

``` bash
npm install
```

## Deploy

Deploy to dev
``` bash
export AWS_PROFILE=dev-profile
npm run deploy
```

Deploy to prod
``` bash
export AWS_PROFILE=prod-profile
npm run deployProd
```

### Note

Due to a limitation of cloudformation to attach existing resource in a cloudformation template
we use serverless-plugin-existing-s3 plugin.

Following command is required for first deployment only.
```bash
# for dev
npm run deployS3

# for prod
npm run deployS3Prod
```
