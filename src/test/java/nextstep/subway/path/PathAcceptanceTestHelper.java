package nextstep.subway.path;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;

public class PathAcceptanceTestHelper {
    private PathAcceptanceTestHelper() {
    }

    public static ExtractableResponse<Response> 최단_경로_조회_요청(long source, long target) {
        return RestAssured
            .given().log().all()
            .when()
            .get("/paths?source=" + source + "&target=" + target)
            .then().log().all()
            .extract();
    }

    public static void 최단_경로_조회_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(PathResponse.class)).isNotNull();
    }

    public static void 최단경로_조회_예상된_결과_응답됨(ExtractableResponse<Response> response, PathResponse expected) {
        PathResponse actual = response.as(PathResponse.class);
        assertThat(actual).isEqualTo(expected);
    }

    public static void 최단_경로_조회_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 최단_경로_조회한_역이_존재하지_않음(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}