package com.dataware.recibo.service;

import com.dataware.recibo.model.Recibo;
import com.dataware.recibo.util.FormatUtil;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Serviço PRO para geração de PDFs de recibos - Design Moderno e Profissional
 * Layout: 2 vias (Cliente e Arquivo) na mesma página A4
 */
public class ReciboPDFServicePro {
    private static final Logger logger = LoggerFactory.getLogger(ReciboPDFServicePro.class);
    
    // Paleta de cores elegante e moderna
    private static final DeviceRgb PRETO = new DeviceRgb(33, 37, 41);
    private static final DeviceRgb CINZA_ESCURO = new DeviceRgb(73, 80, 87);
    private static final DeviceRgb CINZA_MEDIO = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb CINZA_CLARO = new DeviceRgb(233, 236, 239);
    private static final DeviceRgb CINZA_FUNDO = new DeviceRgb(248, 249, 250);
    private static final DeviceRgb BRANCO = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb AZUL_ESCURO = new DeviceRgb(13, 71, 161);
    private static final DeviceRgb VERDE_SUCESSO = new DeviceRgb(25, 135, 84);
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMAT_EXTENSO = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");

    /**
     * Gera PDF profissional do recibo com 1 via
     */
    public void gerarPDF(Recibo recibo, String caminhoArquivo) throws IOException {
        logger.info("Gerando PDF PRO do recibo: " + recibo.getNumeroRecibo());

        File file = new File(caminhoArquivo);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        PdfWriter writer = new PdfWriter(caminhoArquivo);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(25, 30, 25, 30);

        // VIA ÚNICA
        criarViaRecibo(document, recibo, "RECIBO");

        document.close();
        logger.info("PDF PRO gerado com sucesso: " + caminhoArquivo);
    }
    
    /**
     * Cria uma via do recibo (pode ser Cliente ou Arquivo)
     */
    private void criarViaRecibo(Document document, Recibo recibo, String tipoVia) {
        // Container principal com borda
        Div container = new Div()
                .setBorder(new SolidBorder(CINZA_CLARO, 1))
                .setPadding(15)
                .setMarginBottom(5);
        
        // ═══════════════════════════════════════════════════════════════
        // CABEÇALHO
        // ═══════════════════════════════════════════════════════════════
        Table headerTable = new Table(new float[]{3, 2});
        headerTable.setWidth(UnitValue.createPercentValue(100));
        
        // Lado esquerdo - Dados da empresa
        Cell empresaCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(15);
        
        // Nome da empresa
        empresaCell.add(new Paragraph(recibo.getEmpresa().getNomeRazaoSocial())
                .setFontSize(14)
                .setBold()
                .setFontColor(PRETO)
                .setMarginBottom(2));
        
        // Documento
        String tipoDoc = "J".equals(recibo.getEmpresa().getTipoPessoa()) ? "CNPJ" : "CPF";
        empresaCell.add(new Paragraph(tipoDoc + ": " + FormatUtil.formatCPFouCNPJ(recibo.getEmpresa().getCpfCnpj()))
                .setFontSize(9)
                .setFontColor(CINZA_ESCURO)
                .setMarginBottom(1));
        
        // Endereço
        String endereco = montarEnderecoEmpresa(recibo);
        if (!endereco.isEmpty()) {
            empresaCell.add(new Paragraph(endereco)
                    .setFontSize(8)
                    .setFontColor(CINZA_MEDIO)
                    .setMarginBottom(1));
        }
        
        // Contatos
        String contatos = montarContatosEmpresa(recibo);
        if (!contatos.isEmpty()) {
            empresaCell.add(new Paragraph(contatos)
                    .setFontSize(8)
                    .setFontColor(CINZA_MEDIO));
        }
        
        headerTable.addCell(empresaCell);
        
        // Lado direito - Box do número do recibo
        Cell numeroCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        
        // Box destacado com número
        Div numeroBox = new Div()
                .setBackgroundColor(AZUL_ESCURO)
                .setPadding(10)
                .setWidth(UnitValue.createPercentValue(100));
        
        numeroBox.add(new Paragraph("RECIBO")
                .setFontSize(10)
                .setFontColor(BRANCO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(3));
        
        numeroBox.add(new Paragraph("Nº " + recibo.getNumeroRecibo())
                .setFontSize(16)
                .setBold()
                .setFontColor(BRANCO)
                .setTextAlignment(TextAlignment.CENTER));
        
        numeroCell.add(numeroBox);
        
        // Tipo da via
        numeroCell.add(new Paragraph(tipoVia)
                .setFontSize(7)
                .setFontColor(CINZA_MEDIO)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(5));
        
        headerTable.addCell(numeroCell);
        container.add(headerTable);
        
        // Linha separadora
        container.add(criarLinhaSeparadora());
        
        // ═══════════════════════════════════════════════════════════════
        // CORPO DO RECIBO
        // ═══════════════════════════════════════════════════════════════
        
        // Valor em destaque
        Table valorTable = new Table(1);
        valorTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell valorCell = new Cell()
                .setBackgroundColor(CINZA_FUNDO)
                .setBorder(new SolidBorder(VERDE_SUCESSO, 2))
                .setPadding(12)
                .setTextAlignment(TextAlignment.CENTER);
        
        valorCell.add(new Paragraph("VALOR RECEBIDO")
                .setFontSize(8)
                .setFontColor(CINZA_MEDIO)
                .setMarginBottom(3));
        
        valorCell.add(new Paragraph(FormatUtil.formatMoeda(recibo.getValor()))
                .setFontSize(20)
                .setBold()
                .setFontColor(VERDE_SUCESSO)
                .setMarginBottom(3));
        
        valorCell.add(new Paragraph("(" + recibo.getValorExtenso() + ")")
                .setFontSize(9)
                .setItalic()
                .setFontColor(CINZA_ESCURO));
        
        valorTable.addCell(valorCell);
        container.add(valorTable);
        
        container.add(new Paragraph().setMarginBottom(8));
        
        // Dados do pagador e serviço
        Table dadosTable = new Table(new float[]{1.5f, 4f});
        dadosTable.setWidth(UnitValue.createPercentValue(100));
        
        // Recebi de
        adicionarLinhaDados(dadosTable, "RECEBI DE:", 
                recibo.getCliente().getNomeRazaoSocial(), true);
        
        // CPF/CNPJ do cliente
        if (recibo.getCliente().getCpfCnpj() != null && !recibo.getCliente().getCpfCnpj().isEmpty()) {
            String tipoDocCliente = recibo.getCliente().getCpfCnpj().length() > 11 ? "CNPJ" : "CPF";
            adicionarLinhaDados(dadosTable, tipoDocCliente + ":", 
                    FormatUtil.formatCPFouCNPJ(recibo.getCliente().getCpfCnpj()), false);
        }
        
        // Referente
        adicionarLinhaDados(dadosTable, "REFERENTE:", recibo.getReferente(), false);
        
        // Forma de pagamento
        if (recibo.getFormaPagamento() != null && !recibo.getFormaPagamento().isEmpty()) {
            adicionarLinhaDados(dadosTable, "PAGAMENTO:", recibo.getFormaPagamento(), false);
        }
        
        // Data de emissão
        adicionarLinhaDados(dadosTable, "DATA:", 
                recibo.getDataEmissao().format(DATE_FORMAT), false);
        
        container.add(dadosTable);
        
        // Observações (se houver)
        if (recibo.getObservacoes() != null && !recibo.getObservacoes().isEmpty()) {
            container.add(new Paragraph().setMarginBottom(5));
            container.add(new Paragraph("Observações: " + recibo.getObservacoes())
                    .setFontSize(8)
                    .setItalic()
                    .setFontColor(CINZA_MEDIO));
        }
        
        // ═══════════════════════════════════════════════════════════════
        // DECLARAÇÃO E ASSINATURA
        // ═══════════════════════════════════════════════════════════════
        container.add(new Paragraph().setMarginBottom(8));
        
        // Texto de quitação
        container.add(new Paragraph(
                "Declaro ter recebido a importância acima especificada e para clareza " +
                "firmo o presente recibo, dando plena, geral e irrevogável quitação.")
                .setFontSize(8)
                .setFontColor(CINZA_ESCURO)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMarginBottom(10));
        
        // Local, data e assinatura
        Table assinaturaTable = new Table(new float[]{1, 1});
        assinaturaTable.setWidth(UnitValue.createPercentValue(100));
        
        // Local e data
        String localData = "";
        if (recibo.getEmpresa().getCidade() != null) {
            localData = recibo.getEmpresa().getCidade();
            if (recibo.getEmpresa().getEstado() != null) {
                localData += "-" + recibo.getEmpresa().getEstado();
            }
            localData += ", ";
        }
        localData += recibo.getDataEmissao().format(DATE_FORMAT_EXTENSO);
        
        Cell localCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(localData)
                        .setFontSize(9)
                        .setFontColor(CINZA_ESCURO));
        assinaturaTable.addCell(localCell);
        
        // Assinatura
        Cell assCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
        
        assCell.add(new Paragraph("_________________________________")
                .setFontSize(10)
                .setFontColor(CINZA_MEDIO)
                .setMarginBottom(2));
        
        assCell.add(new Paragraph(recibo.getEmpresa().getNomeRazaoSocial())
                .setFontSize(8)
                .setBold()
                .setFontColor(PRETO));
        
        assCell.add(new Paragraph(tipoDoc + ": " + FormatUtil.formatCPFouCNPJ(recibo.getEmpresa().getCpfCnpj()))
                .setFontSize(7)
                .setFontColor(CINZA_MEDIO));
        
        assinaturaTable.addCell(assCell);
        container.add(assinaturaTable);
        
        document.add(container);
    }
    
    /**
     * Adiciona linha de dados formatada
     */
    private void adicionarLinhaDados(Table table, String label, String valor, boolean destaque) {
        Cell labelCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(4)
                .add(new Paragraph(label)
                        .setFontSize(8)
                        .setBold()
                        .setFontColor(CINZA_MEDIO));
        
        Cell valorCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(4)
                .add(new Paragraph(valor)
                        .setFontSize(destaque ? 10 : 9)
                        .setBold()
                        .setFontColor(destaque ? PRETO : CINZA_ESCURO));
        
        table.addCell(labelCell);
        table.addCell(valorCell);
    }
    
    /**
     * Monta string do endereço da empresa
     */
    private String montarEnderecoEmpresa(Recibo recibo) {
        StringBuilder sb = new StringBuilder();
        
        if (recibo.getEmpresa().getEndereco() != null && !recibo.getEmpresa().getEndereco().isEmpty()) {
            sb.append(recibo.getEmpresa().getEndereco());
            
            if (recibo.getEmpresa().getNumero() != null && !recibo.getEmpresa().getNumero().isEmpty()) {
                sb.append(", ").append(recibo.getEmpresa().getNumero());
            }
            
            if (recibo.getEmpresa().getBairro() != null && !recibo.getEmpresa().getBairro().isEmpty()) {
                sb.append(" - ").append(recibo.getEmpresa().getBairro());
            }
        }
        
        if (recibo.getEmpresa().getCidade() != null && !recibo.getEmpresa().getCidade().isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(recibo.getEmpresa().getCidade());
            
            if (recibo.getEmpresa().getEstado() != null) {
                sb.append("-").append(recibo.getEmpresa().getEstado());
            }
        }
        
        if (recibo.getEmpresa().getCep() != null && !recibo.getEmpresa().getCep().isEmpty()) {
            sb.append(" | CEP: ").append(FormatUtil.formatCEP(recibo.getEmpresa().getCep()));
        }
        
        return sb.toString();
    }
    
    /**
     * Monta string dos contatos da empresa
     */
    private String montarContatosEmpresa(Recibo recibo) {
        StringBuilder sb = new StringBuilder();
        
        if (recibo.getEmpresa().getTelefone() != null && !recibo.getEmpresa().getTelefone().isEmpty()) {
            sb.append("Tel: ").append(FormatUtil.formatTelefone(recibo.getEmpresa().getTelefone()));
        }
        
        if (recibo.getEmpresa().getCelular() != null && !recibo.getEmpresa().getCelular().isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Cel: ").append(FormatUtil.formatTelefone(recibo.getEmpresa().getCelular()));
        }
        
        if (recibo.getEmpresa().getEmail() != null && !recibo.getEmpresa().getEmail().isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(recibo.getEmpresa().getEmail());
        }
        
        return sb.toString();
    }
    
    /**
     * Cria linha separadora sutil
     */
    private Div criarLinhaSeparadora() {
        Table linha = new Table(1);
        linha.setWidth(UnitValue.createPercentValue(100));
        linha.setMarginTop(10);
        linha.setMarginBottom(10);
        
        Cell cell = new Cell()
                .setBorderTop(new SolidBorder(CINZA_CLARO, 1))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setHeight(1);
        
        linha.addCell(cell);
        
        Div div = new Div();
        div.add(linha);
        return div;
    }
    
}


