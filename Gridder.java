
public class Gridder {

    private final int nx_;
    private final int ny_;

    public Gridder( int nx, int ny ) {
        nx_ = nx;
        ny_ = ny;
    }

    public int pixelCount() {
        return nx_ * ny_;
    }

    public int sampleCount() {
        return 4 * nx_ * ny_;
    }

    public int nx() {
        return nx_;
    }

    public int ny() {
        return ny_;
    }

    public int pixelIndex( PixelPos ppos ) {
        int ix = ppos.ix_;
        int iy = ppos.iy_;
        return inRange( ix, iy )
             ? ix + nx_ * iy 
             : -1;
    }

    public int sampleIndex( SamplePos spos ) {
        int ix = spos.ix_;
        int iy = spos.iy_;
        switch ( spos.phase_ ) {
            case 0: return iy * 2 * nx_ + ix;
            case 1: return ( iy + 1 ) * 2 * nx_ - ix - 1;
            case 2: return ( ny_ * nx_ * 4 ) - ( iy * 2 * nx_ ) + ix;
            case 3: return ( ny_ * nx_ * 4 ) - ( ( iy + 1 ) * 2 * nx_ ) - ix -1;
            default: return -1;
        }
    }

    public PixelPos pixelPos( int pIndex ) {
        int ix = pIndex % nx_;
        int iy = pIndex / nx_;
        return inRange( ix, iy ) ? new PixelPos( ix, iy ) : null;
    }

    public SamplePos samplePos( int sIndex ) {
        int iTrace = sIndex / nx_;
        int iStep = sIndex % nx_;
        boolean xFwd = iTrace % 2 == 0;
        boolean yFwd = iTrace < 2 * ny_;
        short phase = (short) ( ( xFwd ? 0 : 1 ) + ( yFwd ? 0 : 2 ) );
        int ix = xFwd ? iStep : nx_ - 1 - iStep;
        int iy = yFwd ? ( iTrace / 2 ) : ny_ * 2 - 1 - ( iTrace / 2 );
        return new SamplePos( ix, iy, phase );
    }

    public boolean inRange( int ix, int iy ) {
        return ix >= 0 && ix < nx_
            && iy >= 0 && iy < ny_;
    }
}
