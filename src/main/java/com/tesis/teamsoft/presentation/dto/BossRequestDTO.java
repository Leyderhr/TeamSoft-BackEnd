package com.tesis.teamsoft.presentation.dto;


import com.tesis.teamsoft.pojos.TeamFormationParameters;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BossRequestDTO {

    @Data
    public static class BossProposalRequestDTO {
        @Valid
        private TeamFormationParameters teamFormationParameters;
        private List<Long> projectIDs;
        private List<Long> groupIDs;
    }
}
