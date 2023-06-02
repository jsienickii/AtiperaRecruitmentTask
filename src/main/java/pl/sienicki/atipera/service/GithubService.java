package pl.sienicki.atipera.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.GitRepositoriesWithBranches;
import pl.sienicki.atipera.dto.GitRepository;
import pl.sienicki.atipera.dto.RepositoriesWithBranchesAndLastCommitByUsername;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubClient githubClient;

    public RepositoriesWithBranchesAndLastCommitByUsername getRepositoriesWithBranchAndLastCommit(String username, List<GitRepository> repositoriesByUsername) {
        return buildResponse(username, filterRepositoriesAndGetBranchesFromClient(username, repositoriesByUsername));
    }

    private static RepositoriesWithBranchesAndLastCommitByUsername buildResponse(String username, List<GitRepositoriesWithBranches> gitRepositoriesWithoutForksWithBranchesList) {
        return RepositoriesWithBranchesAndLastCommitByUsername.builder()
                .ownerLogin(username)
                .repositories(gitRepositoriesWithoutForksWithBranchesList)
                .build();
    }

    private List<GitRepositoriesWithBranches> filterRepositoriesAndGetBranchesFromClient(String username, List<GitRepository> repositoriesByUsername) {
        return repositoriesByUsername.stream()
                .filter(repo -> repo.forks_count() == 0)
                .map(repo -> mapToGitRepositoriesWithBranches(getBranchesAndCommitsByUsernameAndRepositoryFromClient(username, repo.name()), repo))
                .toList();
    }

    private GitRepositoriesWithBranches mapToGitRepositoriesWithBranches(List<Branch> branchesByUsernameAndRepository, GitRepository repo) {
        return GitRepositoriesWithBranches.builder()
                .repositoryName(repo.name())
                .branches(branchesByUsernameAndRepository)
                .build();
    }

    private List<Branch> getBranchesAndCommitsByUsernameAndRepositoryFromClient(String username, String repo) {
        return githubClient.getBranchesByUsernameAndRepository(username, repo);
    }
}
