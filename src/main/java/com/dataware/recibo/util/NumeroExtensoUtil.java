package com.dataware.recibo.util;

import java.math.BigDecimal;

/**
 * Utilitário para converter números em extenso
 */
public class NumeroExtensoUtil {

    private static final String[] UNIDADES = {
        "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove"
    };

    private static final String[] DEZ_A_DEZENOVE = {
        "dez", "onze", "doze", "treze", "quatorze", "quinze", 
        "dezesseis", "dezessete", "dezoito", "dezenove"
    };

    private static final String[] DEZENAS = {
        "", "", "vinte", "trinta", "quarenta", "cinquenta", 
        "sessenta", "setenta", "oitenta", "noventa"
    };

    private static final String[] CENTENAS = {
        "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos",
        "seiscentos", "setecentos", "oitocentos", "novecentos"
    };

    /**
     * Converte um valor BigDecimal em extenso
     */
    public static String valorPorExtenso(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
            return "zero reais";
        }

        long parteInteira = valor.longValue();
        int centavos = valor.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();

        StringBuilder extenso = new StringBuilder();

        // Parte inteira
        if (parteInteira > 0) {
            extenso.append(numeroPorExtenso(parteInteira));
            if (parteInteira == 1) {
                extenso.append(" real");
            } else {
                extenso.append(" reais");
            }
        }

        // Centavos
        if (centavos > 0) {
            if (parteInteira > 0) {
                extenso.append(" e ");
            }
            extenso.append(numeroPorExtenso(centavos));
            if (centavos == 1) {
                extenso.append(" centavo");
            } else {
                extenso.append(" centavos");
            }
        }

        return extenso.toString();
    }

    /**
     * Converte um número inteiro em extenso
     */
    private static String numeroPorExtenso(long numero) {
        if (numero == 0) {
            return "zero";
        }

        if (numero < 0) {
            return "menos " + numeroPorExtenso(-numero);
        }

        StringBuilder resultado = new StringBuilder();

        // Milhões
        if (numero >= 1000000) {
            long milhoes = numero / 1000000;
            if (milhoes == 1) {
                resultado.append("um milhão");
            } else {
                resultado.append(numeroPorExtenso(milhoes)).append(" milhões");
            }
            numero %= 1000000;
            if (numero > 0) {
                resultado.append(numero < 100 ? " e " : ", ");
            }
        }

        // Milhares
        if (numero >= 1000) {
            long milhares = numero / 1000;
            if (milhares == 1) {
                resultado.append("mil");
            } else {
                resultado.append(numeroPorExtenso(milhares)).append(" mil");
            }
            numero %= 1000;
            if (numero > 0) {
                resultado.append(numero < 100 ? " e " : ", ");
            }
        }

        // Centenas
        if (numero >= 100) {
            if (numero == 100) {
                resultado.append("cem");
                return resultado.toString();
            }
            resultado.append(CENTENAS[(int) numero / 100]);
            numero %= 100;
            if (numero > 0) {
                resultado.append(" e ");
            }
        }

        // Dezenas e unidades
        if (numero >= 20) {
            resultado.append(DEZENAS[(int) numero / 10]);
            numero %= 10;
            if (numero > 0) {
                resultado.append(" e ");
            }
        } else if (numero >= 10) {
            resultado.append(DEZ_A_DEZENOVE[(int) numero - 10]);
            return resultado.toString();
        }

        // Unidades
        if (numero > 0) {
            resultado.append(UNIDADES[(int) numero]);
        }

        return resultado.toString();
    }
}



