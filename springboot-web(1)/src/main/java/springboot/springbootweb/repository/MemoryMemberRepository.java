package springboot.springbootweb.repository;

import springboot.springbootweb.domain.Member;

import java.util.*;

//임시 구현체
public class MemoryMemberRepository implements MemberRepository {

    //임시 저장을 위한 맵
    //실무의 경우에는 공유 되는 이슈 있기 때문에 ConcurrentHashMap<> 사용
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id)); //널 가능성 고려
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny(); //찾으면 반환(1개라도)
    }

    @Override
    public List<Member> findAll() {
        //실무에서는 보통 return List
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
