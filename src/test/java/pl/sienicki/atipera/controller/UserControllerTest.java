package pl.sienicki.atipera.controller;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.sienicki.atipera.config.AcceptHeaderInterceptor;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.Commit;
import pl.sienicki.atipera.dto.GitNonForkRepos;
import pl.sienicki.atipera.dto.GitReposWithBranches;
import pl.sienicki.atipera.service.GithubService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private GithubService githubService;
    @Mock
    private FeignException.NotFound feignException;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        AcceptHeaderInterceptor acceptHeaderInterceptor = new AcceptHeaderInterceptor();
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionHandler())
                .addInterceptors(acceptHeaderInterceptor)
                .build();
    }

    @Test
    public void getOwnerLoginAndRepositoriesWithBranchesAndLastCommitByUsername() throws Exception {
        String username = "john";
        Commit commitOne = new Commit("12315");
        Commit commitTwo = new Commit("1231512412245");
        Branch branchOne = new Branch("Main", commitOne);
        Branch branchTwo = new Branch("Master", commitTwo);
        GitReposWithBranches gitRepositoriesWithBranchesOne = new GitReposWithBranches("Repo1", List.of(branchOne));
        GitReposWithBranches gitRepositoriesWithBranchesTwo = new GitReposWithBranches("Repo2", List.of(branchTwo));
        List<GitReposWithBranches> repositories = new ArrayList<>();
        repositories.add(gitRepositoriesWithBranchesOne);
        repositories.add(gitRepositoriesWithBranchesTwo);
        GitNonForkRepos response = new GitNonForkRepos(username, repositories);

        given(githubService.getAllNonForkRepos(username)).willReturn(response);

        mockMvc.perform(get("/" + username)
                        .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerLogin").value(username))
                .andExpect(jsonPath("$.repositories[0].repositoryName").value("Repo1"))
                .andExpect(jsonPath("$.repositories[0].branches[0].name").value("Main"))
                .andExpect(jsonPath("$.repositories[0].branches[0].commit.sha").value("12315"));
        verify(githubService).getAllNonForkRepos(username);
    }

    @Test
    void testGetReposByUsernameWithHeaderAcceptApplicationXmlThrows406() throws Exception {
        String username = "john";

        mockMvc.perform(get("/" + username)
                .header("Accept", "application/xml"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.message").value("The requested header: Accept:'application/xml' is not acceptable."));
    }
    @Test
    void testGetReposByUsernameThrowsFeignExceptionNotFound() throws Exception {
        String username = "john";

        given(githubService.getAllNonForkRepos(username)).willThrow(feignException);

        mockMvc.perform(get("/" + username)
                        .header("Accept", "application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

}