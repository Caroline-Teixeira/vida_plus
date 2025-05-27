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
git clone https://github.com/Caroline-Teixeira/vida_plus.git
<br>

<h5>Navegue até o diretório:</h5> 
cd vida_plus
<br>

<h5>Configure o banco de dados (MySQL):</h5> 
DATABASE hospital_vidaplus;
<br>

<h5>Atualize o arquivo application.properties para:</h5>
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_vidaplus
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
<br>

<h5>Compile e execute:</h5>
mvn clean install
mvn spring-boot:
<br>


<h2>Testes de API:</h2>
Acesse http://localhost:8080 (ou porta configurada). Use o Postman ou outra plataforma para testar endpoints.
Os seguintes links apresentam os testes realizados realizado para a aplicação:
<br>https://youtu.be/gLvQzmj5r5g
<br>https://youtu.be/iD2cYpGLDQs
<br>https://youtu.be/xapQgoiTB_U

![image](https://github.com/user-attachments/assets/6d1458c7-ac03-48e8-ba24-9e142cbf0f70)
![image](https://github.com/user-attachments/assets/a5e8d5d7-68f7-42ef-a59f-96e68b238279)
![image](https://github.com/user-attachments/assets/a791ff79-f912-40f4-b9d7-ae66a8a38f70)
![image](https://github.com/user-attachments/assets/0ed69f6e-d4c6-4546-a753-b3dae10535d5)
![image](https://github.com/user-attachments/assets/f3309682-cd98-4a6b-900c-691417444e60)
<br>![image](https://github.com/user-attachments/assets/2a8dae27-852c-41a5-a00b-e8d6dab3bfcb)
![image](https://github.com/user-attachments/assets/756b733e-deb6-4a44-a3e6-740f8d16c799)
![image](https://github.com/user-attachments/assets/996c3dfe-a544-4d96-9cec-762abce6bdda)




















