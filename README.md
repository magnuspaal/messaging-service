### Social application

Simple messaging service for private messaging.

#### Environment arguments
```agsl
PORT=8083
ALLOWED_ORIGINS=
DB_PASSWORD=
DB_URL=
DB_USERNAME=
JWT_SECRET=
```

#### Release
* Run `./cicd/deploy/bump <version>`
* Push new commit and tag. GitHub Actions will deploy the container.