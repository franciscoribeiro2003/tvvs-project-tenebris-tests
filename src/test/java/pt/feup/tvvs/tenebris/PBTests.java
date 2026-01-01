package pt.feup.tvvs.tenebris;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange; // Import this
import pt.feup.tvvs.tenebris.utils.Vector2D;

class PBTests {

    // Constrain inputs to avoid Long overflow which isn't relevant for game logic
    @Property
    void vectorAdditionIsCommutative(@ForAll @IntRange(min = -10000, max = 10000) int x1,
                                     @ForAll @IntRange(min = -10000, max = 10000) int y1,
                                     @ForAll @IntRange(min = -10000, max = 10000) int x2,
                                     @ForAll @IntRange(min = -10000, max = 10000) int y2) {
        Vector2D v1 = new Vector2D(x1, y1);
        Vector2D v2 = new Vector2D(x2, y2);

        Vector2D r1 = v1.add(v2);
        Vector2D r2 = v2.add(v1);

        assert r1.x() == r2.x() && r1.y() == r2.y();
    }

    @Property
    void vectorSubtractionReversesAddition(@ForAll @IntRange(min = -10000, max = 10000) int x1,
                                           @ForAll @IntRange(min = -10000, max = 10000) int y1,
                                           @ForAll @IntRange(min = -10000, max = 10000) int x2,
                                           @ForAll @IntRange(min = -10000, max = 10000) int y2) {
        Vector2D v1 = new Vector2D(x1, y1);
        Vector2D v2 = new Vector2D(x2, y2);

        Vector2D result = v1.add(v2).minus(v2);

        assert result.x() == v1.x() && result.y() == v1.y();
    }

    @Property
    void magnitudeIsAlwaysPositive(@ForAll @IntRange(min = -10000, max = 10000) int x,
                                   @ForAll @IntRange(min = -10000, max = 10000) int y) {
        Vector2D v = new Vector2D(x, y);
        assert v.magnitude() >= 0;
    }
}