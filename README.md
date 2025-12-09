# Sistema de Gestão de Recibos

Sistema desktop desenvolvido em Java para geração e gerenciamento de recibos de prestação de serviços. Desenvolvido com JavaFX para a interface gráfica e SQLite como banco de dados embarcado.

## Funcionalidades Principais

- Cadastro de empresas (suporte multi-tenant)
- Gestão de clientes (pessoa física e jurídica)
- Geração de recibos em PDF
- Categorização de serviços
- Histórico de recibos emitidos
- Formatação automática de documentos (CPF/CNPJ, telefones)
- Conversão de valores por extenso
- Dados de exemplo para testes

## Tecnologias Utilizadas

- Java 21
- JavaFX 21 (Interface gráfica)
- Maven (Gerenciamento de dependências)
- SQLite (Banco de dados)
- iText 8 (Geração de PDF)
- SLF4J + Logback (Logs)

## Requisitos do Sistema

- JDK 21 ou superior
- Maven 3.8+

## Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/Recibo.git
   cd Recibo
   ```

2. Execute o projeto com Maven:
   ```bash
   mvn javafx:run
   ```

Na primeira execução, o sistema irá criar automaticamente o banco de dados SQLite com dados de exemplo.

## Dados de Exemplo

Para facilitar os testes iniciais, o sistema inclui os seguintes dados:
- 2 empresas cadastradas
- 5 clientes (pessoas físicas e jurídicas)
- 7 categorias de serviços
- 6 recibos de exemplo

[Ver detalhes dos dados →](DADOS_EXEMPLO.md)

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/dataware/recibo/
│   │   ├── controller/     # Controladores das telas
│   │   ├── dao/            # Camada de acesso a dados
│   │   ├── model/          # Modelos de domínio
│   │   ├── service/        # Lógica de negócios
│   │   └── util/           # Utilitários gerais
│   └── resources/          # Arquivos de recursos
│       ├── fxml/           # Definições das telas
│       └── styles/         # Estilos CSS
└── test/                   # Testes unitários e de integração
```

## Guia Rápido

### Login Inicial
Na primeira execução, utilize os dados da empresa de exemplo:
- Empresa: **Transportadora Expresso Brasil Ltda**
- CNPJ: **45.677.973/0001-06**

### Principais Funcionalidades

1. **Clientes**
   - Cadastro de clientes (pessoa física/jurídica)
   - Consulta e edição de cadastros
   - Filtros de busca

2. **Recibos**
   - Emissão de recibos
   - Histórico de recibos emitidos
   - Filtros por data e cliente

3. **Categorias**
   - Cadastro de categorias de serviços
   - Organização dos tipos de serviços

### Gerando um Recibo

1. Acesse o menu "Novo Recibo"
2. Selecione o cliente
3. Escolha a categoria de serviço
4. Preencha os valores e descrição
5. Clique em "Gerar" para criar o PDF

## Reiniciando os Dados

Para voltar aos dados iniciais, remova o arquivo `recibo.db` na raiz do projeto e execute novamente.

## Documentação Adicional

Consulte a pasta `docs/` para informações detalhadas sobre instalação e personalização.
- [Arquitetura do Sistema](docs/ARQUITETURA.md)
- [Dados de Exemplo](DADOS_EXEMPLO.md)
- [Migração MySQL → SQLite](MIGRACAO_CONCLUIDA.md)

## 🤝 Contribuindo

Contribuições são bem-vindas! Veja [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes.

## 📝 Changelog

Veja [CHANGELOG.md](CHANGELOG.md) para histórico de versões.

## 🎯 Roadmap

- [ ] Testes unitários
- [ ] Busca avançada de recibos
- [ ] Exportação para Excel
- [ ] Dashboard com estatísticas
- [ ] Tema escuro
- [ ] Envio de recibos por e-mail

## 📄 Licença

Este projeto está sob a licença MIT. Veja [LICENSE](LICENSE) para mais detalhes.

## 👤 Autor

Desenvolvido com ☕ por [Seu Nome]

## 🐛 Problemas ou Sugestões?

Abra uma [issue](https://github.com/seu-usuario/Recibo/issues) no GitHub!

---

**⭐ Se este projeto foi útil, deixe uma estrela no GitHub!**
