# pgloader

pgloader is a data loading utility for migrating data from various databases into PostgreSQL. This guide provides steps to clone, build, and use pgloader for migrating a MySQL database to PostgreSQL.

## Prerequisites

Before installing pgloader, ensure the following dependencies are installed on your system:
  
- **SBCL (Steel Bank Common Lisp)**
- **Unzip**
- **libsqlite3-dev**
- **Make**
- **Curl**
- **Gawk**
- **freetds-dev**
- **libzip-dev**

You can install these using apt:

```
sudo apt-get install sbcl unzip libsqlite3-dev make curl gawk freetds-dev libzip-dev
```

## Installation

Clone the pgloader repository:
```
git clone https://github.com/dimitri/pgloader.git
```
Navigate to the pgloader directory:
```
cd /path/to/pgloader
```
Build the project:
```
make
```

## Usage

After building, you can access pgloader from the build directory.

To view the available options and commands:

```
./build/bin/pgloader --help
```
To migrate a MySQL database to PostgreSQL, use the following command:
```
./build/bin/pgloader mysql://<user>:<pass>@<host>:<port>/<database> postgresql://<user>:<pass>@<host>:<port>/<database>
```
Replace <user>, <pass>, <host>, <port>, and <database> with your MySQL and PostgreSQL credentials.

### Example

To migrate a MySQL database mydb from a host localhost (port 3306) to a PostgreSQL database mydb on the same host (port 5432):
```
./build/bin/pgloader mysql://root:password@localhost:3306/mydb postgresql://postgres:password@localhost:5432/mydb
```

## Troubleshooting

If you get a ```QMYND:MYSQL-UNSUPPORTED-AUTHENTICATION``` or ```fell through ECASE expression``` error

you need to switch your ```mysqld``` into using ```mysql_native_password``` as a default

Edit your ```my.cnf``` and in ```[mysqld]``` section add:
(if [mysqld] doesnt exist, create)

```
default-authentication-plugin=mysql_native_password
```
it will look like this:
![image](https://github.com/user-attachments/assets/707c399d-7a19-42d3-a298-05b7074edfd1)


Then you need to update your user's password to mysql_native_password type like this:
```
ALTER USER 'youruser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'yourpassword';
```
