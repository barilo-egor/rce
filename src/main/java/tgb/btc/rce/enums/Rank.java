package tgb.btc.rce.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum Rank {
    FIRST(0, "\uD83D\uDC76", x -> x < 10),
    SECOND(0.1, "\uD83D\uDE0A", x -> x >= 10 && x < 20),
    THIRD(0.25, "\uD83E\uDD13", x -> x >= 20 && x < 50),
    FOURTH(0.5, "\uD83D\uDE0E", x -> x >= 50 && x < 80),
    FIFTH(0.75, "\uD83D\uDE08", x -> x >= 80 && x < 110),
    SIXTH(1, "\uD83D\uDC8E", x -> x >= 110 && x < 150),
    SEVENTH(1.25, "\uD83E\uDDB8\u200D♂️", x -> x >= 150 && x < 200),
    EIGHTH(1.5, "\uD83E\uDDB9\u200D♂️", x -> x >= 200 && x < 300),
    NINTH(1.75, "\uD83E\uDD77", x -> x >= 300 && x < 500),
    TENTH(2, "\uD83E\uDDFF", x -> x >= 500);

    final double percent;
    final String smile;
    final Predicate<Integer> isItInTheRange;

    Rank(double percent, String smile, Predicate<Integer> predicate) {
        this.percent = percent;
        this.smile = smile;
        this.isItInTheRange = predicate;
    }

    public static Rank getByDealsNumber(int dealsNumber) {
        Rank[] ranks = Rank.values();
        Optional<Rank> rank = Arrays.stream(ranks).filter(p -> p.isItInTheRange.test(dealsNumber)).findFirst();
        return rank.orElseThrow(IllegalStateException::new);
    }

    public double getPercent() {
        return percent;
    }

    public String getSmile() {
        return smile;
    }

    public Predicate<Integer> getIsItInTheRange() {
        return isItInTheRange;
    }
}
