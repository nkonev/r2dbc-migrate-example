# Example of usage r2dbc-migrate

1. `docker-compose up -d`
2. Start CustomerApplication class in your IDE
3. `curl -v http://localhost:8383/customer`
4. Profit:
![Curl output](./.markdown/example.png)

# Further steps
* See [video](https://www.youtube.com/watch?v=t7oLx9RJkB8&feature=youtu.be)
* [Video](https://www.youtube.com/watch?v=xCu73WVg8Ps) about proper invocation blocking methods from the reactive code

# FAQ:
Q: On Linux machine I get
```
com.github.dockerjava.api.exception.NotFoundException: Status 404: {"message":"No such container: 9de76c203bff65a225569e0e91afb3a200f0232e4db4e730548fe34b585dc2a6"
```

A: You need to disable SELinux:
```bash
su -
setenforce 0
```
