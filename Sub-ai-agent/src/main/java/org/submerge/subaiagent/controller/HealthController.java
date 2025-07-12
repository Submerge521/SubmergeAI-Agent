package org.submerge.subaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: HealthController
 * Package: org.submerge.subaiagent.controller
 * Description:
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/8 21:37
 * @Version 1.0
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String health() {
        return "OK";
    }
}
