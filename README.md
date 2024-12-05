This is a mock server for validating the oauth2 client based in the other repo [here](https://github.com/krnbr/spring-oauth2-rest-client)

This one is for mocking the oauth2 token endpoint and a mock resource endpoint /ping

Oauth2 Token endpoint is mainly focused for the client credentials grant type

**These are the mocked endpoint details:-**

| Endpoint Name         | Method      | Route                                                                                      | Response                                                                                                                                                 | 
|-----------------------|-------------|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Oauth2 Token Endpoint | <b>POST</b> | [/oauth2/v2/token](src/main/java/in/neuw/mocks/controllers/Oauth2TokenMockController.java) | <pre lang="json">{<br/>    "access_token": "01939670-ba6e-7805-bf80-9a8b657b5c7d",<br/>    "expires_in": 300,<br/>    "token_type": "Bearer"<br/>}</pre> |
|                       |             |                                                                                            |                                                                                                                                                          |
| Resource Endpoint     | <b>GET</b>  | [/ping](src/main/java/in/neuw/mocks/controllers/ResourceController.java)                   | <pre lang="json">{<br/>    "status":"pong"<br>}</pre>                                                                                                    |
|                       |             |                                                                                            |                                                                                                                                                          |

The server runs in two ports https 8453 and https 8443 with Client Auth mode at the same time<br>

Why two ports? To mimic a different set of config(mtls / non mtls) for oauth2 token endpoint and resources endpoint

8453 is easy and straight forward to consume.

While 8443 is needed to mock enable the client auth aka mutual TLS!

It makes use of the same SSL config when running on https for the 8453, but if you want it run on http port, just provide the properties like below:-

```properties
server.second.secured=false
server.second.server.port=8080
```

This project can work in multiple set of configurations. One can tweak them accordingly.