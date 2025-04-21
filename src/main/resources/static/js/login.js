// Função para fazer requisições à API
async function fetchApi(url, method = "GET", body = null) {
    const headers = { "Content-Type": "application/json" };
    const options = { method, headers };
    
    if (body) options.body = JSON.stringify(body);
    console.log(`[Login] Enviando requisição para ${url} com método ${method}`);
    console.log("[Login] Corpo da requisição:", body);
    const response = await fetch(url, options);
    console.log(`[Login] Resposta de ${url}: Status ${response.status}`);

    if (!response.ok) {
        const errorText = await response.text();
        console.error("[Login] Detalhes do erro:", errorText);
        throw new Error(`Erro ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    console.log("[Login] Dados recebidos:", JSON.stringify(data, null, 2));
    return data;
}

// Função para lidar com o login
document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMessageDiv = document.getElementById("errorMessage");

    console.log("[Login] Iniciando processo de login...");
    console.log("[Login] E-mail:", email);
    console.log("[Login] Senha:", password);

    try {
        const data = await fetchApi("/auth/login", "POST", { email, password });
        const token = data.token;
        if (!token) {
            console.error("[Login] Token não encontrado na resposta:", data);
            throw new Error("Token não encontrado na resposta.");
        }
        console.log("[Login] Token recebido:", token);
        localStorage.setItem("hospital-token", token);
        console.log("[Login] Token armazenado no localStorage:", localStorage.getItem("hospital-token"));
        // Remove o alerta e redireciona automaticamente para /users/index.html
        window.location.href = "/users/index.html";
    } catch (error) {
        console.error("[Login] Erro ao fazer login:", error.message);
        errorMessageDiv.style.display = "block";
    }
});