package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     */
    @Test
    void outerTxOff_success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장된다.
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * MemberService    @Transactional:OFF
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        String username = "로그예외_outerTxOff_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then: member는 저장되지만, log는 롤백된다.
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:OFF
     * LogRepository    @Transactional:OFF
     */
    @Test
    void singleTx() {
        //given
        String username = "singleTx";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장된다.
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON
     */
    @Test
    void outerTxOn_success() {
        //given
        String username = "outerTxOn_success";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장된다.
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isPresent()).isTrue();
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        String username = "로그예외_outerTxOn_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then: member, log 둘 다 롤백된다.
        assertThat(memberRepository.find(username).isEmpty()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //then: member, log 둘 다 롤백된다.
        assertThat(memberRepository.find(username).isEmpty()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_success";

        //when
        memberService.joinV2(username);

        //then: member 커밋, log 롤백
        assertThat(memberRepository.find(username).isPresent()).isTrue();
        assertThat(logRepository.find(username).isEmpty()).isTrue();
    }

}