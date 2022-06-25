package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.member.dto.MemberRequest;
import org.springframework.http.MediaType;

public class RestAssuredRequest {

    public static ExtractableResponse<Response> post(String path, Object request) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> postWithOAuth(String path, Object request, String token) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> getWithOAuth(String path, String token) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> put(String path, Object request) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> putWithOAuth(String path, Object request, String token) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteWithOAuth(String path, String token) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .when().delete(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 로그인_요청(String email, String password) {
        TokenRequest tokenRequest = new TokenRequest(email, password);

        return post("/login/token", tokenRequest);
    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(memberRequest)
            .when().post("/members")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return get(uri);
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return put(uri, memberRequest);
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return delete(uri);
    }

    public static ExtractableResponse<Response> 내_정보_조회_요청(String token) {
        return getWithOAuth("/members/me", token);
    }

}
