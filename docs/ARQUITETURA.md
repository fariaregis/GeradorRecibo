# Arquitetura do Sistema

## Visão Geral

O sistema segue uma arquitetura em camadas MVC (Model-View-Controller) adaptada para JavaFX.

```
┌─────────────────────────────────────────┐
│           View (FXML)                   │
│  Telas da interface do usuário          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Controller (JavaFX)              │
│  Lógica de apresentação e eventos       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │
│  Lógica de negócio (ex: PDF)            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         DAO (Data Access)               │
│  Acesso ao banco de dados               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Database (SQLite)                │
│  Persistência de dados                  │
└─────────────────────────────────────────┘
```

## Camadas

### 1. View (FXML)
Arquivos `.fxml` que definem a interface visual.

**Localização:** `src/main/resources/com/dataware/recibo/`

**Exemplos:**
- `login-view.fxml` - Tela de login
- `dashboard-view.fxml` - Tela principal
- `recibo-form-view.fxml` - Formulário de recibo

### 2. Controller
Classes Java que controlam as views.

**Localização:** `src/main/java/com/dataware/recibo/controller/`

**Responsabilidades:**
- Manipular eventos da UI
- Validar entrada do usuário
- Chamar services/DAOs
- Atualizar a view

**Exemplo:**
```java
@FXML
private void handleSalvar() {
    // Validar
    // Chamar DAO
    // Atualizar UI
}
```

### 3. Service
Lógica de negócio complexa.

**Localização:** `src/main/java/com/dataware/recibo/service/`

**Exemplos:**
- `ReciboPDFService` - Geração de PDF
- `NumeroExtensoUtil` - Conversão numérica

### 4. DAO (Data Access Object)
Acesso ao banco de dados.

**Localização:** `src/main/java/com/dataware/recibo/dao/`

**Padrão:**
```java
public class ClienteDAO {
    public void inserir(Cliente cliente) { }
    public List<Cliente> listar() { }
    public void atualizar(Cliente cliente) { }
    public void deletar(int id) { }
}
```

### 5. Model
Entidades de domínio (POJOs).

**Localização:** `src/main/java/com/dataware/recibo/model/`

**Exemplos:**
- `Empresa`
- `Cliente`
- `Recibo`
- `Categoria`

### 6. Util
Funções auxiliares.

**Localização:** `src/main/java/com/dataware/recibo/util/`

**Exemplos:**
- `FormatUtil` - Formatação de dados
- `ValidatorUtil` - Validações
- `DatabaseConnection` - Conexão com BD

## Fluxo de Dados

### Exemplo: Criar Recibo

1. **View**: Usuário preenche formulário
2. **Controller**: `ReciboFormController.handleSalvar()`
3. **Validação**: Verifica campos obrigatórios
4. **DAO**: `ReciboDAO.inserir(recibo)`
5. **Database**: INSERT na tabela
6. **Service**: `ReciboPDFService.gerarPDF(recibo)`
7. **Controller**: Atualiza UI e exibe mensagem

## Padrões Utilizados

- **MVC**: Separação de responsabilidades
- **DAO**: Abstração do acesso a dados
- **Singleton**: SessionManager, DatabaseConnection
- **Factory**: Criação de objetos complexos
- **Observer**: JavaFX Properties

## Multi-tenancy

O sistema é multi-tenant através do `empresa_sistema_id`:

- Cada empresa tem seus próprios dados isolados
- `SessionManager` mantém a empresa atual
- Todas as queries filtram por `empresa_sistema_id`

## Segurança

- Validação de entrada em todos os formulários
- Prepared Statements (proteção contra SQL Injection)
- Sessão por empresa (isolamento de dados)

## Performance

- Connection pooling (potencial melhoria)
- Lazy loading de listas
- Índices no banco de dados

## Extensibilidade

Para adicionar novas funcionalidades:

1. Criar Model (se necessário)
2. Criar/atualizar DAO
3. Criar FXML da view
4. Criar Controller
5. Adicionar navegação no Dashboard



