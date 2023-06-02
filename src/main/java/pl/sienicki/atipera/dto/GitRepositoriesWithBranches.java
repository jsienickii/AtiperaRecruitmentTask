package pl.sienicki.atipera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitRepositoriesWithBranches {
    private String repositoryName;
    private List<Branch> branches;
}
