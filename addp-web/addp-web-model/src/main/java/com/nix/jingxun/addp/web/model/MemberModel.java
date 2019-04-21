package com.nix.jingxun.addp.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/20 23:30
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nix_member",indexes = {@Index(name = "index_username",columnList = "username",unique = true)})
public class MemberModel implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(length = 64)
    private String username;
    @Column(length = 32)
    private String password;
}
