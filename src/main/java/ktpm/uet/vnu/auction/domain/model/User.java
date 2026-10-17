package ktpm.uet.vnu.auction.domain.model;

/** Tài khoản người dùng. Java thuần, không dùng annotation của framework. */
public record User(Long id, String username, String passwordHash) {
}
