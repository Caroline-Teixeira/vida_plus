// Para traduções

// Perfils em portugues
const roleTranslations = {
    "ADMIN": "Administrador",
    "PATIENT": "Paciente",
    "ATTENDANT": "Atendente",
    "HEALTH_PROFESSIONAL": "Profissional de Saúde"
};

// Tipos de consulta em português
const appointmentTypeTranslations = {
    "IN_PERSON": "Presencial",
    "TELEMEDICINE": "Telemedicina"
};

// Eventos em portugues
const appointmentStatusTranslations = {
    "SCHEDULED": "Agendada",
    "CONFIRMED": "Confirmada",
    "IN_PROGRESS": "Em Andamento",
    "COMPLETED": "Concluída",
    "CANCELLED": "Cancelada"
};

// Função para exibir mensagens de erro ou sucesso
function showMessage(message, type = 'success') {
    const messageBox = document.getElementById('messageBox');
    messageBox.textContent = message;
    messageBox.className = `message-box ${type}`;
    messageBox.style.display = 'block';
    setTimeout(() => {
        messageBox.style.display = 'none';
    }, 5000);
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
}