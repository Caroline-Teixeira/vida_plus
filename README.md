<h1>Sistema de Gestão Hospitalar e de Serviços de Saúde (SGHSS) - VidaPlus</h1>
Repositório do SGHSS (Sistema de Gestão Hospitalar e de Serviços de Saúde), um projeto multidisciplinar desenvolvido para a disciplina de Projeto Multidisciplinar da UNINTER (2025). Este sistema foi criado para a instituição fictícia VidaPlus, com o objetivo de centralizar cadastros, atendimentos, telemedicina, e administração hospitalar, conforme o estudo de caso fornecido.
Sobre o Projeto: é um protótipo de back-end, utilizando Java Spring com arquitetura MVC e banco de dados MySQL 8. A API REST implementa as principais funcionalidades do sistema, atendendo aos requisitos funcionais e não funcionais do estudo de caso.

<h2>Funcionalidades:</h2>
Cadastro e gestão de pacientes (dados pessoais, prontuários).
Agendamento e cancelamento de consultas e cirurgias.
Gestão de profissionais de saúde (agendas).
Administração hospitalar (controle de leitos, relatórios de logs).


<h2>Tecnologias Utilizadas:</h2>

Back-end: Java 17, Spring Boot, Spring MVC, Spring Data JPA, Spring Security
<br>Banco de Dados: MySQL 8
<br>Ferramentas: Maven (gerenciamento de dependências), Postman (testes de API)

<h2>Configurações</h2>
<h5>Clone o repositório:</h5> 

```
git clone https://github.com/Caroline-Teixeira/vida_plus.git
```


<h5>Navegue até o diretório:</h5> 

```
cd vida_plus
```


<h5>Configure o banco de dados (MySQL):</h5
                                         
```
DATABASE hospital_vidaplus;
```

<h5>Atualize o arquivo application.properties para:</h5>

```
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_vidaplus
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

<h5>Compile e execute:</h5>

```
mvn clean install
mvn spring-boot:
```



<h2>Testes de API:</h2>
Acesse http://localhost:8080 (ou porta configurada). Use o Postman ou outra plataforma para testar endpoints.
Os seguintes links apresentam os testes realizados realizado para a aplicação: <br>
<br>https://youtu.be/gLvQzmj5r5g
<br>https://youtu.be/iD2cYpGLDQs
<br>https://youtu.be/xapQgoiTB_U

<h2>Configuração de Token automático - POSTMAN</h2>
Crie um ambiente chamado ‘Hospital VidaPlus’. Na sessão de login utilize o scritp:

```
pm.environment.unset("hospital-token");
console.log("Token removido da variável de ambiente");
```
Para as demais requisições: 
Na seção `Header`, crie uma key chamada `Authorization` e coloque no `Value` o seguinte valor: `Bearer {{hospital-token}}`.
O Token expira após 7 dias.

<h2>Usuários para Testes</h2>

| **PAPEL** | **EMAIL** | **SENHA** |
|-----------|-----------|-----------|
| ADMIN | fernanda.costa@example.com | fernanda022 |
| ADMIN | leon.s@example.com | senha123 |
| HEALTH_PROFESSIONAL | mariana.lopes@example.com | 123mariana |
| ATTENDANT | lucas.oliveira@example.com | lucas789 |
| ATTENDANT | sheila.s@example.com | 123senha |
| PATIENT | alice.costa@example.com | alice202 |
| PATIENT | clara.s@example.com | senha123 |

<h2>Documentação da API - Endpoints HTTP</h2>

## Método HTTP: POST

| URL | DESCRIÇÃO | PERMISSÕES |
|-----|-----------|------------|
| /auth/login | Permite acesso público a rotas de autenticação (ex.: login) | Todos (Sem Autenticação) |
| /auth/logout | Realiza o logout do usuário autenticado | Usuários Autenticados |
| /api/audit-records/filter | Filtra registros de auditoria (por usuários) | Admin |
| /api/users | Cria um novo usuário | Admin, Attendant |
| /api/appointments | Cria um novo agendamento | Admin, Attendant |
| /api/surgeries | Cria um novo registro de cirurgia | Admin, Attendant |
| /api/medical-records/{patientId}/add-observations | Adiciona observações de uma consulta ao prontuário | Admin, Health_Professional |
| /api/medical-records/{patientId}/add-surgery-observations | Adiciona observações de uma cirurgia ao prontuário | Admin, Health_Professional |

## Método HTTP: PUT

| URL | DESCRIÇÃO | PERMISSÕES |
|-----|-----------|------------|
| /api/users/{id} | Atualiza um usuário específico | Admin, Attendant |
| /api/appointments/{id} | Atualiza um agendamento específico | Admin, Attendant |
| /api/appointments/{id}/status | Atualiza o status de um agendamento | Admin, Attendant |
| /api/surgeries/{id} | Atualiza uma cirurgia específica | Admin, Attendant |
| /api/surgeries/{id}/status | Atualiza o status de uma cirurgia | Admin, Attendant |
| /api/medical-records/{patientId}/update-observations | Atualiza observações de uma consulta de um prontuário | Admin, Health_Professional |
| /api/medical-records/{patientId}/update-surgery-observations | Atualiza observações de uma cirurgia de um prontuário | Admin, Health_Professional |

## Método HTTP: GET

| URL | DESCRIÇÃO | PERMISSÕES |
|-----|-----------|------------|
| /api/users | Lista todos os usuários | Admin, Attendant |
| /api/users/{id} | Obtém detalhes de um usuário específico | Admin, Attendant |
| /api/users/current | Obtém dados do usuário autenticado | Todos (Usuário autenticado) |
| /api/appointments | Lista todos os agendamentos | Admin, Attendant |
| /api/appointments/{id} | Obtém detalhes de um agendamento específico | Admin, Attendant, Health_Professional |
| /api/appointments/current | Lista os agendamentos atuais do usuário | Todos (Usuário autenticado) |
| /api/appointments/patient/{patientId} | Lista agendamentos de um paciente específico | Admin, Attendant, Health_Professional |
| /api/appointments/healthProfessional/{healthProfessionalId} | Lista agendamentos de um profissional de saúde | Admin, Attendant, Health_Professional |
| /api/surgeries | Lista todas as cirurgias | Admin, Attendant |
| /api/surgeries/{id} | Obtém detalhes de uma cirurgia específica | Admin, Attendant, Health_Professional |
| /api/surgeries/current | Lista as cirurgias atuais do usuário | Todos (Usuário autenticado) |
| /api/surgeries/patient/{patientId} | Lista cirurgias de um paciente específico | Admin, Attendant, Health_Professional |
| /api/surgeries/healthProfessional/{healthProfessionalId} | Lista cirurgias de um profissional de saúde | Admin, Attendant, Health_Professional |
| /api/medical-records/current | Lista o prontuário do usuário atual | Todos (Usuário autenticado) |
| /api/medical-records/patient/{patientId} | Lista o prontuário de um paciente específico | Admin, Attendant, Health_Professional |
| /api/audit-records/all | Lista todos os registros de auditoria | Admin |
| /api/schedule/all-slots/{professionalId}/{date} | Lista todos os slots disponíveis de um profissional | Admin, attendant |
| /api/schedule/current/{date} | Obtém a agenda atual de uma data específica | Admin, attendant, health_professional |
| /api/hospitalizations/active | Lista internações ativas | Admin |
| /api/hospitalizations/available-beds | Lista leitos disponíveis | Admin |

## Método HTTP: DELETE

| URL | DESCRIÇÃO | PERMISSÕES |
|-----|-----------|------------|
| /api/users/{id} | Remove um usuário específico | Admin |
| /api/appointments/{id} | Remove um agendamento específico | Admin, Attendant |
| /api/surgeries/{id} | Remove uma cirurgia específica | Admin, Attendant |
| /api/medical-records/{patientId}/remove-observations | Remove observações de uma consulta no prontuário | Admin, Health_Professional |
| /api/medical-records/{patientId}/remove-surgery-observations | Remove observações de cirurgia no prontuário | Admin, Health_Professional |
| /api/medical-records/{patientId} | Remove um prontuário específico | Admin |


















