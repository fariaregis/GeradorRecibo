# Guia de Migração: MySQL → SQLite

## Por que migrar?

- ✅ Sem necessidade de servidor MySQL
- ✅ Banco de dados em arquivo único
- ✅ Portável entre sistemas operacionais
- ✅ Perfeito para aplicações desktop
- ✅ Fácil para contribuidores testarem

## Passos para Migração

### 1. Atualizar dependências no `pom.xml`

**Remover:**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

**Adicionar:**
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.0.0</version>
</dependency>
```

### 2. Atualizar `module-info.java`

**Remover:**
```java
requires com.mysql.cj;
```

**Adicionar:**
```java
requires org.xerial.sqlitejdbc;
```

### 3. Atualizar `DatabaseConnection.java`

**MySQL (antes):**
```java
private static final String URL = "jdbc:mysql://localhost:3306/db_recibo";
private static final String USER = "fariaregis";
private static final String PASSWORD = "331310rfMM";
```

**SQLite (depois):**
```java
private static final String URL = "jdbc:sqlite:recibo.db";
// SQLite não precisa de usuário e senha
```

### 4. Ajustes no SQL

#### AUTO_INCREMENT → AUTOINCREMENT
```sql
-- MySQL
id INT AUTO_INCREMENT PRIMARY KEY

-- SQLite
id INTEGER PRIMARY KEY AUTOINCREMENT
```

#### Tipos de dados
```sql
-- MySQL: VARCHAR, TEXT, DATETIME
-- SQLite: TEXT, TEXT, TEXT (mais flexível)

-- MySQL
nome VARCHAR(200)
data_emissao DATETIME

-- SQLite
nome TEXT
data_emissao TEXT
```

#### ENUM → TEXT com CHECK
```sql
-- MySQL
tipo_pessoa ENUM('F', 'J')

-- SQLite
tipo_pessoa TEXT CHECK(tipo_pessoa IN ('F', 'J'))
```

### 5. Script SQL para SQLite

Criei o arquivo `database_setup_sqlite.sql` com a estrutura adaptada.

Para criar o banco:
```bash
sqlite3 recibo.db < database_setup_sqlite.sql
```

Ou deixe o próprio Java criar automaticamente na primeira execução.

### 6. Testar

```bash
mvn clean compile
mvn javafx:run
```

## Diferenças importantes

| Recurso | MySQL | SQLite |
|---------|-------|--------|
| Servidor | Necessário | Embutido |
| Arquivo | Vários arquivos | Um arquivo (.db) |
| Usuário/Senha | Sim | Não |
| DATETIME | Nativo | TEXT/INTEGER |
| ENUM | Nativo | TEXT + CHECK |
| Transações | InnoDB | Nativo |

## Vantagens para Projeto Open Source

1. **Clone e rode**: Sem configuração de banco
2. **Portabilidade**: Funciona em qualquer SO
3. **Backup simples**: Copie o arquivo .db
4. **Dev-friendly**: Fácil para contribuidores
5. **Distribuição**: Pode vir com dados de exemplo

## Ferramentas úteis

- **DB Browser for SQLite**: GUI para visualizar/editar
- **sqlite3**: CLI oficial
- **DataGrip / DBeaver**: IDEs que suportam SQLite

## Precisa de ajuda?

Posso fazer a migração completa para você! Basta confirmar.



