package pl.sienicki.atipera.dto;

import java.util.List;
public record GitReposWithBranches(String repositoryName, List<Branch> branches) {


}
