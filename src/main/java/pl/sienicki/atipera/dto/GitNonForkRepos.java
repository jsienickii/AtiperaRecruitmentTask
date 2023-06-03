package pl.sienicki.atipera.dto;

import java.util.List;

public record GitNonForkRepos(String ownerLogin, List<GitReposWithBranches> repositories) {

}
