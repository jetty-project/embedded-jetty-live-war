package jetty.boot;

public class Main
{
    public static void main(String[] args)
    {
        new Main().run();
    }

    private void run()
    {
        
        System.err.printf("In %s%n",this.getClass().getName());
    }
}
