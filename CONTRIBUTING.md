# Guia de Contribuição

Obrigado por considerar contribuir com este projeto! 

## Como Contribuir

### Reportando Bugs

Se encontrou um bug, abra uma issue incluindo:

- Descrição clara do problema
- Passos para reproduzir
- Comportamento esperado vs. comportamento atual
- Screenshots (se aplicável)
- Versão do Java e sistema operacional

### Sugerindo Melhorias

Para sugerir novas funcionalidades:

1. Verifique se já não existe uma issue similar
2. Crie uma issue descrevendo a funcionalidade
3. Explique o caso de uso e os benefícios

### Pull Requests

1. Faça fork do projeto
2. Crie uma branch a partir da `main`:
   ```bash
   git checkout -b feature/minha-contribuicao
   ```

3. Faça suas alterações seguindo o estilo do código existente

4. Teste suas mudanças:
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

5. Commit com mensagens claras:
   ```bash
   git commit -m "Adiciona funcionalidade X"
   ```

6. Push para seu fork:
   ```bash
   git push origin feature/minha-contribuicao
   ```

7. Abra um Pull Request

## Padrões de Código

### Java

- Use Java 21+
- Siga convenções de nomenclatura Java
- Mantenha métodos pequenos e focados
- Adicione comentários apenas quando necessário
- Use nomes descritivos para variáveis e métodos

### Commits

Formato de mensagem de commit:

```
Tipo: Breve descrição (até 50 caracteres)

Descrição mais detalhada, se necessário.
```

Tipos:
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação
- `refactor`: Refatoração de código
- `test`: Adição de testes
- `chore`: Tarefas de manutenção

Exemplos:
```
feat: Adiciona exportação de recibos para Excel

fix: Corrige formatação de CPF em recibos
```

## Estrutura do Código

- `controller/`: Lógica das telas JavaFX
- `dao/`: Acesso ao banco de dados
- `model/`: Entidades de domínio
- `service/`: Lógica de negócio
- `util/`: Funções auxiliares

## Dúvidas

Se tiver dúvidas, abra uma issue ou entre em contato.

Obrigado pela contribuição! 🎉



