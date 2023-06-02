package pl.sienicki.atipera.dto;

public record GitRepository(String name, Owner owner, int forks_count) {
}

