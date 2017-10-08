# money-transfer-with-play-akka-macwire
Small pet project for RESTful money transfer app built with Play, Akka and MacWire.

## App details
API is built with Play Framework
Domain logic is built with Akka actors framework (Bank and BankAccount)
Akka and Play are linked together with MacWire DI framework.

### API

next API is supported:
```
 /account                           { "accountName": "aa" }                         	Content-Type: application/json
 /account/:id/deposit               { "amount":  200 }                              	Content-Type: application/json
 /account/:id/withdraw              { "amount":  10 }                               	Content-Type: application/json
 /account/:id/transfer-money        { "destinationAccountId": 2, "amount":  100 }   	Content-Type: application/json

GET
 /account/:id                      
```

#### Error Handling
Proper error handling is not implemented, e.g. for scenarios when account does not exist but operation for specific account id is requested. 


### Distribution
In order to build executables use: `sbt dist` - it will generate platform specific archive with application content. 
E.g. for windows:

```
├───bin
	└───money-transfer-with-play-akka-macwire.bat
	└─── ...
├───conf
	└─── ...
├───lib
	└─── ...
└───share
    └───doc
        └───...
```

Running money-transfer-with-play-akka-macwire.bat will launch webserver on port 9000.


### Testing
There are couple unit tests to verify Bank and Bank Account Actors work and integration tests to verify overall app with API works.

Use `sbt test` to run tests
