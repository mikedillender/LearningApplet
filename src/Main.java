import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class Main extends Applet implements Runnable {

    private final int WIDTH=1900, HEIGHT=900;

    private Thread thread;
    Graphics gfx;
    Image img;

    float[][] map;
    int vectors=0;
    boolean done=false;
    int movetimer=20;


    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        thread=new Thread(this);
        thread.start();
        map=new float[WIDTH][HEIGHT];
    }

    public void paint(Graphics g){
        drawwater(gfx);
        g.drawImage(img,0,0,this);
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME
        if (!done) {
            if (vectors < 14) {
                movetimer -= 1;
                if (movetimer < 0) {
                    addVector();
                    movetimer = 20;
                }
            } else {
                done=true;
                createThreshhold();
            }
        }
        repaint();//UPDATES FRAME
        try{ Thread.sleep(20); } //ADDS TIME BETWEEN FRAMES (FPS)
        catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }

    public void createThreshhold(){
        //float threshold=.5f;
        float[][] map1=map;

        float mid=getAvgInRange(0, 1);
        float q1=getAvgInRange(0, mid);
        float q3=getAvgInRange(mid, 1);


        for (int x=0; x<map.length; x++){
            for (int y=0; y<map[0].length; y++){
                if (map[x][y]<q1){
                    map[x][y]=0;
                }else if (map[x][y]<mid){
                    map[x][y]=.33f;
                }else if (map[x][y]<q3){
                    map[x][y]=.66f;
                }else{
                    map[x][y]=1.0f;
                }
            }
        }
    }

    public float getAvgInRange(float low, float high){
        int numTiles=0;
        float sum=0;
        for (int x=0; x<map.length; x++){
            for (int y=0; y<map[0].length; y++){
                if (map[x][y]<high&&map[x][y]>low){
                    numTiles++;
                    sum+=map[x][y];
                }
            }
        }
        float avg=sum/(float)numTiles;
        return avg;
    }

    public void addVector(){
        int[] v=new int[]{(int)(Math.random()*WIDTH),(int)(Math.random()*HEIGHT),(int)(Math.random()*WIDTH),(int)(Math.random()*HEIGHT)};
        if (v[1]==v[3]||v[0]==v[2]){return;}
        float slope=(float) (v[1]-v[0])/(float) (v[0]-v[2]);
        float b=v[1]-(slope*v[0]);
        float pslope=-(1/slope);
        float weight=1/((float)vectors+1);
        for (int x=0; x<map.length; x++){
            for (int y=0; y<map[0].length; y++){
                float pb1=y-(pslope*x);
                float xint=(b-pb1)/(pslope-slope);
                float value=(float)Math.sin(((xint-v[0])/(v[2]-v[0]))*6.28)+1;
                value/=2;
                map[x][y] = (map[x][y] * (1 - weight)) + (value * weight);
            }
        }
        vectors++;
    }


    public void drawwater(Graphics gfx){
        for (int x=0; x<map.length; x++){
            for (int y=0; y<map[0].length; y++){
                gfx.setColor(new Color((int)(map[x][y]*255),(int)(map[x][y]*255),(int)(map[x][y]*255)));
                gfx.fillRect(x , y , 1,1);
            }
        }
    }
}