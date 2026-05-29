package com.tesis.teamsoft.pojos;

import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.persistence.entity.ProjectStructureEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ProjectStructureInfo {
    ProjectEntity project;
    ProjectStructureEntity projectStructure;
}
