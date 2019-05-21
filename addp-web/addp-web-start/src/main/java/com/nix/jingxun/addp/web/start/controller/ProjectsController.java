package com.nix.jingxun.addp.web.start.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.ssh.common.exception.ShellConnectException;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.diamond.ADDPEnvironment;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServiceRe;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2019/04/21 17:40
 */
@RestController
@RequestMapping("/projects")
public class ProjectsController  extends BaseController{

    @Resource
    private IProjectsService projectsService;

    @Resource
    private IServicesService servicesService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ProjectsModel projectsModel) {
        return Result.of(() -> {
            try {
                if (!projectsModel.getServicesModels().stream().allMatch(servicesModel -> servicesModel.getMemberId().equals(MemberCache.currentUser().getId()))) {
                    return Result.fail("1401", "no project permission " + projectsModel.getName());
                }
                // 检查项目正式环境主机数 如果为1创建备份主机
                List<ServicesModel> proServices = projectsModel.getServicesModels()
                        .stream()
                        .filter(service -> service.getEnvironment() == ADDPEnvironment.pro)
                        .collect(Collectors.toList());
                if (proServices.size() == 1) {
                    ServicesModel bakService = new ServicesModel();
                    BeanUtil.copyProperties(proServices.get(0),bakService);
                    bakService.setId(null);
                    bakService.setEnvironment(ADDPEnvironment.bak);
                    servicesService.save(bakService);
                    projectsModel.getProjectsServiceRes().add(ProjectsServiceRe.builder().servicesId(bakService.getId()).build());
                }
                projectsModel.setMemberId(MemberCache.currentUser().getId());
                return projectsService.save(projectsModel);
            } catch (ShellConnectException e) {
                return Result.fail("1404","服务器连接失败");
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }
        }).logFail();
    }
}
