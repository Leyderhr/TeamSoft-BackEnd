package com.tesis.teamsoft.presentation.dto;

import com.tesis.teamsoft.pojos.TeamFormationParameters;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MembersRequestDTO {

    @Data
    public static class MemberProposalRequestDTO {
        @Valid
        private TeamFormationParameters teamFormationParameters;
        private Long projectId;
        private Long roleId;
        private List<Long> groupIDs;
    }
}
