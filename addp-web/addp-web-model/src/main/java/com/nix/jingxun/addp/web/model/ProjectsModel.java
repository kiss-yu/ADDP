package com.nix.jingxun.addp.web.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/21 13:20
 */

@Data
@Builder
@Entity
@Table(name = "nix_projects")
public class ProjectsModel implements Serializable {
    @Id

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gitUrl;

    private String gitUsername;

    private String gitPassword;

    private String gitToken;

    private String gitKey;

    private String master;

    private Long memberId;

    @NotNull
    private Long servicesId;
}
