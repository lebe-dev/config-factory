# Config Factory

Pretty simple config files generator based on templates and profiles.

## Getting started

Example task: generate 17 virtual host configurations for nginx. 
Each configuration will proxy some java application for specified subdomain.

### 1. Create application config

```
cp config-factory.conf-example config-factory.conf
```

### 2. Define variable names

config-factory.conf:

```
config {
  # Global variable names
  variableNames = ["name", "appId"]

  outputFileFormat = "${name}.somedomain.com.conf"
}
```

### 3. Create template file

Create `nginx.template` file:

```
server {
    listen       80;
    server_name  ${name}.somedomain.com;

    charset utf8;

    location / {
        proxy_pass http://localhost:${appPort};
        proxy_set_header Host            $host;
        proxy_set_header X-Forwarded-For $remote_addr;
    }
}
```

We need two variables for our template:

- subdomain name
- application port

### 4. Create profiles:

Inside profiles directory create `.conf` files:

For example - demo-app profile: `profiles/demo-app.conf`:

```
profile {
  name = "demo-app"
  appPort = 4182
}
```

Example suzuki profile: `profiles/suzuki.conf`:

```
profile {
  name = "suzuki"
  appPort = 7958
}
```

Create rest profiles in the same way.

### 5. Run

Run tool:

```
java -jar config-factory.jar -t nginx.template
```

And check results inside `output` directory. The first file `demo-app.conf` will contain:

```
 server {
     listen       80;
     server_name  demo-app.somedomain.com;
 
     charset utf8;
 
     location / {
         proxy_pass http://localhost:4182;
         proxy_set_header Host            $host;
         proxy_set_header X-Forwarded-For $remote_addr;
     }
 }
```

## Configuration

Config factory looks for `config-factory.conf` file in work directory. Use sample file `config-factory.conf-example`.

### Variable names and scope

Property `variableNames` contain global variable names. You can add additional variable names inside profiles.

Example:

File `config-factory.conf`:
```
config {
     # visible everywhere
     variableNames = ["subDomainName", "appPort"]
}
```

File `someprofile.conf`:
```
profile {
     # visible only for current profile
     variableNames = ["surname", "age"]
}
```

### Output files format

You can specify output file names format with `outputFileFormat` property. It supports template variables too.

## How it works

1. Reads main configuration and global variables from `config-factory.conf`, then read profiles from `profiles/*.conf`.
2. Override variables with same names.
3. In loop: get profile, override global variables 
4. Substitutes variables inside template
5. Create output file with specified format

### Profiles

Profile - plain text file with variables. It should be placed inside `profiles/` directory with `*.conf` extension.

Example:

```
profile {
    "name" = "cookie",
    "domain" = "",
    "portNumber" = 12345,
    "whatever" = "somevalue"
}
```

### Templates

Template - plain text file. Insert variables with following syntax:

```
${variableName}
```

## How to build a project

How to build project from sources

**Linux\MacOS:**

```
gradlew clean build
```

**Windows:**

```
gradlew.bat clean build
```

