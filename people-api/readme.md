# People Api

## Requirements

```bash
node -v
v8.10.0
```

```bash
npm -v
5.8.0
```

## Test

Launch tests
``` bash
npm test
```

Launch tests in TDD mode
``` bash
npm run tdd
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
