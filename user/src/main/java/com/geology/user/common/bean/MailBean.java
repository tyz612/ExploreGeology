package com.geology.user.common.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MailBean {
    private String to;

    private String theme;

    private String content;
}
