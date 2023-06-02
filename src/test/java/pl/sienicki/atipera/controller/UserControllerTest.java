package pl.sienicki.atipera.controller;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.Commit;
import pl.sienicki.atipera.dto.GitRepositoriesWithBranches;
import pl.sienicki.atipera.dto.GitRepository;
import pl.sienicki.atipera.dto.Owner;
import pl.sienicki.atipera.dto.RepositoriesWithBranchesAndLastCommitByUsername;
import pl.sienicki.atipera.exception.CustomExceptionResponse;
import pl.sienicki.atipera.service.GithubService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private GithubService githubService;

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    public void getOwnerLoginAndRepositoriesWithBranchesAndLastCommitByUsername() throws Exception {
        String username = "john";
        Owner ownerJohn = new Owner("John");
        Commit commitOne = new Commit("12315");
        Commit commitTwo = new Commit("1231512412245");
        Branch branchOne = new Branch("Main", commitOne);
        Branch branchTwo = new Branch("Master", commitTwo);
        GitRepository gitRepository = new GitRepository("Repo1", ownerJohn, 0);
        GitRepository gitRepository2 = new GitRepository("Repo2", ownerJohn, 4);
        List<GitRepository> gitRepositoryList = new ArrayList<>();
        gitRepositoryList.add(gitRepository);
        gitRepositoryList.add(gitRepository2);
        GitRepositoriesWithBranches gitRepositoriesWithBranchesOne = new GitRepositoriesWithBranches("Repo1", List.of(branchOne));
        GitRepositoriesWithBranches gitRepositoriesWithBranchesTwo = new GitRepositoriesWithBranches("Repo2", List.of(branchTwo));
        List<GitRepositoriesWithBranches> repositories = new ArrayList<>();
        repositories.add(gitRepositoriesWithBranchesOne);
        repositories.add(gitRepositoriesWithBranchesTwo);
        RepositoriesWithBranchesAndLastCommitByUsername response = new RepositoriesWithBranchesAndLastCommitByUsername(username, repositories);

        given(githubClient.getRepositoriesByUsername(username)).willReturn(gitRepositoryList);
        given(githubService.getRepositoriesWithBranchAndLastCommit(username, gitRepositoryList)).willReturn(response);

        mockMvc.perform(get("/" + username)
                        .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerLogin").value(username))
                .andExpect(jsonPath("$.repositories[0].repositoryName").value("Repo1"))
                .andExpect(jsonPath("$.repositories[0].branches[0].name").value("Main"))
                .andExpect(jsonPath("$.repositories[0].branches[0].commit.sha").value("12315"));
        verify(githubClient).getRepositoriesByUsername(username);
        verify(githubService).getRepositoriesWithBranchAndLastCommit(username, gitRepositoryList);
    }

    @Test
    public void userNotFoundOnGithubReturn404AndCorrectMessage() throws Exception {
        //given
        String username = "john";
        FeignException.NotFound feignException = (FeignException.NotFound) FeignException.errorStatus("GET", Response.builder()
                .status(404)
                .reason("User not found")
                .request(Request.create(Request.HttpMethod.GET, "", new HashMap<>(), null, Util.UTF_8, new RequestTemplate()))
                .build());
        when(githubClient.getRepositoriesByUsername(username))
                .thenThrow(feignException);
        //when
        ResponseEntity<Object> response = userController.getReposByUsername(username, "application/json");
        CustomExceptionResponse responseBody = (CustomExceptionResponse) response.getBody();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.status());
        assertEquals("User not found", responseBody.message());
        verify(githubClient).getRepositoriesByUsername(username);
    }


    @Test
    public void acceptHeaderApplicationXMLNotAcceptableReturn406AndCorrectMessage() throws Exception {
        mockMvc.perform(get("/xxx")
                        .header("Accept", "application/xml"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value(406))
                .andExpect(jsonPath("$.message").value("The requested header: Accept:'application/xml' is not acceptable."));

        verifyNoInteractions(githubClient);
        verifyNoInteractions(githubService);
    }
}