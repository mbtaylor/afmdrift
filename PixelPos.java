
public class PixelPos {
    public int ix;
    public int iy;
    public PixelPos( int ix, int iy ) {
        this.ix = ix;
        this.iy = iy;
    }

    @Override
    public String toString() {
        return "(" + ix + ", " + iy + ")";
    }
}
