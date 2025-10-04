package com.elearning.service;

import com.elearning.dto.EmailDTO;

public interface EmailService {
    void sendEmail(EmailDTO emailDTO);
    void sendHtmlEmail(EmailDTO emailDTO);
    void sendTemplateEmail(EmailDTO emailDTO);
    void sendBulkEmail(EmailDTO emailDTO);
}