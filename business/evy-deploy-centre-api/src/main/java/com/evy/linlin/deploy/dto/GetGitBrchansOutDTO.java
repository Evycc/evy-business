package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.*;

import java.util.List;

/**
 * getGitBrchans OutDTO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:12
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetGitBrchansOutDTO extends OutDTO {
    private String gitPath;
    private List<String> branchs;
}
