// Servicio para enviar correos electrónicos de verificación
package com.churninsight_dev.backend_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Bean para enviar correos
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía el código de verificación al correo del usuario.
     * Lanza excepción si el correo es inválido.
     */
    public void sendVerificationCode(String to, String code) {
        if (to == null || to.trim().isEmpty() || !to.matches("^.+@.+\\..+")) {
            throw new IllegalArgumentException("Dirección de correo inválida: " + to);
        }

        // Crear y enviar el mensaje de verificación
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to.trim());
        message.setSubject("Código de Verificación para tu Cuenta");
        message.setText("Hola,\n\nGracias por registrarte. Tu código de verificación es: " + code + "\n\nEste código expirará en 24 horas.\n\nSaludos.");
        mailSender.send(message);
    }
}
