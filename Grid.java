
public class Grid {

    private final int nx_;
    private final int ny_;

    public Grid( int nx, int ny ) {
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
        int ix = ppos.ix;
        int iy = ppos.iy;
        return inRange( ix, iy )
             ? ix + nx_ * iy 
             : -1;
    }

    public int sampleIndex( SamplePos spos ) {
        int ix = spos.ix;
        int iy = spos.iy;
        switch ( spos.phase ) {
            case 0: return iy * 2 * nx_ + ix;
            case 1: return ( iy + 1 ) * 2 * nx_ - ix - 1;
            case 2: return ( ny_ * nx_ * 4 ) - ( ( iy + 1 ) * 2 * nx_ ) + ix;
            case 3: return ( ny_ * nx_ * 4 ) - ( ( iy ) * 2 * nx_ ) - ix - 1;
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

    public static void main( String[] args ) {
        int nx = Integer.parseInt( args[ 0 ] );
        int ny = Integer.parseInt( args[ 1 ] );
        Grid g = new Grid( nx, ny );
        for ( int is = 0; is < g.sampleCount(); is++ ) {
            System.out.println( is + "\t" + g.samplePos( is ) );
        }
    }
}
