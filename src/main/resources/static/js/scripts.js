// Mapeamento de papéis para português
const roleTranslations = {
    "ADMIN": "Administrador",
    "PATIENT": "Paciente",
    "ATTENDANT": "Atendente",
    "HEALTH_PROFESSIONAL": "Profissional de Saúde"
};

// Mapeamento de tipos de consulta para português
const appointmentTypeTranslations = {
    "IN_PERSON": "Presencial",
    "TELEMEDICINE": "Telemedicina"
};

// Mapeamento de status de consulta para português
const appointmentStatusTranslations = {
    "SCHEDULED": "Agendada",
    "CONFIRMED": "Confirmada",
    "IN_PROGRESS": "Em Andamento",
    "COMPLETED": "Concluída",
    "CANCELLED": "Cancelada"
};

// Variáveis globais para armazenar o ID e o nome do usuário atual
let currentUserId = null;
let currentUserName = null;

// Função para obter o token
function getToken() {
    const token = localStorage.getItem("hospital-token");
    console.log("[Script] Token recuperado do localStorage:", token);
    if (!token) {
        console.error("[Script] Nenhum token encontrado no localStorage.");
        alert("Nenhum token encontrado. Faça login novamente.");
        throw new Error("Nenhum token encontrado.");
    }
    return token;
}

// Função para fazer requisições à API
async function fetchApi(url, method = "GET", body = null) {
    const token = getToken();
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token.trim()}`
    };
    console.log(`[Script] Requisição para ${url} com método ${method}`);
    console.log("[Script] Cabeçalho Authorization:", headers.Authorization);

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(url, options);
    console.log(`[Script] Resposta de ${url}: Status ${response.status}`);
    if (!response.ok) {
        const errorText = await response.text();
        console.error("[Script] Detalhes do erro:", errorText);
        if (response.status === 401 || response.status === 403) {
            console.error("[Script] Erro de autenticação. Token inválido ou expirado.");
            localStorage.removeItem("hospital-token");
            throw new Error("Erro de autenticação. Faça login novamente.");
        }
        throw new Error(`Erro ${response.status}: ${response.statusText}`);
    }
    return response.json();
}

// Função para carregar os dados do usuário autenticado
async function loadCurrentUser() {
    console.log("[Script] Iniciando carregamento dos dados do usuário...");
    try {
        const user = await fetchApi("/api/users/current");
        console.log("[Script] Dados completos do usuário:", JSON.stringify(user, null, 2));
        console.log("[Script] Papéis do usuário (user.roles):", user.roles);

        // Armazena o ID e o nome do usuário atual
        currentUserId = user.id;
        currentUserName = user.name;
        console.log("[Script] Usuário atual - ID:", currentUserId, "Nome:", currentUserName);

        document.getElementById("currentUserName").textContent = user.name || "N/A";
        document.getElementById("currentUserCpf").textContent = user.cpf || "N/A";
        document.getElementById("currentUserEmail").textContent = user.email || "N/A";
        document.getElementById("currentUserContact").textContent = user.contact || "N/A";
        document.getElementById("currentUserDateOfBirth").textContent = user.dateOfBirth || "N/A";
        document.getElementById("currentUserGender").textContent = user.gender === "MALE" ? "Masculino" : user.gender === "FEMALE" ? "Feminino" : "N/A";
        const translatedRoles = user.roles.map(role => {
            console.log("[Script] Processando papel:", role);
            const translatedRole = roleTranslations[role.name] || role.name;
            console.log("[Script] Papel traduzido:", translatedRole);
            return translatedRole;
        }).join(", ");
        document.getElementById("currentUserRoles").textContent = translatedRoles || "N/A";
    } catch (error) {
        console.error("[Script] Erro ao carregar usuário:", error.message);
        alert(error.message);
        window.location.href = "/auth/login.html";
    }
}

// Função para carregar as consultas do usuário atual
async function loadAppointments() {
    console.log("[Script] Iniciando carregamento das consultas...");
    try {
        const appointments = await fetchApi("/api/appointments/current");
        console.log("[Script] Consultas recebidas:", JSON.stringify(appointments, null, 2));

        const appointmentsList = document.getElementById("appointmentsList");
        appointmentsList.innerHTML = ""; // Limpa a lista antes de adicionar novos itens

        if (appointments.length === 0) {
            appointmentsList.innerHTML = "<p>Nenhuma consulta encontrada.</p>";
            return;
        }

        for (const appointment of appointments) {
            // Usa os nomes dentro dos objetos patientId e healthProfessionalId
            const patientDisplay = appointment.patientId && appointment.patientId.name 
                ? appointment.patientId.name 
                : `Paciente (ID: ${appointment.patientId?.id || 'Desconhecido'})`;
            const healthProfessionalDisplay = appointment.healthProfessionalId && appointment.healthProfessionalId.name 
                ? appointment.healthProfessionalId.name 
                : `Profissional de Saúde (ID: ${appointment.healthProfessionalId?.id || 'Desconhecido'})`;

            // Formata a data e hora
            const dateTime = new Date(appointment.dateTime);
            const formattedDateTime = `${dateTime.getDate().toString().padStart(2, '0')}/${(dateTime.getMonth() + 1).toString().padStart(2, '0')}/${dateTime.getFullYear()} ${dateTime.getHours().toString().padStart(2, '0')}:${dateTime.getMinutes().toString().padStart(2, '0')}`;

            // Cria o elemento HTML para a consulta
            const appointmentItem = document.createElement("div");
            appointmentItem.classList.add("appointment-item");
            appointmentItem.innerHTML = `
                <p><strong>Data e Hora:</strong> ${formattedDateTime}</p>
                <p><strong>Tipo:</strong> ${appointmentTypeTranslations[appointment.type] || appointment.type}</p>
                <p><strong>Status:</strong> ${appointmentStatusTranslations[appointment.status] || appointment.status}</p>
                <p><strong>Motivo:</strong> ${appointment.reason || "N/A"}</p>
                <p><strong>Paciente:</strong> ${patientDisplay}</p>
                <p><strong>Profissional de Saúde:</strong> ${healthProfessionalDisplay}</p>
            `;
            appointmentsList.appendChild(appointmentItem);
        }
    } catch (error) {
        console.error("[Script] Erro ao carregar consultas:", error.message);
        alert(error.message);
        window.location.href = "/auth/login.html";
    }
}

// Função para carregar o prontuário do usuário atual
async function loadMedicalRecord() {
    console.log("[Script] Iniciando carregamento do prontuário...");
    try {
        const medicalRecord = await fetchApi("/api/medical-records/current");
        console.log("[Script] Prontuário recebido:", JSON.stringify(medicalRecord, null, 2));

        // Preenche o nome do paciente
        document.getElementById("medicalRecordPatient").textContent = medicalRecord.patient && medicalRecord.patient.name 
            ? medicalRecord.patient.name 
            : "N/A";

        // Formata e preenche a data de criação do prontuário
        const recordDate = new Date(medicalRecord.recordDate);
        const formattedRecordDate = `${recordDate.getDate().toString().padStart(2, '0')}/${(recordDate.getMonth() + 1).toString().padStart(2, '0')}/${recordDate.getFullYear()} ${recordDate.getHours().toString().padStart(2, '0')}:${recordDate.getMinutes().toString().padStart(2, '0')}`;
        document.getElementById("medicalRecordDate").textContent = formattedRecordDate;

        // Preenche a lista de consultas associadas
        const appointmentsList = document.getElementById("medicalRecordAppointments");
        appointmentsList.innerHTML = ""; // Limpa a lista antes de adicionar novos itens

        if (!medicalRecord.appointments || medicalRecord.appointments.length === 0) {
            appointmentsList.innerHTML = "<p>Nenhuma consulta associada.</p>";
        } else {
            for (const appointment of medicalRecord.appointments) {
                const patientDisplay = appointment.patientId && appointment.patientId.name 
                    ? appointment.patientId.name 
                    : `Paciente (ID: ${appointment.patientId?.id || 'Desconhecido'})`;
                const healthProfessionalDisplay = appointment.healthProfessionalId && appointment.healthProfessionalId.name 
                    ? appointment.healthProfessionalId.name 
                    : `Profissional de Saúde (ID: ${appointment.healthProfessionalId?.id || 'Desconhecido'})`;

                const dateTime = new Date(appointment.dateTime);
                const formattedDateTime = `${dateTime.getDate().toString().padStart(2, '0')}/${(dateTime.getMonth() + 1).toString().padStart(2, '0')}/${dateTime.getFullYear()} ${dateTime.getHours().toString().padStart(2, '0')}:${dateTime.getMinutes().toString().padStart(2, '0')}`;

                const appointmentItem = document.createElement("div");
                appointmentItem.classList.add("appointment-item");
                appointmentItem.innerHTML = `
                    <p><strong>Data e Hora:</strong> ${formattedDateTime}</p>
                    <p><strong>Tipo:</strong> ${appointmentTypeTranslations[appointment.type] || appointment.type}</p>
                    <p><strong>Status:</strong> ${appointmentStatusTranslations[appointment.status] || appointment.status}</p>
                    <p><strong>Motivo:</strong> ${appointment.reason || "N/A"}</p>
                    <p><strong>Paciente:</strong> ${patientDisplay}</p>
                    <p><strong>Profissional de Saúde:</strong> ${healthProfessionalDisplay}</p>
                `;
                appointmentsList.appendChild(appointmentItem);
            }
        }

        // Preenche a lista de observações
        const observationsList = document.getElementById("medicalRecordObservations");
        observationsList.innerHTML = ""; // Limpa a lista antes de adicionar novos itens

        if (!medicalRecord.observations || medicalRecord.observations.length === 0) {
            observationsList.innerHTML = "<p>Nenhuma observação registrada.</p>";
        } else {
            for (const observation of medicalRecord.observations) {
                const dateTime = new Date(observation.dateTime);
                const formattedDateTime = `${dateTime.getDate().toString().padStart(2, '0')}/${(dateTime.getMonth() + 1).toString().padStart(2, '0')}/${dateTime.getFullYear()} ${dateTime.getHours().toString().padStart(2, '0')}:${dateTime.getMinutes().toString().padStart(2, '0')}`;

                const observationItem = document.createElement("div");
                observationItem.classList.add("observation-item");
                observationItem.innerHTML = `
                    <p><strong>Data:</strong> ${formattedDateTime}</p>
                    <p><strong>Observação:</strong> ${observation.text || "N/A"}</p>
                `;
                observationsList.appendChild(observationItem);
            }
        }
    } catch (error) {
        console.error("[Script] Erro ao carregar prontuário:", error.message);
        alert(error.message);
        window.location.href = "/auth/login.html";
    }
}

// Função para logout
async function logout() {
    console.log("[Script] Iniciando logout...");
    try {
        await fetchApi("/auth/logout", "POST");
        localStorage.removeItem("hospital-token");
        console.log("[Script] Token removido do localStorage.");
        window.location.href = "/auth/login.html";
    } catch (error) {
        console.error("[Script] Erro ao fazer logout:", error.message);
        alert("Erro ao fazer logout: " + error.message);
    }
}

// Função para gerenciar a exibição das abas
function openTab(event, tabName) {
    // Remove a classe "active" de todas as abas e conteúdos
    const tabContents = document.getElementsByClassName("tab-content");
    for (let i = 0; i < tabContents.length; i++) {
        tabContents[i].classList.remove("active");
    }

    const tabButtons = document.getElementsByClassName("tab-button");
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove("active");
    }

    // Adiciona a classe "active" à aba e ao conteúdo selecionados
    document.getElementById(tabName).classList.add("active");
    event.currentTarget.classList.add("active");

    // Carrega os dados correspondentes à aba selecionada
    if (tabName === "meus-dados") {
        loadCurrentUser();
    } else if (tabName === "consultas") {
        loadAppointments();
    } else if (tabName === "prontuario") {
        loadMedicalRecord();
    }
}

// Adiciona evento ao botão de logout
document.getElementById("logoutButton").addEventListener("click", logout);

// Carrega os dados do usuário automaticamente ao abrir a página, já que "Meus Dados" é a aba padrão
window.onload = function() {
    loadCurrentUser();
};