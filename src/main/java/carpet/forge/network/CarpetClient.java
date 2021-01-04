package carpet.forge.network;

public class CarpetClient
{
    public static final int HI = 69;
    public static final int HELLO = 420;
    public static final int RULES = 1;
    public static final int TICK_RATE = 2;
    
    private static boolean isCarpetServer = false;
    public static String serverCarpetVersion;;
    
    public static void setCarpet()
    {
        isCarpetServer = true;
    }
}
