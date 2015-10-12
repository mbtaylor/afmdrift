
public class SamplePos {

    public int ix;
    public int iy;
    public short phase;

    public SamplePos( int ix, int iy, short phase ) {
        this.ix = ix;
        this.iy = iy;
        this.phase = phase;
    }

    @Override
    public String toString() {
        return "(" + ix + ", " + iy + "; " + phase + ")";
    }
}
