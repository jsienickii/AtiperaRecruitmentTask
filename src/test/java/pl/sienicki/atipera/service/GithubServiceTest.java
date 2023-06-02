package pl.sienicki.atipera.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.Commit;
import pl.sienicki.atipera.dto.GitRepositoriesWithBranches;
import pl.sienicki.atipera.dto.GitRepository;
import pl.sienicki.atipera.dto.Owner;
import pl.sienicki.atipera.dto.RepositoriesWithBranchesAndLastCommitByUsername;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GithubServiceTest {
    @Mock
    private GithubClient githubClient;

    public GithubServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void filterRepositoriesWithForks() {
        //given
        GithubService githubService = new GithubService(githubClient);
        String username = "john";
        Owner ownerJohn = new Owner("John");
        Commit commitOne = new Commit("12315");
        Commit commitTwo = new Commit("1231512412245");
        Branch branchOne = new Branch("Main", commitOne);
        Branch branchTwo = new Branch("Master", commitTwo);
        GitRepository gitRepository = new GitRepository("Repo1", ownerJohn, 0);
        GitRepository repositoryWithFork = new GitRepository("Repo2", ownerJohn, 4);
        List<GitRepository> gitRepositoryList = new ArrayList<>();
        gitRepositoryList.add(gitRepository);
        gitRepositoryList.add(repositoryWithFork);
        when(githubClient.getBranchesByUsernameAndRepository(eq(username), eq("Repo1")))
                .thenReturn(List.of(branchOne, branchTwo));
        when(githubClient.getBranchesByUsernameAndRepository(eq(username), eq("Repo2")))
                .thenReturn(List.of(branchOne, branchTwo));

        // when
        RepositoriesWithBranchesAndLastCommitByUsername result = githubService.getRepositoriesWithBranchAndLastCommit(username, gitRepositoryList);
        GitRepositoriesWithBranches resultRepo = result.getRepositories().get(0);

        //then
        verify(githubClient, times(1)).getBranchesByUsernameAndRepository(eq(username), eq("Repo1"));
        verifyNoMoreInteractions(githubClient);
        assertEquals(username, result.getOwnerLogin());
        assertEquals(1, result.getRepositories().size());
        assertEquals("Repo1", resultRepo.getRepositoryName());
        assertEquals(List.of(branchOne, branchTwo), resultRepo.getBranches());
    }
}