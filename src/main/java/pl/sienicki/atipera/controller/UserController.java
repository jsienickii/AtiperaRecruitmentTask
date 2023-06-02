package pl.sienicki.atipera.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.sienicki.atipera.client.GithubClient;
import pl.sienicki.atipera.dto.GitRepository;
import pl.sienicki.atipera.dto.RepositoriesWithBranchesAndLastCommitByUsername;
import pl.sienicki.atipera.exception.CustomExceptionResponse;
import pl.sienicki.atipera.service.GithubService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final GithubService githubService;
    private final GithubClient githubClient;

    @GetMapping("/{username}")
    public ResponseEntity<Object> getReposByUsername(
            @PathVariable String username,
            @RequestHeader(value = "Accept") String acceptHeader) throws HttpMediaTypeNotAcceptableException {
        if (acceptHeader.equalsIgnoreCase("application/xml")) {
            throw new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_XML));
        }
        try {
            List<GitRepository> repositoriesByUsername = githubClient.getRepositoriesByUsername(username);
            RepositoriesWithBranchesAndLastCommitByUsername response = githubService.getRepositoriesWithBranchAndLastCommit(username, repositoriesByUsername);
            return ResponseEntity.ok(response);
        } catch (FeignException.NotFound ex) {
            CustomExceptionResponse feignError = new CustomExceptionResponse(ex.status(), "User not found");
            return ResponseEntity.status(ex.status()).body(feignError);
        }
    }
}

