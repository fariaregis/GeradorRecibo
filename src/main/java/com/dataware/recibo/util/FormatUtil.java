package com.dataware.recibo.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilitário para formatação de dados
 */
public class FormatUtil {
    
    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(LOCALE_BR);
    
    /**
     * Formata CPF: 123.456.789-00
     */
    public static String formatCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return "";
        }
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9);
    }

    /**
     * Formata CNPJ: 12.345.678/0001-90
     */
    public static String formatCNPJ(String cnpj) {
        if (cnpj == null || cnpj.isEmpty()) {
            return "";
        }
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.length() != 14) {
            return cnpj;
        }
        return cnpj.substring(0, 2) + "." + 
               cnpj.substring(2, 5) + "." + 
               cnpj.substring(5, 8) + "/" + 
               cnpj.substring(8, 12) + "-" + 
               cnpj.substring(12);
    }

    /**
     * Formata CPF ou CNPJ automaticamente
     */
    public static String formatCPFouCNPJ(String documento) {
        if (documento == null || documento.isEmpty()) {
            return "";
        }
        String numeros = documento.replaceAll("\\D", "");
        if (numeros.length() == 11) {
            return formatCPF(numeros);
        } else if (numeros.length() == 14) {
            return formatCNPJ(numeros);
        }
        return documento;
    }

    /**
     * Formata CEP: 12345-678
     */
    public static String formatCEP(String cep) {
        if (cep == null || cep.isEmpty()) {
            return "";
        }
        cep = cep.replaceAll("\\D", "");
        if (cep.length() != 8) {
            return cep;
        }
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    /**
     * Formata telefone: (11) 1234-5678
     */
    public static String formatTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            return "";
        }
        telefone = telefone.replaceAll("\\D", "");
        if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + 
                   telefone.substring(2, 6) + "-" + 
                   telefone.substring(6);
        } else if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + 
                   telefone.substring(2, 7) + "-" + 
                   telefone.substring(7);
        }
        return telefone;
    }

    /**
     * Formata valor monetário: R$ 1.234,56
     */
    public static String formatMoeda(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }
        return CURRENCY_FORMAT.format(valor);
    }

    /**
     * Formata data: 08/12/2025
     */
    public static String formatData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Formata data e hora: 08/12/2025 14:30:00
     */
    public static String formatDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    /**
     * Remove formatação deixando apenas números
     */
    public static String removeFormatacao(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replaceAll("\\D", "");
    }
}



