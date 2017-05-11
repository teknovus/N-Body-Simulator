import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.applet.*;  
/**
 * Main.java  
 *
 * @author: Josh Hu
 * Version: 1.1
 * 
 * Brief Program Description:
 * 
 * This simulation visually shows planets in orbit of a sun with (reasonably) accurate physics and math
 * By reasonably accurate, all values are essentially the square of what they should be in reality to maintain accuracy of the smaller values
 * e.g. 1 billion meters is instead calculated as 1 billion billion meters 
 * another e.g. default timestep it 10 billion seconds per iteration
 * 
 * It utilizes a totally brute force approach towards calculations
 * This makes it a little slow 
 * Adding more planets makes it exponentially slower, n^2 slower in fact (https://en.wikipedia.org/wiki/N-body_simulation#Calculation_optimizations)
 * 
 * No Enhanced For loops are used in this simulation due to the fact that arraylist bodies can be changed and
 * enhanced for loops have issues when the list they iterate through is changed mid loop
 *
 * When adding bodies via clicking, sometimes the program does not respond, I think it may because it won't accept the mouseevent input if its 
 * in the middle of calculating or looping or something
 */
public class Main extends Applet implements MouseListener
{
    public final static double scale=250/1e18;          //who cares if its public if its final
    public final static double G = 6.67e-11; //oh no they might discover the universal constant of gravitation!

    //actual variables
    private static ArrayList<Body> bodies = new ArrayList<Body>(); //all bodies
    private static boolean pause=false;
    private static int centerx = 500;
    private static int centery = 500;
    private static int N=2;

    
    private Image img0,img1,img2,img3,img4,img5,img6,bg;
    private final static double solMass = 1.98892e30;

    public void init(){ 
        addBodies(N);// creates a bunch of bodies
        
        bodies.set(0, new Body(0,0,0,0,1e6*solMass,0));//Sol at center
        //load images
        img0=getImage(getCodeBase(), "sun.png");
        img1=getImage(getCodeBase(), "earth.png");
        img2=getImage(getCodeBase(), "barren.png");
        img3=getImage(getCodeBase(), "dust.png");
        img4=getImage(getCodeBase(), "tundra.png");
        img5=getImage(getCodeBase(), "fe.png");
        img6=getImage(getCodeBase(), "neptune.png");
        
        bg=getImage(getCodeBase(), "bg.jpg");
        
        addMouseListener(this);
    }

    /**
     * Animates the motion of the bodies
     */
    public void paint(Graphics g) {
        g.drawImage(bg,0,0,this); //paint background
        g.translate(centerx,centery);//create a cartesian plane with origin at 500 500

        if(!pause){//run only if sim is not paused
            for(int i=0;i<bodies.size();i++) {
                int x=(int) Math.round(bodies.get(i).getx()*scale);//fetch and scale paint positions
                int y=(int) Math.round(bodies.get(i).gety()*scale);
                if( x<-600 || x>600 || y<-600 || y>600){ //remove body if it goes too far off screen
                    bodies.remove(i);
                    i--;
                }
                switch(bodies.get(i).getimg()){ //call image number
                    case 0:g.drawImage(img0,x-24,y-24,48,48,this);break; //paint sun
                    case 1:g.drawImage(img1,x-12,y-12,24,24,this);break; //paint planetary bodies
                    case 2:g.drawImage(img2,x-12,y-12,24,24,this);break;
                    case 3:g.drawImage(img3,x-12,y-12,24,24,this);break;
                    case 4:g.drawImage(img4,x-12,y-12,24,24,this);break;
                    case 5:g.drawImage(img5,x-12,y-12,24,24,this);break;
                    case 6:g.drawImage(img6,x-12,y-12,24,24,this);break;
                }
            }
            //pass one tick
            tick();
            //go through the same process again until applet is stopped/paused
            repaint();
        }
    }

    /**
     * Adds n bodies to the simulation and arraylist, randomly assigning position and mass, mathematically assigning velocity
     * @param int - number of bodies to be added
     * @return none
     */
    public static void addBodies(int n){
        double r = 1e18; // radius of simulated universe
        for (int i = 0; i < n; i++) {
            double x = r*(.5-Math.random()); //places a body in a random position within the confines of the
            double y = r*(.5-Math.random()); //size of the universe and also at least 5 px from the sun
            double v = circlev(x,y); //calculate magnitude of velocity

            double absangle = Math.atan(Math.abs(y/x)); //calculate angle of the body
            double theta= Math.PI/2-absangle; //calculate theta
            double vx   = -1*Math.signum(y)*Math.cos(theta)*v; //calculate x vector, signum is to fetch sign, trig to get component of v
            double vy   = Math.signum(x)*Math.sin(theta)*v; //calculate y vector, x vector sign is inverted to work with maths
            //Pick Clockwise or counterclockwise
            if (Math.random() <=.5) {
                vx=-vx;
                vy=-vy;
            } 

            double mass = Math.random()*solMass*1+1e20; //random mass within acceptable bounds (some random percentage of solar mass+1e20 minimum)
            int img=(int)(Math.random()*6)+1;// random image number, 1-6 inclusive
            bodies.add( new Body(x, y, vx, vy, mass,img));
        } 
    }

    /**
     * calculates the magnitude of circular velocity of the body (speed, unvectorized)
     * @param doubles - coordinates
     * @return double - velocity
     */
    public static double circlev(double x, double y) {
        double r2=Math.sqrt(x*x+y*y);
        double numerator=G*1e6*solMass;
        return Math.sqrt(numerator/r2);
    }

    /**
     * Calculates the distance between two points
     * @param double - 4 values,2 coordinate pairs being calculated between
     * @return double - distance between coordinates
     */
    private static double distance(double x1,double y1, double x2, double y2){
        return (  Math.sqrt( Math.pow((x2-x1),2) + Math.pow((y2-y1),2) )  );
    }

    /**
     * Calculates the distance between two bodies
     * @param Body - 2 bodies being calculated between
     * @return double - distance between bodies
     */
    private static double distance(Body star, Body planet){
        return (  Math.sqrt( (planet.getx()-star.getx())*(planet.getx()-star.getx()) + (planet.gety()-star.gety())*(planet.gety()-star.gety()) )  );
    }

    /**
     * Performs one iteration of calculations for all bodies on the screen, setting the net force and calling update
     * @param none
     * @return none
     */
    private static void tick(){
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).setfx(0.0); //reset forces for new iteration
            bodies.get(i).setfy(0.0);
            //2 loops = bad for processor
            for (int j = 0; j < bodies.size(); j++) {
                if (i != j){ //a body cannot accelerate itself, and if it did, it would calculate infinity
                    bodies.get(i).addForce(bodies.get(j)); //accelerate body i by body j
                }
            }
        }
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).update(); //update velocities and position
        }
    }

    /**
     * Places a new body in a circular orbit at the point the user clicks
     * @param MouseEvent - Object from event interface that can has the mouse location
     * @return none
     */
    public  void mouseClicked(MouseEvent e){
        if(! (Math.abs(e.getX()-centerx)<36 && Math.abs(e.getY()-centery)<36) ){ // only run if body is being placed some distance from the sun
            double x = (e.getX()-centerx)/scale; //places a body in a random position within the confines of the
            double y = (e.getY()-centery)/scale; //size of the universe and also at least 5 px from the sun
            double v = circlev(x,y); //calculate magnitude of velocity

            double absangle = Math.atan(Math.abs(y/x)); //calculate absolute angle of the body
            double theta= Math.PI/2-absangle; //calculate relative angle useful for calculations
            double vx   = -1*Math.signum(y)*Math.cos(theta)*v; //calculate x vector
            double vy   = Math.signum(x)*Math.sin(theta)*v; //calculate y vector
            //Orient a random 2D circular orbit direction
            if (Math.random() <=.5) {
                vx=-vx;
                vy=-vy;
            } 

            double mass = Math.random()*solMass*10+1e20; //random mass
             int img=(int)(Math.random()*6)+1;// random image number, 1-6 inclusive
            bodies.add( new Body(x, y, vx, vy, mass,img));
        }
    }

    public  void mousePressed(MouseEvent e){}

    public  void mouseEntered(MouseEvent e){}

    public  void mouseExited(MouseEvent e){}

    public  void mouseReleased(MouseEvent e){}

    /** //This does not seem to work properly nor is it used currently
     * Checks if two bodies are colliding by calculating their distance and comparing to their radii
     * @param - 2 bodies being checked for collision
     * @return boolean - whether the bodies have collided or not
     */
    private static boolean collide(Body star, Body planet){
        double d=distance(star,planet);
        if(d<20){//one body is intersecting with the other
            return true;
        }
        return false;
    }
}
