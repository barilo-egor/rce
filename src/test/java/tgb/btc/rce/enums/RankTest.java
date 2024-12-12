package tgb.btc.rce.enums;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankTest {

    @Test
    void getByDealsNumber() {
        Map<Rank, Set<Integer>> rankIntegerMap = new EnumMap<>(Rank.class);
        rankIntegerMap.put(Rank.FIRST, Set.of(0, 5, 9));
        rankIntegerMap.put(Rank.SECOND, Set.of(10, 15, 19));
        rankIntegerMap.put(Rank.THIRD, Set.of(20, 34, 49));
        rankIntegerMap.put(Rank.FOURTH, Set.of(50, 59, 79));
        rankIntegerMap.put(Rank.FIFTH, Set.of(80, 100, 101));
        rankIntegerMap.put(Rank.SIXTH, Set.of(110, 111, 149));
        rankIntegerMap.put(Rank.SEVENTH, Set.of(150, 198, 199));
        rankIntegerMap.put(Rank.EIGHTH, Set.of(200, 248, 299));
        rankIntegerMap.put(Rank.NINTH, Set.of(300, 487, 499));
        rankIntegerMap.put(Rank.TENTH, Set.of(500, 1000000));
        for (Rank rank : Rank.values()) {
            for (Integer dealsNumber : rankIntegerMap.get(rank)) {
                assertEquals(rank, Rank.getByDealsNumber(dealsNumber));
            }
        }
    }
}