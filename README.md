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
Banco de Dados: MySQL 8
Ferramentas:
Maven (gerenciamento de dependências)
Postman (testes de API)

Clone o repositório:
git clone https://github.com/Caroline-Teixeira/vida_plus.git


Navegue até o diretório:
cd vida_plus


Configure o banco de dados:

Crie um banco MySQL chamado vidaplus:
CREATE DATABASE hospital_vidaplus;


Atualize o arquivo src/main/resources/application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_vidaplus
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update


Compile e execute:
mvn clean install
mvn spring-boot:run


API:

Acesse http://localhost:8080 (ou porta configurada).
Use o Postman para testar endpoints, ex.:
POST /auth/login
GET /api/users










