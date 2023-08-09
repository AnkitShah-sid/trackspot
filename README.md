**Edit a file, create a new file, and clone from Bitbucket in under 2 minutes**

To Register a new emulator/device POST@'' http://64.226.101.239:8080/emulator/create ''
To fetch all emulators GET@'' http://64.226.101.239:8080/emulator ''

To log-in a user POST@'' http://64.226.101.239:8080/admin/log-in ''
To Sign-in a user POST@'' http://64.226.101.239:8080/admin/sign-in ''

#### Serving google maps using mongodb
For reference, use [This Article](https://medium.com/@simonskyau/serve-google-maps-using-spring-boot-and-mongodb-915f5feb4929)
#### Install MONGODB using
https://docs.mongodb.com/manual/administration/install-community/
##### open terminal and follow below commands to create root user :
```mongosh```

```use admin```

```
db.createUser(
    {
        user: "root",
        pwd: passwordPrompt(), // enter password on the prompt
        roles: [ { role: "root", db: "admin" } ]
    }
)
```
```db.adminCommand( { shutdown: 1 } )```

```exit```

```mongosh --port 27017  --authenticationDatabase "admin" -u "root" -p```

```db.createUser({ user: "root", pwd: "password", roles: [ { role: "root", db: "admin" } ] })```
