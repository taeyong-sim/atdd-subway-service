package nextstep.subway.member;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.member.dto.MemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.AcceptanceTestHelper.로그인_되어_있음;
import static nextstep.subway.utils.AcceptanceTestHelper.회원_삭제됨;
import static nextstep.subway.utils.AcceptanceTestHelper.회원_생성됨;
import static nextstep.subway.utils.AcceptanceTestHelper.회원_정보_수정됨;
import static nextstep.subway.utils.AcceptanceTestHelper.회원_정보_조회됨;
import static nextstep.subway.utils.RestAssuredRequest.*;

public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;

    /**
     * Feature: 회원 정보 관리 기능
     *
     *   Scenario: 회원 정보 관리
     *     When 회원 등록 요청
     *     Then 회원 등록됨
     *
     *     When 회원 정보 조회 요청
     *     Then 회원 정보 조회됨
     *
     *     When 회원 정보 수정 요청
     *     Then 회원 정보 수정됨
     *
     *     When 회원 삭제 요청
     *     Then 회원 삭제됨
     */
    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    /**
     * Feature: 내 정보 관리 기능
     *
     *   Background
     *     Given 회원 등록되어 있음
     *     And 로그인 되어 있음
     *     And 토큰을 발급받는다
     *
     *    Scenario: 내 정보 관리
     *     When 토큰을 사용하여 정보 조회 요청
     *     Then 내 정보 조회됨
     *
     *     When 토큰을 사용하여 정보 수정 요청
     *     Then 내 정보 수정됨
     *
     *     When 토큰을 사용하여 정보 삭제 요청
     *     Then 내 정보 삭제됨
     */
    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // given
        회원_생성을_요청(EMAIL, PASSWORD, AGE);
        String token = 로그인_되어_있음(EMAIL, PASSWORD);

        // when
        ExtractableResponse<Response> findResponse = 내_정보_조회_요청(token);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 내_정보_수정_요청(token, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // given
        token = 로그인_되어_있음(NEW_EMAIL, NEW_PASSWORD);
        // when
        ExtractableResponse<Response> deleteResponse = 내_정보_삭제_요청(token);
        // then
        회원_삭제됨(deleteResponse);
    }

    private ExtractableResponse<Response> 내_정보_수정_요청(String accessToken, String email, String password, int age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return putWithOAuth("/members/me", memberRequest, accessToken);
    }

    private ExtractableResponse<Response> 내_정보_삭제_요청(String accessToken) {
        return deleteWithOAuth("/members/me", accessToken);
    }
}
