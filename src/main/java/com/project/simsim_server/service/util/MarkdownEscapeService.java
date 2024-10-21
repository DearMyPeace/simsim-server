package com.project.simsim_server.service.util;

import org.springframework.stereotype.Component;

@Component
public class MarkdownEscapeService {

    public String escapeMarkdown(String text) {
        if (text == null) return null;
        return text.replaceAll("([*_~`])", "\\\\$1");
    }
}
