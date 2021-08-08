Remote file control manager
=============

:rocket: Purpose of program

Control file system in client PC. The server provide console for control client which connected with server.

So this project consist of Server, Client, Console.

:factory: Program structure
-------------

### Technology set

__Server__

|Technical Name|Value|
|:---:|:---:|
|Language|Java|
|Framework|Spring boot|
|DBMS|Postgres|
|Network|Asynchronous server socket channel|
<br/>

__Client__

|Technical Name|Value|
|:---:|:---:|
|Language|Java|
|Network|Asynchronous server socket channel|
<br/>

__Console__

|Technical Name|Value|
|:---:|:---:|
|Language|JavaScript|
|Framework|React|
|State Management|Redux|
|Network|Websocket|
<br/>

### Demo Screenshot

![image](https://user-images.githubusercontent.com/23304953/128632003-108e02ac-dc9a-4378-bce6-84492fd9a4cd.png)

### Flow of this project

![image](https://user-images.githubusercontent.com/23304953/128631889-16ff2504-d938-4ab7-957e-8de026ca067e.png)

### Socket protocol

![image](https://user-images.githubusercontent.com/23304953/128631924-319b7388-255b-4a1f-b70e-ef835e8b9583.png)

### Structure of `Asynchronous server socket channel` for multiple times byte transmit.

![image](https://user-images.githubusercontent.com/23304953/128631978-a2b9ff0a-1bd8-4907-9c45-57054b92f27b.png)
