# 📊 Dados de Exemplo no SQLite

## ✅ Dados Inseridos Automaticamente

Quando você executar `mvn javafx:run` pela primeira vez, o sistema criará o banco `recibo.db` com os seguintes dados:

---

## 🏢 **2 EMPRESAS:**

### Empresa 1:
- **Nome:** Motoboy luma Express Ltda
- **CNPJ:** 98.677.973/0014-09
- **Endereço:** Av. João Naves de Ávila, 1000 - Santa Mônica
- **Cidade:** Uberlândia/MG
- **CEP:** 38408-100
- **Celular:** (34) 99999-9999
- **Email:** contato@motoboyajato.com.br
- **Site:** www.motoboyajato.com.br

### Empresa 2:
- **Nome:** Carlos Faria
- **CPF:** 823.289.261-34
- **Endereço:** Rua das Flores, 123 - Centro
- **Cidade:** Uberlândia/MG
- **Celular:** (34) 98888-7777

---

## 📁 **7 CATEGORIAS:**

**Para Empresa 1:**
1. Prestação de Serviços
2. Venda de Produtos
3. Aluguel
4. Consultoria
5. Transporte

**Para Empresa 2:**
6. Prestação de Serviços
7. Venda de Produtos

---

## 👥 **5 CLIENTES:**

### Cliente 1:
- **Nome:** Laticínios Tirolez Ltda
- **CNPJ:** 55.885.321/0001-02
- **Cidade:** São Paulo/SP
- **Telefone:** (11) 3333-4444
- **Email:** contato@tirolez.com.br

### Cliente 2:
- **Nome:** João Silva Santos
- **CPF:** 123.456.789-00
- **Cidade:** Uberlândia/MG
- **Telefone:** (34) 3232-1111

### Cliente 3:
- **Nome:** Supermercado Bom Preço Ltda
- **CNPJ:** 12.345.678/0001-90
- **Cidade:** Uberlândia/MG
- **Telefone:** (34) 3333-2222

### Cliente 4:
- **Nome:** Maria Oliveira Costa
- **CPF:** 987.654.321-11
- **Cidade:** Uberlândia/MG
- **Telefone:** (34) 3234-5678

### Cliente 5:
- **Nome:** Tech Solutions Informática Ltda
- **CNPJ:** 98.765.432/0001-10
- **Cidade:** Uberlândia/MG
- **Telefone:** (34) 3330-4455

---

## 📄 **6 RECIBOS:**

### Recibo 1:
- **Número:** 001-2025
- **Cliente:** Laticínios Tirolez Ltda
- **Valor:** R$ 200,00
- **Referente:** Serviço de entrega expressa de documentos em São Paulo/SP
- **Pagamento:** PIX
- **Data:** 5 dias atrás

### Recibo 2:
- **Número:** 002-2025
- **Cliente:** João Silva Santos
- **Valor:** R$ 150,00
- **Referente:** Serviço de manutenção residencial
- **Pagamento:** Dinheiro
- **Data:** 3 dias atrás

### Recibo 3:
- **Número:** 003-2025
- **Cliente:** Supermercado Bom Preço
- **Valor:** R$ 350,00
- **Referente:** Transporte de mercadorias de Uberlândia para Araguari
- **Pagamento:** Transferência Bancária
- **Data:** 2 dias atrás

### Recibo 4:
- **Número:** 004-2025
- **Cliente:** Maria Oliveira Costa
- **Valor:** R$ 500,00
- **Referente:** Consultoria em organização doméstica
- **Pagamento:** Cartão de Crédito
- **Data:** 1 dia atrás

### Recibo 5:
- **Número:** 005-2025
- **Cliente:** Tech Solutions Informática
- **Valor:** R$ 1.200,00
- **Referente:** Consultoria em sistemas de informação
- **Pagamento:** PIX
- **Data:** Hoje

### Recibo 6:
- **Número:** 006-2025
- **Cliente:** Laticínios Tirolez Ltda
- **Valor:** R$ 450,00
- **Referente:** Entrega de equipamentos em Campinas/SP
- **Pagamento:** PIX
- **Data:** Hoje

---

## 🚀 **Como Testar:**

```bash
# Se já existe um banco antigo, delete:
del recibo.db

# Execute o sistema:
mvn javafx:run
```

### Na tela de login:
- Selecione: **Motoboy Ajato Express Ltda**
- Clique: **Entrar**

### No Dashboard:
- ✅ **Clientes:** 5 clientes cadastrados
- ✅ **Recibos:** 6 recibos emitidos
- ✅ **Categorias:** 5 categorias

---

## 💾 **Resetar Dados:**

Para começar do zero:

```bash
del recibo.db
mvn javafx:run
```

Os dados de exemplo serão inseridos novamente automaticamente!

---

## 📝 **Adicionar Mais Dados:**

Você pode:
1. Usar o próprio sistema (cadastrar novos clientes/recibos)
2. Ou editar `DatabaseConnection.java` método `inserirDadosExemplo()`



