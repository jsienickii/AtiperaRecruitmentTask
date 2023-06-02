package pl.sienicki.atipera.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.sienicki.atipera.dto.Branch;
import pl.sienicki.atipera.dto.GitRepository;

import java.util.List;

@FeignClient(name = "githubClient",url = "${client.api.baseUrl}")

public interface GithubClient {
    @GetMapping(value = "/users/{username}/repos")
    List<GitRepository> getRepositoriesByUsername(@PathVariable String username);
    @GetMapping(value = "/repos/{username}/{repo}/branches")
    List<Branch> getBranchesByUsernameAndRepository(@PathVariable String username, @PathVariable String repo);
}
