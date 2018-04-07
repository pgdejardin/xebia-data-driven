# Xke Transformer
##    This project have the responsibility to read xke data event and load it in (at now) dynamodb table ${env}-xke-xebia-data

## Requirements

```bash
$ serverless -v
1.26.1
```

## Installation
Install the Node packages

``` bash
$ npm install
```

## Deploy
### Note

    Due to the limitation of cloudformation to attach existing resource in a cloudformation template
    we use *serverless-plugin-existing-s3* plugin

we suppose you have export your AWS_PROFILE in terminal
```bash
serverless deploy

#just the first time to attach source s3 bucket
serverless s3deploy
```
    if you got this error:
    *Configuration is ambiguously defined. Cannot have overlapping suffixes in two rules if the prefixes are overlapping*
    is because the deletion of lambda doest recursively delele event so you have to do it.
    go to your *bucket -> properties ->Advanced settings -> event* and drop the previous one.
