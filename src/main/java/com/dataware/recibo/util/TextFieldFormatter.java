package com.dataware.recibo.util;

import javafx.scene.control.TextField;

/**
 * Utilitário para aplicar formatação automática em campos de texto
 */
public class TextFieldFormatter {

    /**
     * Aplica formatação de CPF: 000.000.000-00
     */
    public static void formatarCPF(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 11 dígitos
            if (numeros.length() > 11) {
                numeros = numeros.substring(0, 11);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 3 || i == 6) {
                    formatado.append(".");
                } else if (i == 9) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo sem disparar o listener novamente
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação de CNPJ: 00.000.000/0000-00
     */
    public static void formatarCNPJ(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 14 dígitos
            if (numeros.length() > 14) {
                numeros = numeros.substring(0, 14);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 2 || i == 5) {
                    formatado.append(".");
                } else if (i == 8) {
                    formatado.append("/");
                } else if (i == 12) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo sem disparar o listener novamente
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação dinâmica de CPF ou CNPJ
     * Detecta automaticamente baseado no tamanho
     */
    public static void formatarCPFouCNPJ(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 14 dígitos (CNPJ)
            if (numeros.length() > 14) {
                numeros = numeros.substring(0, 14);
            }

            // Aplica formatação apropriada
            StringBuilder formatado = new StringBuilder();
            
            if (numeros.length() <= 11) {
                // Formato CPF
                for (int i = 0; i < numeros.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatado.append(".");
                    } else if (i == 9) {
                        formatado.append("-");
                    }
                    formatado.append(numeros.charAt(i));
                }
            } else {
                // Formato CNPJ
                for (int i = 0; i < numeros.length(); i++) {
                    if (i == 2 || i == 5) {
                        formatado.append(".");
                    } else if (i == 8) {
                        formatado.append("/");
                    } else if (i == 12) {
                        formatado.append("-");
                    }
                    formatado.append(numeros.charAt(i));
                }
            }

            // Atualiza o campo sem disparar o listener novamente
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação de CEP: 00000-000
     */
    public static void formatarCEP(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 8 dígitos
            if (numeros.length() > 8) {
                numeros = numeros.substring(0, 8);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 5) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação de telefone fixo: (00) 0000-0000
     */
    public static void formatarTelefone(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 10 dígitos
            if (numeros.length() > 10) {
                numeros = numeros.substring(0, 10);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 0) {
                    formatado.append("(");
                } else if (i == 2) {
                    formatado.append(") ");
                } else if (i == 6) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação de celular: (00) 00000-0000
     */
    public static void formatarCelular(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 11 dígitos
            if (numeros.length() > 11) {
                numeros = numeros.substring(0, 11);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 0) {
                    formatado.append("(");
                } else if (i == 2) {
                    formatado.append(") ");
                } else if (i == 7) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Aplica formatação inteligente de telefone/celular
     * Detecta automaticamente se é fixo (10) ou celular (11)
     */
    public static void formatarTelefoneOuCelular(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            // Remove tudo que não é número
            String numeros = newValue.replaceAll("\\D", "");

            // Limita a 11 dígitos
            if (numeros.length() > 11) {
                numeros = numeros.substring(0, 11);
            }

            // Aplica a formatação
            StringBuilder formatado = new StringBuilder();
            for (int i = 0; i < numeros.length(); i++) {
                if (i == 0) {
                    formatado.append("(");
                } else if (i == 2) {
                    formatado.append(") ");
                } else if ((numeros.length() == 10 && i == 6) || (numeros.length() == 11 && i == 7)) {
                    formatado.append("-");
                }
                formatado.append(numeros.charAt(i));
            }

            // Atualiza o campo
            if (!formatado.toString().equals(newValue)) {
                textField.setText(formatado.toString());
                textField.positionCaret(formatado.length());
            }
        });
    }

    /**
     * Permite apenas números no campo
     */
    public static void apenasNumeros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Limita o número de caracteres no campo
     */
    public static void limitarCaracteres(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });
    }
}



