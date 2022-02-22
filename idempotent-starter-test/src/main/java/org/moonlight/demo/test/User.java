package org.moonlight.demo.test;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Moonlight
 */
@Data
public class User implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private Integer age;
}