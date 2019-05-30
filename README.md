# **API RESTful de usuários + login**

# Introdução 
Aplicação que expõe uma API RESTful de criação de usuários com login

# Descrição do sistema
API REST hospedada no Heroku (https://restuser.herokuapp.com/)

Estão disponíveis os seguintes endpoint:

# ENDPOINTS

#### /signup 

Essa rota espera um usuário com os campos abaixo:

```json
{
	"email": "user34@pitang.com",
	"password": "123456",
	"firstName": "Anderson",
	"lastName": "Marques",
	"phones" : [
		{
		"number" : 988887888 ,
		"area_code" : 81 ,
		"country_code" : "+55"
		}
	]
}
````
Obs: O id do usuário pode ser um sequencial gerado pelo banco ou um id único.
- Responder o código de status HTTP apropriado
- Em caso de sucesso você deve retornar:
<br />token : token de acesso da API (JWT) com informações do usuário
cadastrado;

- Em caso de erro:
<br />E-mail existente [retornar um erro com a mensagem "E-mail already
exists"];
<br />Campos inválidos [retornar um erro com a mensagem "Invalid fields"];
<br />Campos não preenchidos [retornar um erro com a mensagem "Missing
fields"];

#### /signin 

Essa rota espera um objeto com os campos abaixo:

```json
{
	"email": "user@pitang.com",
	"password": "123456"
}
````
- Em caso de sucesso você deve retornar:
<br />token : token de acesso da API (JWT) com informaçÕes do usuário
logado;
- Em caso de erro:
<br />E-mail inexistente ou senha errada [retornar um erro com a mensagem "Invalid e-mail or password"];
<br />Campos não preenchidos [retornar um erro com a mensagem "Missing fields"];

#### /me 

-Essa rota espera o token da api (via header):
<br />Authorization [JWT Token]
- Em caso de sucesso você deve retornar:

```json
{
    "data": {
        "firstName": "Anderson",
        "lastName": "Marques",
        "email": "user2@pitang.com",
        "phones": [
            {
                "id": "1",
                "number": 988887888,
                "area_code": 81,
                "country_code": "+55"
            }
        ],
        "createdAt": "2019-05-30T03:29:08.595",
        "lastLogin": "2019-05-30T03:29:22.542"
    },
    "errors": []
}
````
- Em caso de erro:
<br />Token não enviado [retornar um erro com a mensagem "Unauthorized"];
<br />Token expirado [retornar um erro com a mensagem "Unauthorized - invalid session"];


#### /user (POST)
Este serviço permite CRIAR novos usuários (Apenas usuários com perfil ADMIN logados podem criar. Já está criado na base um usuário com esse perfil: email: admin@pitang.com e password:123456)


```json
{
	"email": "user@pitang.com",
	"password": "123456",
	"profile": "ROLE_USER"
}
````

#### /user (PUT) 
Este serviço permite ATUALIZAR novos usuários (Apenas usuários com perfil ADMIN logados podem atualizar. Já está criado na base um usuário com esse perfil: email: admin@pitang.com e password:123456)

```json
{
	"email": "user2@pitang.com",
	"password": "123456",
	"profile": "ROLE_USER"
}
````

#### /user/{id} (GET) 
Este serviço permite buscar um usuário pelo ID respondendo um HTTP GET (Apenas usuários com perfil ADMIN logados realizar consultas de usuários. Já está criado na base um usuário com esse perfil: email: admin@pitang.com e password:123456)

#### /user/{page}/{count} (GET)
Este serviço permite listar os usuários realizando paginação respondendo um HTTP GET (Apenas usuários com perfil ADMIN logados realizar consultas de usuários. Já está criado na base um usuário com esse perfil: email: admin@pitang.com e password:123456)

#### /user/{id} (DELETE)
Este serviço permite deletar um usuário pelo ID respondendo um HTTP DELETE (Apenas usuários com perfil ADMIN logado podem deletar. Já está criado na base um usuário com esse perfil: email: admin@pitang.com e password:123456)

# Composição da Solução
- Banco de dados H2 que sobe em tempo de execução
- Spring Boot
- Spring Data
- Spring Security
- JWT
- Lombok
- Junit
- Documentação da API com Swagger
- Maven
- Entre outros


# Detalhe da solução
- API Rest desenvolvida em Spring
- Necessário autenticação para consumir os serviços da API. Utilizado JWT.
  No endpoint de autenticação é necessário informar e-mail e password. Já estão criados por padrão dois usuários, um de perfil ADMIN que pode listar, criar, atualizar e remover outros usuários. E um de perfil USER, ele pode chamar: 
  * /signin
  * /signup
  * /me
- Como banco de dados está sendo utilizado o H2, banco que sobe em tempo de execução da aplicação: http://localhost:8888/h2/
  * Usuário: sa 
  * Password:
  * BD: userDB
- Documentação da API utilizando Swagger, disponível em: http://localhost:8888/doc/index.html
- Deploys da aplicação disponível no Heroku. Swagger: https://https://restuser.herokuapp.com/doc/index.html
- Collection utilizado no Postman disponível em: 
  * resources/postman/RestUser.postman_collection.json
