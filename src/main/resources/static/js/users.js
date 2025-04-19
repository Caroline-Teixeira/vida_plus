// Variáveis globais para armazenar o ID, nome e papéis do usuário atual
let currentUserId = null;
let currentUserName = null;
let userRoles = [];
let isRegisterMode = false; // Controla o modo (cadastro ou gerenciamento)

// Função para obter o token
function getToken() {
    const token = localStorage.getItem("hospital-token");
    console.log("[Users] Token recuperado do localStorage:", token);
    if (!token) {
        console.error("[Users] Nenhum token encontrado no localStorage.");
        throw new Error("Nenhum token encontrado.");
    }
    return token;
}

// Função para fazer requisições à API
async function fetchApi(url, method = "GET", body = null, requiresAuth = true) {
    const headers = { "Content-Type": "application/json" };
    
    if (requiresAuth) {
        const token = getToken();
        headers["Authorization"] = `Bearer ${token.trim()}`;
        console.log("[Users] Cabeçalho Authorization:", headers.Authorization);
    }

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    console.log(`[Users] Enviando requisição para ${url} com método ${method}`);
    console.log("[Users] Corpo da requisição:", body);
    const response = await fetch(url, options);
    console.log(`[Users] Resposta de ${url}: Status ${response.status}`);

    if (!response.ok) {
        const errorText = await response.text();
        console.error("[Users] Detalhes do erro:", errorText);
        // Não tratamos 401/403 aqui, deixamos a função chamadora decidir
        throw new Error(`Erro ${response.status}: ${errorText || response.statusText}`);
    }

    const data = await response.json();
    console.log("[Users] Dados recebidos:", JSON.stringify(data, null, 2));
    return data;
}

// Função para logout
async function logout() {
    console.log("[Users] Iniciando logout...");
    try {
        await fetchApi("/auth/logout", "POST", null, true);
        localStorage.removeItem("hospital-token");
        console.log("[Users] Token removido do localStorage.");
        window.location.href = "/auth/login.html";
    } catch (error) {
        console.error("[Users] Erro ao fazer logout:", error.message);
        showMessage("Erro ao fazer logout: " + error.message, 'error');
    }
}

// Função para carregar os dados do usuário autenticado
async function loadCurrentUser() {
    console.log("[Users] Iniciando carregamento dos dados do usuário...");
    try {
        const user = await fetchApi("/api/users/current", "GET", null, true);
        console.log("[Users] Dados completos do usuário:", JSON.stringify(user, null, 2));
        console.log("[Users] Papéis do usuário (user.roles):", user.roles);

        currentUserId = user.id;
        currentUserName = user.name;
        userRoles = user.roles.map(role => role.name);
        console.log("[Users] Usuário atual - ID:", currentUserId, "Nome:", currentUserName, "Papéis:", userRoles);

        document.getElementById("currentUserName").textContent = user.name || "N/A";
        document.getElementById("currentUserCpf").textContent = user.cpf || "N/A";
        document.getElementById("currentUserEmail").textContent = user.email || "N/A";
        document.getElementById("currentUserContact").textContent = user.contact || "N/A";
        document.getElementById("currentUserDateOfBirth").textContent = user.dateOfBirth || "N/A";
        document.getElementById("currentUserGender").textContent = user.gender === "MALE" ? "Masculino" : user.gender === "FEMALE" ? "Feminino" : "N/A";
        const translatedRoles = user.roles.map(role => {
            console.log("[Users] Processando papel:", role);
            const translatedRole = roleTranslations[role.name] || role.name;
            console.log("[Users] Papel traduzido:", translatedRole);
            return translatedRole;
        }).join(", ");
        document.getElementById("currentUserRoles").textContent = translatedRoles || "N/A";

        if (userRoles.includes("ADMIN") || userRoles.includes("ATTENDANT")) {
            document.getElementById("manageUsersTab").style.display = "block";
            loadUsers();
        }
    } catch (error) {
        console.error("[Users] Erro ao carregar usuário:", error.message);
        showMessage(error.message, 'error');
        setTimeout(() => {
            window.location.href = "/auth/login.html";
        }, 3000);
    }
}

// Função para carregar a lista de usuários
async function loadUsers() {
    console.log("[Users] Iniciando carregamento da lista de usuários...");
    try {
        const users = await fetchApi("/api/users", "GET", null, true);
        console.log("[Users] Usuários recebidos:", JSON.stringify(users, null, 2));

        const usersList = document.getElementById("usersList");
        usersList.innerHTML = "";

        if (!users || users.length === 0) {
            usersList.innerHTML = "<p>Nenhum usuário encontrado.</p>";
        } else {
            for (const user of users) {
                const userItem = document.createElement("div");
                userItem.classList.add("user-item");
                userItem.innerHTML = `
                    <div>
                        <p><strong>ID:</strong> ${user.id}</p>
                        <p><strong>Nome:</strong> ${user.name || "N/A"}</p>
                        <p><strong>E-mail:</strong> ${user.email || "N/A"}</p>
                        <p><strong>Perfil:</strong> ${user.roles ? user.roles.map(role => roleTranslations[role.name] || role.name).join(', ') : "N/A"}</p>
                    </div>
                    <div>
                        <button class="edit" onclick="editUser(${user.id})">Editar</button>
                        <button onclick="deleteUser(${user.id})">Deletar</button>
                    </div>
                `;
                usersList.appendChild(userItem);
            }
        }
    } catch (error) {
        console.error("[Users] Erro ao carregar usuários:", error.message);
        showMessage("Erro ao carregar usuários: " + error.message, 'error');
        document.getElementById("usersList").innerHTML = "<p>Você não tem permissão para visualizar os usuários.</p>";
    }
}

// Função para buscar um usuário por ID
async function searchUserById() {
    const userId = document.getElementById("searchUserId").value;
    if (!userId) {
        showMessage("Por favor, insira um ID válido.", 'error');
        return;
    }

    console.log("[Users] Buscando usuário com ID:", userId);
    try {
        const user = await fetchApi(`/api/users/${userId}`, "GET", null, true);
        console.log("[Users] Usuário recebido:", JSON.stringify(user, null, 2));

        const usersList = document.getElementById("usersList");
        usersList.innerHTML = "";

        const userItem = document.createElement("div");
        userItem.classList.add("user-item");
        userItem.innerHTML = `
            <div>
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Nome:</strong> ${user.name || "N/A"}</p>
                <p><strong>E-mail:</strong> ${user.email || "N/A"}</p>
                <p><strong>Perfil:</strong> ${user.roles ? user.roles.map(role => roleTranslations[role.name] || role.name).join(', ') : "N/A"}</p>
            </div>
            <div>
                <button class="edit" onclick="editUser(${user.id})">Editar</button>
                <button onclick="deleteUser(${user.id})">Deletar</button>
            </div>
        `;
        usersList.appendChild(userItem);
    } catch (error) {
        console.error("[Users] Erro ao buscar usuário:", error.message);
        showMessage("Erro ao buscar usuário: " + error.message, 'error');
        document.getElementById("usersList").innerHTML = "<p>Usuário não encontrado ou você não tem permissão.</p>";
    }
}

// Função para alternar entre modo de cadastro e gerenciamento
function toggleMode(clearForm = true) {
    isRegisterMode = !isRegisterMode;
    const toggleButton = document.getElementById("toggleModeButton");
    const userFormContainer = document.getElementById("userFormContainer");
    const usersListContainer = document.getElementById("usersListContainer");
    const userSearchContainer = document.getElementById("userSearchContainer");

    if (isRegisterMode) {
        toggleButton.textContent = "Gerenciar Usuários";
        userFormContainer.style.display = "block";
        usersListContainer.style.display = "none";
        userSearchContainer.style.display = "none";
        if (clearForm) {
            clearUserForm(); // Limpa o formulário apenas se clearForm for true
        }
    } else {
        toggleButton.textContent = "Cadastrar Usuário";
        userFormContainer.style.display = "none";
        usersListContainer.style.display = "block";
        userSearchContainer.style.display = "block";
        loadUsers(); // Recarrega a lista de usuários ao voltar para o modo de gerenciamento
    }
}

// Função para preencher o formulário para edição
async function editUser(userId) {
    console.log("[Users] Editando usuário com ID:", userId);
    try {
        // Busca os dados do usuário
        const user = await fetchApi(`/api/users/${userId}`, "GET", null, true);
        console.log("[Users] Dados do usuário para edição:", JSON.stringify(user, null, 2));

        // Preenche os campos do formulário com os dados do usuário
        document.getElementById("userId").value = user.id || "";
        document.getElementById("userName").value = user.name || "";
        document.getElementById("userCpf").value = user.cpf || "";
        document.getElementById("userEmail").value = user.email || "";
        document.getElementById("userPassword").value = ""; // Senha não deve ser preenchida para edição
        document.getElementById("userContact").value = user.contact || "";
        document.getElementById("userDateOfBirth").value = user.dateOfBirth ? user.dateOfBirth.split("T")[0] : "";
        document.getElementById("userGender").value = user.gender || "MALE";

        // Preenche os checkboxes de papéis
        const roleCheckboxes = document.querySelectorAll("#userRoles input[name='roles']");
        roleCheckboxes.forEach(checkbox => {
            const hasRole = user.roles && user.roles.some(role => role.name === checkbox.value);
            checkbox.checked = hasRole;
            console.log(`[Users] Checkbox ${checkbox.value} marcado:`, hasRole);
        });

        // Garante que o formulário esteja visível (modo de cadastro)
        if (!isRegisterMode) {
            toggleMode(false); // Não limpa o formulário ao entrar no modo de cadastro
        }
    } catch (error) {
        console.error("[Users] Erro ao carregar dados do usuário para edição:", error.message);
        showMessage("Erro ao carregar dados do usuário: " + error.message, 'error');
    }
}

// Função para salvar (cadastrar ou atualizar) um usuário
async function saveUser() {
    const userId = document.getElementById("userId").value;
    const selectedRoles = Array.from(document.querySelectorAll("#userRoles input[name='roles']:checked"))
        .map(checkbox => checkbox.value);

    if (selectedRoles.length === 0) {
        showMessage("Por favor, selecione pelo menos um perfil.", 'error');
        return;
    }

    const userData = {
        name: document.getElementById("userName").value,
        cpf: document.getElementById("userCpf").value,
        email: document.getElementById("userEmail").value,
        password: document.getElementById("userPassword").value || undefined,
        contact: document.getElementById("userContact").value,
        dateOfBirth: document.getElementById("userDateOfBirth").value,
        gender: document.getElementById("userGender").value,
        roles: selectedRoles
    };

    const method = userId ? "PUT" : "POST";
    const url = userId ? `/api/users/${userId}` : "/api/users";

    console.log("[Users] Salvando usuário com método:", method);
    console.log("[Users] URL:", url);
    console.log("[Users] Dados enviados:", JSON.stringify(userData, null, 2));
    try {
        const response = await fetchApi(url, method, userData, true);
        console.log("[Users] Resposta do servidor:", JSON.stringify(response, null, 2));
        showMessage(userId ? "Usuário atualizado com sucesso!" : "Usuário cadastrado com sucesso!", 'success');
        clearUserForm();
        // Volta para o modo de gerenciamento após salvar
        isRegisterMode = true; // Força a troca de modo
        toggleMode();
    } catch (error) {
        console.error("[Users] Erro ao salvar usuário:", error);
        showMessage("Erro ao salvar usuário: " + error.message, 'error');
    }
}

// Função para deletar um usuário
async function deleteUser(userId) {
    if (!confirm("Tem certeza que deseja deletar este usuário?")) return;

    console.log("[Users] Deletando usuário com ID:", userId);
    try {
        await fetchApi(`/api/users/${userId}`, "DELETE", null, true);
        showMessage("Usuário deletado com sucesso!", 'success');
        loadUsers();
    } catch (error) {
        console.error("[Users] Erro ao deletar usuário:", error.message);
        showMessage("Erro ao deletar usuário: " + error.message, 'error');
    }
}

// Função para limpar o formulário
function clearUserForm() {
    document.getElementById("userForm").reset();
    document.getElementById("userId").value = "";
    const roleCheckboxes = document.querySelectorAll("#userRoles input[name='roles']");
    roleCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
    });
}

// Inicialização
window.onload = function() {
    const tabButtons = document.getElementsByClassName("tab-button");
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].addEventListener("click", (event) => {
            const tabName = tabButtons[i].getAttribute("data-tab");
            openTab(event, tabName);
            if (tabName === "meus-dados") {
                loadCurrentUser();
            } else if (tabName === "consultas") {
                loadAppointments();
            } else if (tabName === "prontuario") {
                loadMedicalRecord();
            } else if (tabName === "cirurgias") {
                loadSurgeries();
            } else if (tabName === "gerenciar-usuarios") {
                // Garante que a aba comece no modo de gerenciamento
                isRegisterMode = false;
                toggleMode();
            }
        });
    }

    document.getElementById("logoutButton").addEventListener("click", logout);

    loadCurrentUser();
};