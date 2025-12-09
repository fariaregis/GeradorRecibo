package com.dataware.recibo.service;

import com.dataware.recibo.model.Recibo;
import com.dataware.recibo.util.FormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Serviço para geração de PDFs de recibos
 */
public class ReciboPDFService {
    private static final Logger logger = LoggerFactory.getLogger(ReciboPDFService.class);
    
    // Paleta de cores profissional e elegante
    private static final DeviceRgb AZUL_PRINCIPAL = new DeviceRgb(31, 58, 96);      // Azul corporativo
    private static final DeviceRgb AZUL_CLARO = new DeviceRgb(52, 152, 219);       // Azul suave
    private static final DeviceRgb VERDE_DESTAQUE = new DeviceRgb(46, 204, 113);   // Verde vibrante
    private static final DeviceRgb CINZA_CLARO = new DeviceRgb(245, 247, 250);     // Fundo suave
    private static final DeviceRgb CINZA_TEXTO = new DeviceRgb(52, 73, 94);        // Texto secundário
    private static final DeviceRgb CINZA_BORDA = new DeviceRgb(223, 230, 233);     // Bordas suaves
    private static final DeviceRgb BRANCO = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb PRETO = new DeviceRgb(44, 62, 80);              // Preto suave

    /**
     * Gera PDF do recibo
     */
    public void gerarPDF(Recibo recibo, String caminhoArquivo) throws IOException {
        logger.info("Gerando PDF do recibo: " + recibo.getNumeroRecibo());

        File file = new File(caminhoArquivo);
        file.getParentFile().mkdirs();

        PdfWriter writer = new PdfWriter(caminhoArquivo);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // Cabeçalho (nome da empresa + "Recibo de Prestação de Serviços")
        adicionarTitulo(document, recibo);
        
        // Número do recibo
        adicionarNumeroRecibo(document, recibo);
        
        // Valor destacado em verde
        adicionarValorDestacado(document, recibo);
        
        // Dados do Cliente e descrição
        adicionarDadosCliente(document, recibo);
        
        // Assinatura
        adicionarAssinatura(document, recibo);
        
        // Rodapé
        adicionarRodape(document);

        document.close();
        logger.info("PDF gerado com sucesso: " + caminhoArquivo);
    }

    private void adicionarTitulo(Document document, Recibo recibo) {
        // Nome da empresa centralizado
        Paragraph nomeEmpresa = new Paragraph(recibo.getEmpresa().getNomeRazaoSocial().toUpperCase())
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(AZUL_PRINCIPAL)
                .setMarginBottom(5);
        document.add(nomeEmpresa);
        
        // Subtítulo
        String categoria = recibo.getCategoria() != null ? recibo.getCategoria().getNome() : "Prestação de Serviços";
        Paragraph subtitulo = new Paragraph("Recibo de " + categoria)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(CINZA_TEXTO)
                .setMarginBottom(10);
        document.add(subtitulo);
        
        // Linha separadora elegante
        Table linha = new Table(1);
        linha.setWidth(UnitValue.createPercentValue(100));
        Cell cellLinha = new Cell()
                .add(new Paragraph(""))
                .setBorderTop(new SolidBorder(AZUL_PRINCIPAL, 2))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setHeight(1)
                .setMarginBottom(15);
        linha.addCell(cellLinha);
        document.add(linha);
    }
    
    private void adicionarNumeroRecibo(Document document, Recibo recibo) {
        // Número e data lado a lado
        Table infoTable = new Table(new float[]{1, 1});
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell cellNumero = new Cell()
                .add(new Paragraph("RECIBO Nº " + recibo.getNumeroRecibo())
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(AZUL_PRINCIPAL))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(CINZA_CLARO)
                .setPadding(10);
        
        Cell cellData = new Cell()
                .add(new Paragraph("EMISSÃO: " + FormatUtil.formatData(recibo.getDataEmissao()))
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(AZUL_PRINCIPAL)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(CINZA_CLARO)
                .setPadding(10);
        
        infoTable.addCell(cellNumero);
        infoTable.addCell(cellData);
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }
    
    private void adicionarValorDestacado(Document document, Recibo recibo) {
        // Valor em destaque moderado
        Table valorTable = new Table(1);
        valorTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell valorCell = new Cell()
                .add(new Paragraph("Valor: " + FormatUtil.formatMoeda(recibo.getValor()))
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(AZUL_PRINCIPAL))
                .add(new Paragraph("(" + recibo.getValorExtenso() + ")")
                        .setFontSize(11)
                        .setItalic()
                        .setFontColor(CINZA_TEXTO)
                        .setMarginTop(3))
                .setBackgroundColor(new DeviceRgb(240, 245, 250))
                .setBorder(new SolidBorder(CINZA_BORDA, 1))
                .setPadding(12)
                .setMarginBottom(15);
        
        valorTable.addCell(valorCell);
        document.add(valorTable);
    }

    private void adicionarDadosEmpresa(Document document, Recibo recibo) {
        // Box com título "EMITENTE"
        Table tableTitle = new Table(1);
        tableTitle.setWidth(UnitValue.createPercentValue(100));
        
        Cell cellTitle = new Cell()
                .add(new Paragraph("EMITENTE")
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(BRANCO))
                .setBackgroundColor(AZUL_CLARO)
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
        
        tableTitle.addCell(cellTitle);
        document.add(tableTitle);
        
        // Dados da empresa
        Table table = new Table(1);
        table.setWidth(UnitValue.createPercentValue(100));
        
        StringBuilder empresaInfo = new StringBuilder();
        
        // Nome em destaque
        empresaInfo.append(recibo.getEmpresa().getNomeRazaoSocial().toUpperCase());
        
        if (recibo.getEmpresa().getNomeFantasia() != null && !recibo.getEmpresa().getNomeFantasia().isEmpty()) {
            empresaInfo.append("\n").append(recibo.getEmpresa().getNomeFantasia());
        }
        
        empresaInfo.append("\n");
        
        // Documento
        String tipoDoc = "J".equals(recibo.getEmpresa().getTipoPessoa()) ? "CNPJ: " : "CPF: ";
        empresaInfo.append(tipoDoc).append(FormatUtil.formatCPFouCNPJ(recibo.getEmpresa().getCpfCnpj()));
        
        if (recibo.getEmpresa().getRgInscricaoEstadual() != null && !recibo.getEmpresa().getRgInscricaoEstadual().isEmpty()) {
            String tipoIE = "J".equals(recibo.getEmpresa().getTipoPessoa()) ? " | IE: " : " | RG: ";
            empresaInfo.append(tipoIE).append(recibo.getEmpresa().getRgInscricaoEstadual());
        }
        
        // Endereço
        String endereco = recibo.getEmpresa().getEnderecoCompleto();
        if (endereco != null && !endereco.isEmpty()) {
            empresaInfo.append("\n").append(endereco);
            if (recibo.getEmpresa().getBairro() != null) {
                empresaInfo.append(", ").append(recibo.getEmpresa().getBairro());
            }
            if (recibo.getEmpresa().getCidade() != null) {
                empresaInfo.append(" - ").append(recibo.getEmpresa().getCidade());
                if (recibo.getEmpresa().getEstado() != null) {
                    empresaInfo.append("/").append(recibo.getEmpresa().getEstado());
                }
            }
            if (recibo.getEmpresa().getCep() != null) {
                empresaInfo.append(" - CEP ").append(FormatUtil.formatCEP(recibo.getEmpresa().getCep()));
            }
        }
        
        // Contatos
        if (recibo.getEmpresa().getTelefone() != null || recibo.getEmpresa().getCelular() != null) {
            empresaInfo.append("\n");
            if (recibo.getEmpresa().getTelefone() != null) {
                empresaInfo.append("☎ ").append(FormatUtil.formatTelefone(recibo.getEmpresa().getTelefone()));
            }
            if (recibo.getEmpresa().getCelular() != null) {
                if (recibo.getEmpresa().getTelefone() != null) {
                    empresaInfo.append("  ");
                }
                empresaInfo.append("📱 ").append(FormatUtil.formatTelefone(recibo.getEmpresa().getCelular()));
            }
        }
        
        if (recibo.getEmpresa().getEmail() != null) {
            empresaInfo.append("\n✉ ").append(recibo.getEmpresa().getEmail());
        }
        
        Cell cell = new Cell()
                .add(new Paragraph(empresaInfo.toString()).setFontSize(10))
                .setBorder(new SolidBorder(CINZA_BORDA, 1))
                .setBackgroundColor(CINZA_CLARO)
                .setPadding(12);
        
        table.addCell(cell);
        document.add(table);
    }

    private void adicionarCorpoRecibo(Document document, Recibo recibo) {
        // Este método agora está vazio pois movemos o conteúdo para outros métodos
        // A ordem agora é: título → cliente → assinatura
    }

    private void adicionarDadosCliente(Document document, Recibo recibo) {
        // Recebi(emos) de com destaque
        Table recebiTable = new Table(new float[]{1.2f, 3.8f});
        recebiTable.setWidth(UnitValue.createPercentValue(100));
        
        recebiTable.addCell(new Cell()
                .add(new Paragraph("Recebi(emos) de:")
                        .setFontSize(11)
                        .setBold())
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(10)
                .setPaddingBottom(8));
        
        recebiTable.addCell(new Cell()
                .add(new Paragraph(recibo.getCliente().getNomeRazaoSocial().toUpperCase())
                        .setFontSize(11)
                        .setBold())
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(8));
        
        // Referente a
        recebiTable.addCell(new Cell()
                .add(new Paragraph("Referente a:")
                        .setFontSize(11)
                        .setBold())
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(10)
                .setPaddingBottom(8));
        
        recebiTable.addCell(new Cell()
                .add(new Paragraph(recibo.getReferente())
                        .setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(8));
        
        document.add(recebiTable);
        
        // Linha separadora sutil
        document.add(new Paragraph("\n"));
        
        // Dados adicionais
        // Endereço
        String endereco = recibo.getCliente().getEnderecoCompleto();
        if (endereco != null && !endereco.isEmpty()) {
            StringBuilder endCompleto = new StringBuilder(endereco);
            if (recibo.getCliente().getBairro() != null) {
                endCompleto.append(", ").append(recibo.getCliente().getBairro());
            }
            if (recibo.getCliente().getCidade() != null) {
                endCompleto.append(", ").append(recibo.getCliente().getCidade());
                if (recibo.getCliente().getEstado() != null) {
                    endCompleto.append(", ").append(recibo.getCliente().getEstado());
                }
            }
            
            Paragraph endP = new Paragraph("Endereço: " + endCompleto.toString())
                    .setFontSize(10)
                    .setMarginBottom(5);
            document.add(endP);
        }
        
        // CPF/CNPJ
        if (recibo.getCliente().getCpfCnpj() != null && !recibo.getCliente().getCpfCnpj().isEmpty()) {
            Paragraph cpfCnpj = new Paragraph("CPF/CNPJ: " + FormatUtil.formatCPFouCNPJ(recibo.getCliente().getCpfCnpj()))
                    .setFontSize(10)
                    .setMarginBottom(5);
            document.add(cpfCnpj);
        }
        
        // Forma de Pagamento
        if (recibo.getFormaPagamento() != null && !recibo.getFormaPagamento().isEmpty()) {
            Paragraph pgto = new Paragraph("Forma de Pagamento: " + recibo.getFormaPagamento())
                    .setFontSize(10)
                    .setMarginBottom(15);
            document.add(pgto);
        }
        
        document.add(new Paragraph("\n"));
        
        // Texto de quitação em box discreto
        Table quitacaoTable = new Table(1);
        quitacaoTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell quitacaoCell = new Cell()
                .add(new Paragraph(
                        "Para maior clareza, firmo o presente recibo, que comprova o recebimento integral do valor " +
                        "mencionado, concedendo quitação plena, geral e irrevogável pela quantia recebida.")
                        .setFontSize(10)
                        .setItalic()
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setFontColor(CINZA_TEXTO))
                .setBackgroundColor(new DeviceRgb(250, 250, 250))
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
                .setMarginBottom(20);
        
        quitacaoTable.addCell(quitacaoCell);
        document.add(quitacaoTable);
        
        // Observações (se houver)
        if (recibo.getObservacoes() != null && !recibo.getObservacoes().isEmpty()) {
            Paragraph obs = new Paragraph("Observações: " + recibo.getObservacoes())
                    .setFontSize(10)
                    .setItalic()
                    .setMarginBottom(15);
            document.add(obs);
        }
    }

    private void adicionarAssinatura(Document document, Recibo recibo) {
        // Local e Data
        String cidadeEstado = recibo.getEmpresa().getCidade() != null ? 
                recibo.getEmpresa().getCidade().toUpperCase() : "";
        if (recibo.getEmpresa().getEstado() != null) {
            cidadeEstado += "-" + recibo.getEmpresa().getEstado().toUpperCase();
        }
        
        Paragraph localData = new Paragraph(cidadeEstado + ", " + FormatUtil.formatData(recibo.getDataEmissao()))
                .setFontSize(11)
                .setMarginBottom(35);
        document.add(localData);
        
        // Espaço para assinatura manual
        document.add(new Paragraph("\n\n\n"));
        
        // Linha de assinatura centralizada
        Table linhaTbl = new Table(1);
        linhaTbl.setWidth(UnitValue.createPercentValue(50));
        linhaTbl.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        
        Cell linhaCell = new Cell()
                .add(new Paragraph(""))
                .setBorderTop(new SolidBorder(PRETO, 1))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER);
        
        linhaTbl.addCell(linhaCell);
        document.add(linhaTbl);
        
        // Emitente
        Paragraph emitente = new Paragraph("Emitente: " + recibo.getEmpresa().getNomeRazaoSocial().toUpperCase())
                .setFontSize(11)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(8)
                .setMarginBottom(5);
        document.add(emitente);
        
        // CPF/CNPJ
        Paragraph cpfCnpj = new Paragraph(
                ("F".equals(recibo.getEmpresa().getTipoPessoa()) ? "CPF:" : "CNPJ:") + 
                FormatUtil.formatCPFouCNPJ(recibo.getEmpresa().getCpfCnpj()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(CINZA_TEXTO)
                .setMarginBottom(5);
        document.add(cpfCnpj);
        
        // Site (se houver)
        if (recibo.getEmpresa().getSite() != null && !recibo.getEmpresa().getSite().isEmpty()) {
            Paragraph site = new Paragraph(recibo.getEmpresa().getSite())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(AZUL_CLARO);
            document.add(site);
        }
    }

    private void adicionarRodape(Document document) {
        document.add(new Paragraph("\n\n"));
        
        // Linha separadora
        Table tableLinha = new Table(1);
        tableLinha.setWidth(UnitValue.createPercentValue(100));
        
        Cell cellLinha = new Cell()
                .add(new Paragraph(""))
                .setBorderTop(new SolidBorder(CINZA_BORDA, 1))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER);
        
        tableLinha.addCell(cellLinha);
        document.add(tableLinha);
        
        // Rodapé com informações legais
        Paragraph rodape = new Paragraph()
                .add("Documento gerado eletronicamente • ")
                .add("Validade jurídica conforme Art. 585 do Código Civil")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(CINZA_TEXTO)
                .setMarginTop(8);
        
        document.add(rodape);
    }
}

