package com.nix.jingxun.addp.web.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/21 13:09
 */

@Data
@Builder
@Entity
@Table(name = "nix_services")
public class ServicesModel implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "[\\d]{1,3}:[\\d]{1,3}:[\\d]{1,3}:[\\d]{1,3}")
    @Column(nullable = false)
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String sshKey;
    private Long memberId;

    public MemberModel getMember() {
        return null;
    }
}
