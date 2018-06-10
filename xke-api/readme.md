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

To run documentation

``` bash
npm run deploy
```

### Note
```
Due to a limitation of cloudformation to attach existing resource in a cloudformation template
we use serverless-plugin-existing-s3 plugin
```

Folowing command is required for first deployment only
```bash
npm run deployS3
```
