package org.springshop.cart_service.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class User {
    private Integer id;
    private String sub;
    private String username;
}
