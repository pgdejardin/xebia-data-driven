# Infra

## Requirements

```bash
$ terragrunt -v
terragrunt version v0.14.0

$ terraform -v
Terraform v0.11.3
```

## Configure AWS Profile

Configure a profile named `xebia`:
```bash
aws configure --profile xebia
```

## Provision

Plan to provision `some-stack` on `some-stage`:
```bash
cd stage/some-stage/some-stack
AWS_PROFILE=xebia terragrunt plan
```

Do provision `some-stack` on `some-stage`:
```bash
cd stage/some-stage/some-stack
AWS_PROFILE=xebia terragrunt apply
```
