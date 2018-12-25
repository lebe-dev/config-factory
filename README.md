# Config Factory

Pretty simple config files generator based on templates and profiles.

## How to use

For example - we have task to generate 17 virtual host configurations for nginx. 
Each configuration will proxy some java application for specified subdomain.

Nginx config example:

```
server {
    listen       80;
    server_name  some-app.somedomain.com;

    charset utf8;

    location / {
        proxy_pass http://localhost:12345;
        proxy_set_header Host            $host;
        proxy_set_header X-Forwarded-For $remote_addr;
    }
}
```

We need two variables for our template:

- subdomain name
- application port

### 1. Create template file

Let's create nginx template file `nginx.template`:

```
server {
    listen       80;
    server_name  ${subDomainName}.somedomain.com;

    charset utf8;

    location / {
        proxy_pass http://localhost:${appPort};
        proxy_set_header Host            $host;
        proxy_set_header X-Forwarded-For $remote_addr;
    }
}
```

### 2. Define variable names in configuration file

Write variable names inside `config-factory.conf` file.

Example:

```
   variableNames = ["subDomainName", "appPort"]
```

### 2. Create profiles

Profiles contain variables which will be substituted in template.

Let's create few profiles for our example with nginx:

```
 profile {
   subDomainName = "demo-app"
   appPort = 8312
 }
```

```
 profile {
   subDomainName = "tutorial"
   appPort = 3928
 }
```

...

All profile should be placed inside `profiles` directory. One profile per file.

### 3. Run

Run tool:

```
java -jar config-factory.jar -t nginx.template
```

And check `output` directory. The first file `demo-app.conf` will contain:

```
 server {
     listen       80;
     server_name  demo-app.somedomain.com;
 
     charset utf8;
 
     location / {
         proxy_pass http://localhost:8312;
         proxy_set_header Host            $host;
         proxy_set_header X-Forwarded-For $remote_addr;
     }
 }
```

## Profiles

Profile - plain text file with defined variables. It should be placed inside `profiles/` directory with `*.conf` extension.

Example:

```
profile {
    "name" = "cookie",
    "domain" = "",
    "portNumber" = 12345,
    "whatever" = "somevalue"
}
```

## Templates

Insert variables with following syntax:

```
${variableName}
```

## Configuration

Config factory looks for `config-factory.conf` file in work directory. Use sample file `config-factory.conf-example`.

### Output files format

You can specify output file names format with `outputFileFormat` property. It supports template variables too.

## Examples

Check `profiles` directory and `nginx.template-example` file.

## How to build project

How to build project from sources

**Linux\MacOS:**

```
gradlew clean build
```

**Windows:**

```
gradlew.bat clean build
```

