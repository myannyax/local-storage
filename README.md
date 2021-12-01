# local-storage

## Build
```
./gradlew installDist
./client/gradlew installDist
```
### Paths to executables:
#### Server:
build/install/local-storage/bin/local-storage
#### Client:
client/build/install/client/bin/client

## Notes
`./client 4242 10000000 put get`
works 42 minutes for me though :( (I hope it's because i use ktor and it does a lot of different stuff)
upd: without ktor logging it's 15 minutes
upd: works 7 minutes on other computer 😈
