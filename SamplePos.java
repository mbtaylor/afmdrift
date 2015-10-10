
public class SamplePos {

    public int ix_;
    public int iy_;
    public short phase_;

    public SamplePos( int ix, int iy, short phase ) {
        ix_ = ix;
        iy_ = iy;
        phase_ = phase;
    }

    @Override
    public String toString() {
        return "(" + ix_ + ", " + iy_ + "; " + phase_ + ")";
    }
}
