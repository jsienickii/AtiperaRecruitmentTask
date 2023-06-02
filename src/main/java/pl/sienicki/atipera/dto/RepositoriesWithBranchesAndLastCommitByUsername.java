package pl.sienicki.atipera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoriesWithBranchesAndLastCommitByUsername {
    private String ownerLogin;
    private List<GitRepositoriesWithBranches> repositories;

}
