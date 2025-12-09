# Guia de Instalação

## Pré-requisitos

### Windows
- JDK 21: [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- Maven 3.8+: [Download](https://maven.apache.org/download.cgi)

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven
```

### macOS
```bash
brew install openjdk@21 maven
```

## Verificar Instalação

```bash
java -version
# Deve mostrar: java version "21.x.x"

mvn -version
# Deve mostrar: Apache Maven 3.8+
```

## Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/Recibo.git
cd Recibo
```

## Compilar o Projeto

```bash
mvn clean compile
```

## Executar

```bash
mvn javafx:run
```

## Primeira Execução

Na primeira vez, o sistema irá:

1. Criar o banco de dados automaticamente
2. Solicitar cadastro da sua empresa
3. Criar categorias padrão de serviços

## Gerar JAR Executável

```bash
mvn clean package
```

O arquivo JAR será gerado em `target/Recibo-1.0-SNAPSHOT.jar`

Para executar:
```bash
java -jar target/Recibo-1.0-SNAPSHOT.jar
```

## Problemas Comuns

### "Java version não compatível"
Certifique-se de estar usando Java 21 ou superior.

### "JavaFX runtime components are missing"
O Maven deve baixar automaticamente. Se não funcionar:
```bash
mvn clean install -U
```

### "Erro ao conectar ao banco de dados"
Verifique se o arquivo `recibo.db` tem permissão de escrita.

## Suporte

Se tiver problemas, abra uma [issue](https://github.com/seu-usuario/Recibo/issues).



