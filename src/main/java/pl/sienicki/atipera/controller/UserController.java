package pl.sienicki.atipera.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.sienicki.atipera.config.annotation.ValidatedAcceptHeader;
import pl.sienicki.atipera.dto.GitNonForkRepos;
import pl.sienicki.atipera.service.GithubService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final GithubService githubService;

    @GetMapping("/{username}")
    public ResponseEntity<GitNonForkRepos> getReposByUsername(
            @PathVariable String username, @ValidatedAcceptHeader String acceptHeader) {
        GitNonForkRepos response = githubService.getAllNonForkRepos(username);
        return ResponseEntity.ok(response);
    }
}

