Outdated

Q: On Linux machine I get
```
com.github.dockerjava.api.exception.NotFoundException: Status 404: {"message":"No such container: 9de76c203bff65a225569e0e91afb3a200f0232e4db4e730548fe34b585dc2a6"
```

A: You need to disable SELinux:
```bash
su -
setenforce 0
```
