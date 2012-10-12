package yuraku.kimetaru.repo;

import java.util.List;

import yuraku.kimetaru.domain.Member;

public interface MemberDao
{
    public Member findById(Long id);

    public Member findByEmail(String email);

    public List<Member> findAllOrderedByName();

    public void register(Member member);
}
