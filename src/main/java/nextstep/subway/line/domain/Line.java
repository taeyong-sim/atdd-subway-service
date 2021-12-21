package nextstep.subway.line.domain;

import nextstep.subway.BaseEntity;
import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.BadRequestApiException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    private int extraFare;

    @Embedded
    private final Sections sections = Sections.empty();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.addSection(Section.of(this, upStation, downStation, distance));
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance, int extraFare) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        sections.addSection(Section.of(this, upStation, downStation, distance));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance, int extraFare) {
        return new Line(name, color, upStation, downStation, distance, extraFare);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getExtraFare() {
        return extraFare;
    }

    public Sections getSections() {
        return sections;
    }

    public Stations getStations() {
        Stations stations = Stations.empty();
        if (sections.isEmpty()) {
            return stations;
        }

        Station station = findUpStation();
        stations.add(station);

        while (sections.hasSectionByUpStation(station)) {
            Section nextSection = sections.getSectionByUpStation(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section newSection = Section.of(this, upStation, downStation, distance);
        sections.addSection(newSection);
    }

    public void deleteSection(Station station) {
        validateDeleteStation(station);

        if (isFirstStation(station)) {
            sections.deleteByUpStation(station);
            return;
        }

        if (isLastStation(station)) {
            sections.deleteByDownStation(station);
            return;
        }

        sections.deleteByBetweenStation(station);
    }

    private boolean isFirstStation(Station station) {
        Station upStation = findUpStation();
        return station.equals(upStation);
    }

    private boolean isLastStation(Station station) {
        Station downStation = findDownStation();
        return station.equals(downStation);
    }

    private Station findUpStation() {
        Station station = sections.getFirstSection().getUpStation();

        while (sections.hasSectionByDownStation(station)) {
            Section nextSection = sections.getSectionByDownStation(station);
            station = nextSection.getUpStation();
        }
        return station;
    }

    private Station findDownStation() {
        Station station = sections.getFirstSection().getDownStation();

        while (sections.hasSectionByUpStation(station)) {
            Section nextSection = sections.getSectionByUpStation(station);
            station = nextSection.getDownStation();
        }
        return station;
    }

    private void validateDeleteStation(Station station) {
        if (!sections.hasDeletableSection()) {
            throw new BadRequestApiException(ErrorCode.CAN_NOT_REMOVE_SECTION);
        }
        if (getStations().notContains(station)) {
            throw new BadRequestApiException(ErrorCode.NOT_REGISTERED_STATION_TO_LINE);
        }
    }
}
