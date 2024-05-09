package com.demo.security.dto;

import lombok.*;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    private String key;
    private List<String> services;

}
