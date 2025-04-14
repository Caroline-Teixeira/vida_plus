// hospital.js

// Função para criar um prontuário padrão no frontend
function createDefaultMedicalRecord() {
    const currentDate = new Date();
    const formattedDate = `${currentDate.getFullYear()}-${(currentDate.getMonth() + 1).toString().padStart(2, '0')}-${currentDate.getDate().toString().padStart(2, '0')}T${currentDate.getHours().toString().padStart(2, '0')}:${currentDate.getMinutes().toString().padStart(2, '0')}:00.000Z`;

    return {
        patient: { name: currentUserName || "Usuário Atual" },
        recordDate: formattedDate,
        appointments: [],
        observations: []
    };
}

// Função para carregar as consultas do usuário atual
async function loadAppointments() {
    console.log("[Hospital] Iniciando carregamento das consultas...");
    try {
        const appointments = await fetchApi("/api/appointments/current", "GET", null, true);
        console.log("[Hospital] Consultas recebidas:", JSON.stringify(appointments, null, 2));

        const appointmentsList = document.getElementById("appointmentsList");
        appointmentsList.innerHTML = "";

        if (appointments.length === 0) {
            appointmentsList.innerHTML = "<p>Nenhuma consulta encontrada.</p>";
            return;
        }

        for (const appointment of appointments) {
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
    } catch (error) {
        console.error("[Hospital] Erro ao carregar consultas:", error.message);
        // Tratar erros 401 e 403 como falha de autenticação
        if (error.message.includes("401") || error.message.includes("403")) {
            showMessage("Erro de autenticação. Faça login novamente.", 'error');
            setTimeout(() => {
                localStorage.removeItem("hospital-token");
                window.location.href = "/auth/login.html";
            }, 3000);
            return;
        }
        // Para outros erros, apenas exibir mensagem
        showMessage(error.message, 'error');
    }
}

// Função para carregar o prontuário do usuário atual
async function loadMedicalRecord() {
    console.log("[Hospital] Iniciando carregamento do prontuário...");
    let medicalRecord;

    try {
        medicalRecord = await fetchApi("/api/medical-records/current", "GET", null, true);
        console.log("[Hospital] Prontuário recebido:", JSON.stringify(medicalRecord, null, 2));
    } catch (error) {
        console.error("[Hospital] Erro ao carregar prontuário:", error.message);
        // Se o erro for 404 (prontuário não encontrado), 401 ou 403 (sem permissão), criar um prontuário padrão
        if (error.message.includes("404") || error.message.includes("401") || error.message.includes("403")) {
            console.log("[Hospital] Prontuário não encontrado ou acesso negado. Criando prontuário padrão no frontend...");
            medicalRecord = createDefaultMedicalRecord();
            showMessage("Nenhum prontuário encontrado. Prontuário padrão criado com sucesso.", 'success');
        } else {
            // Para outros erros, exibir mensagem e redirecionar para login
            showMessage(error.message, 'error');
            setTimeout(() => {
                localStorage.removeItem("hospital-token");
                window.location.href = "/auth/login.html";
            }, 3000);
            return; // Sai da função para evitar renderização
        }
    }

    // Renderização do prontuário
    document.getElementById("medicalRecordPatient").textContent = medicalRecord.patient && medicalRecord.patient.name 
        ? medicalRecord.patient.name 
        : "N/A";

    const recordDate = new Date(medicalRecord.recordDate);
    const formattedRecordDate = `${recordDate.getDate().toString().padStart(2, '0')}/${(recordDate.getMonth() + 1).toString().padStart(2, '0')}/${recordDate.getFullYear()} ${recordDate.getHours().toString().padStart(2, '0')}:${recordDate.getMinutes().toString().padStart(2, '0')}`;
    document.getElementById("medicalRecordDate").textContent = formattedRecordDate;

    const appointmentsList = document.getElementById("medicalRecordAppointments");
    appointmentsList.innerHTML = "";

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

    const observationsList = document.getElementById("medicalRecordObservations");
    observationsList.innerHTML = "";

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
}