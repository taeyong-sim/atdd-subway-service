package nextstep.subway.line.domain;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.BadRequestApiException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        setLine(line);
        setUpStation(upStation);
        setDownStation(downStation);
        setDistance(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public void setLine(Line line) {
        if (line == null) {
            throw new IllegalArgumentException("line은 null일 수 없습니다.");
        }
        this.line = line;
    }

    public void setUpStation(Station upStation) {
        if (upStation == null) {
            throw new IllegalArgumentException("upStation은 null일 수 없습니다.");
        }
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        if (downStation == null) {
            throw new IllegalArgumentException("downStation은 null일 수 없습니다.");
        }
        this.downStation = downStation;
    }

    public void setDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("distance는 1이상이어야 합니다.");
        }
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void updateUpStation(Station station, int newDistance) {
        if (this.distance <= newDistance) {
            throw new BadRequestApiException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
        this.upStation = station;
        this.distance -= newDistance;
    }

    public void updateDownStation(Station station, int newDistance) {
        if (this.distance <= newDistance) {
            throw new BadRequestApiException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
        this.downStation = station;
        this.distance -= newDistance;
    }

    public boolean isEqualUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isExist(Station upStation, Station downStation) {
        return (isEqualUpStation(upStation) && isEqualDownStation(downStation))
                || (isEqualUpStation(downStation) && isEqualDownStation(upStation));
    }
}
