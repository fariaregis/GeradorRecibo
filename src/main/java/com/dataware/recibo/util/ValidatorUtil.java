package com.dataware.recibo.util;

/**
 * Utilitário para validação de dados
 */
public class ValidatorUtil {

    /**
     * Valida CPF
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Calcula primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) {
                primeiroDigito = 0;
            }

            // Calcula segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) {
                segundoDigito = 0;
            }

            // Verifica se os dígitos calculados conferem
            return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
                   Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida CNPJ
     */
    public static boolean validarCNPJ(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            // Calcula primeiro dígito verificador
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso1[i];
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) {
                primeiroDigito = 0;
            }

            // Calcula segundo dígito verificador
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso2[i];
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) {
                segundoDigito = 0;
            }

            // Verifica se os dígitos calculados conferem
            return Character.getNumericValue(cnpj.charAt(12)) == primeiroDigito &&
                   Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida e-mail
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    /**
     * Valida CEP
     */
    public static boolean validarCEP(String cep) {
        if (cep == null) {
            return false;
        }
        cep = cep.replaceAll("\\D", "");
        return cep.length() == 8;
    }

    /**
     * Valida telefone (10 ou 11 dígitos)
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null) {
            return false;
        }
        telefone = telefone.replaceAll("\\D", "");
        return telefone.length() == 10 || telefone.length() == 11;
    }
}



