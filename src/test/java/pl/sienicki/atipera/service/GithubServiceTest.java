package pl.sienicki.atipera.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.Commit;
import pl.sienicki.atipera.dto.GitReposWithBranches;
import pl.sienicki.atipera.dto.GitRepos;
import pl.sienicki.atipera.dto.Owner;
import pl.sienicki.atipera.dto.GitNonForkRepos;

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
        GitRepos gitRepositoryWithoutForks = new GitRepos("Repo1", ownerJohn, 0);
        GitRepos repositoryWithFork = new GitRepos("Repo2", ownerJohn, 4);
        List<GitRepos> gitRepositoryList = List.of(gitRepositoryWithoutForks,repositoryWithFork);
        when(githubClient.getRepositoriesByUsername(username)).thenReturn(gitRepositoryList);
        when(githubClient.getBranchesByUsernameAndRepository(eq(username), eq("Repo1")))
                .thenReturn(List.of(branchOne, branchTwo));
        when(githubClient.getBranchesByUsernameAndRepository(eq(username), eq("Repo2")))
                .thenReturn(List.of(branchOne, branchTwo));

        // when
        GitNonForkRepos result = githubService.getAllNonForkRepos(username);
        GitReposWithBranches resultRepo = result.repositories().get(0);

        //then
        verify(githubClient, times(1)).getBranchesByUsernameAndRepository(eq(username), eq("Repo1"));
        assertEquals(username, result.ownerLogin());
        assertEquals(1, result.repositories().size());
        assertEquals("Repo1", resultRepo.repositoryName());
        assertEquals(List.of(branchOne, branchTwo), resultRepo.branches());
    }
}