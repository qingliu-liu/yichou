package com.liu.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignSuccessVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String token;

}
