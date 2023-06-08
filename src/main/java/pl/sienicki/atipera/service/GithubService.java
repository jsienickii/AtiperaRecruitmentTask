package pl.sienicki.atipera.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.GitNonForkRepos;
import pl.sienicki.atipera.dto.GitRepos;
import pl.sienicki.atipera.dto.GitReposWithBranches;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubClient githubClient;

    public GitNonForkRepos getAllNonForkRepos(String username) {
        return new GitNonForkRepos(username, getNonForkReposWithBranchesAndCommit(username));
    }

    private List<GitReposWithBranches> getNonForkReposWithBranchesAndCommit(String username) {
        return githubClient.getRepositoriesByUsername(username).stream()
                .filter(repo -> repo.forks_count() == 0)
                .map(repo -> mapToGitRepositoriesWithBranches(getBranchesFromGithub(username, repo.name()), repo))
                .toList();
    }

    private GitReposWithBranches mapToGitRepositoriesWithBranches(List<Branch> branchesByUsernameAndRepository, GitRepos repo) {
        return new GitReposWithBranches(repo.name(), branchesByUsernameAndRepository);
    }

    private List<Branch> getBranchesFromGithub(String username, String repo) {
        return githubClient.getBranchesByUsernameAndRepository(username, repo);
    }
}
