
public class PixelPos {
    public int ix_;
    public int iy_;
    public PixelPos( int ix, int iy ) {
        ix_ = ix;
        iy_ = iy;
    }

    @Override
    public String toString() {
        return "(" + ix_ + ", " + iy_ + ")";
    }
}
